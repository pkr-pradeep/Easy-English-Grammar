package com.grammaradventure.app.ui.screens.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.grammaradventure.app.data.repository.LessonRepository
import com.grammaradventure.app.data.repository.ProgressRepository
import com.grammaradventure.app.domain.engine.CheckpointEvaluationResult
import com.grammaradventure.app.domain.engine.CheckpointEvaluator
import com.grammaradventure.app.domain.model.InteractiveCheckpointStep
import com.grammaradventure.app.domain.model.LessonContent
import com.grammaradventure.app.domain.model.LessonStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CheckpointInteractionState(
    val selectedOption: String = "",
    val typedAnswer: String = "",
    val arrangedTokens: List<String> = emptyList(),
    val showHint: Boolean = false,
    val lastEvaluation: CheckpointEvaluationResult? = null,
    val xpEarnedThisCheckpoint: Int = 0
)

data class LessonEngineUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val lesson: LessonContent? = null,
    val currentStepIndex: Int = 0,
    val completedCheckpointIds: Set<String> = emptySet(),
    val checkpointStates: Map<String, CheckpointInteractionState> = emptyMap(),
    val isLessonCompleted: Boolean = false,
    val completionBannerXp: Int = 0
) {
    val currentStep: LessonStep?
        get() = lesson?.steps?.getOrNull(currentStepIndex)

    val totalSteps: Int
        get() = lesson?.steps?.size ?: 0

    val canAdvanceFromCurrentStep: Boolean
        get() {
            val step = currentStep ?: return false
            return if (step is InteractiveCheckpointStep) {
                completedCheckpointIds.contains(step.checkpointId)
            } else {
                true
            }
        }
}

class LessonEngineViewModel(
    private val moduleId: String,
    private val initialStepId: String?,
    private val lessonRepository: LessonRepository,
    private val progressRepository: ProgressRepository,
    private val checkpointEvaluator: CheckpointEvaluator
) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonEngineUiState())
    val uiState: StateFlow<LessonEngineUiState> = _uiState.asStateFlow()

    init {
        loadLesson()
        observeCheckpointHistory()
    }

    private fun loadLesson() {
        viewModelScope.launch {
            val lesson = lessonRepository.getLessonForModule(moduleId)
            if (lesson == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Lesson content for $moduleId is not yet available offline."
                    )
                }
                return@launch
            }

            val savedProgress = progressRepository.getModuleProgressOnce(moduleId)
            val startIndex = when {
                !initialStepId.isNullOrBlank() ->
                    lesson.steps.indexOfFirst { it.stepId == initialStepId }.takeIf { it >= 0 } ?: 0
                savedProgress != null && savedProgress.status == "IN_PROGRESS" ->
                    savedProgress.lastStepIndex.coerceIn(0, (lesson.steps.size - 1).coerceAtLeast(0))
                else -> 0
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    lesson = lesson,
                    currentStepIndex = startIndex,
                    isLessonCompleted = savedProgress?.status == "COMPLETED"
                )
            }

            val initialStep = lesson.steps.getOrNull(startIndex)
            if (initialStep != null && savedProgress == null) {
                progressRepository.saveLessonStepProgress(
                    moduleId = moduleId,
                    stepId = initialStep.stepId,
                    stepIndex = startIndex,
                    totalSteps = lesson.steps.size
                )
            }
        }
    }

    private fun observeCheckpointHistory() {
        viewModelScope.launch {
            progressRepository.observeModuleCheckpoints(moduleId).collect { attempts ->
                val passedIds = attempts.filter { it.isCorrect }.map { it.checkpointId }.toSet()
                _uiState.update { it.copy(completedCheckpointIds = passedIds) }
            }
        }
    }

    fun selectOption(checkpointId: String, option: String) {
        updateCheckpointState(checkpointId) {
            it.copy(selectedOption = option, lastEvaluation = null)
        }
    }

    fun updateTypedAnswer(checkpointId: String, value: String) {
        updateCheckpointState(checkpointId) {
            it.copy(typedAnswer = value, lastEvaluation = null)
        }
    }

    fun toggleWordToken(checkpointId: String, token: String) {
        updateCheckpointState(checkpointId) { current ->
            val updated = if (current.arrangedTokens.contains(token)) {
                current.arrangedTokens - token
            } else {
                current.arrangedTokens + token
            }
            current.copy(arrangedTokens = updated, lastEvaluation = null)
        }
    }

    fun clearArrangedTokens(checkpointId: String) {
        updateCheckpointState(checkpointId) {
            it.copy(arrangedTokens = emptyList(), lastEvaluation = null)
        }
    }

    fun toggleHint(checkpointId: String) {
        updateCheckpointState(checkpointId) {
            it.copy(showHint = !it.showHint)
        }
    }

    fun submitCheckpointAnswer(step: InteractiveCheckpointStep) {
        val state = _uiState.value.checkpointStates[step.checkpointId] ?: CheckpointInteractionState()
        val rawAnswer = when (step.interactionType) {
            com.grammaradventure.app.domain.model.InteractionType.FILL_IN_BLANK,
            com.grammaradventure.app.domain.model.InteractionType.SENTENCE_CONVERSION ->
                state.typedAnswer
            com.grammaradventure.app.domain.model.InteractionType.ARRANGE_WORDS ->
                state.arrangedTokens.joinToString(" ")
            com.grammaradventure.app.domain.model.InteractionType.MULTIPLE_CHOICE,
            com.grammaradventure.app.domain.model.InteractionType.ERROR_CORRECTION,
            com.grammaradventure.app.domain.model.InteractionType.IDENTIFY_ROLE ->
                state.selectedOption
        }

        val evaluation = checkpointEvaluator.evaluate(step, rawAnswer)
        updateCheckpointState(step.checkpointId) {
            it.copy(lastEvaluation = evaluation)
        }

        if (evaluation.normalizedSubmittedAnswer.isNotBlank()) {
            viewModelScope.launch {
                val xpAwarded = progressRepository.recordCheckpointAttempt(
                    moduleId = moduleId,
                    checkpointId = step.checkpointId,
                    subtopicId = step.subtopicId,
                    subtopicTitle = step.subtopicTitle,
                    isCorrect = evaluation.isCorrect,
                    selectedAnswer = evaluation.normalizedSubmittedAnswer,
                    xpReward = step.xpReward
                )
                if (xpAwarded > 0) {
                    updateCheckpointState(step.checkpointId) {
                        it.copy(xpEarnedThisCheckpoint = xpAwarded)
                    }
                }
            }
        }
    }

    fun retryCheckpoint(checkpointId: String) {
        updateCheckpointState(checkpointId) {
            it.copy(lastEvaluation = null)
        }
    }

    fun goToPreviousStep() {
        val prevIndex = (_uiState.value.currentStepIndex - 1).coerceAtLeast(0)
        jumpToStep(prevIndex)
    }

    fun advanceToNextStep() {
        val lesson = _uiState.value.lesson ?: return
        val currentIdx = _uiState.value.currentStepIndex
        if (currentIdx < lesson.steps.lastIndex) {
            jumpToStep(currentIdx + 1)
        } else {
            completeLessonStory(lesson)
        }
    }

    fun jumpToStep(index: Int) {
        val lesson = _uiState.value.lesson ?: return
        val clamped = index.coerceIn(0, lesson.steps.lastIndex)
        val step = lesson.steps[clamped]
        _uiState.update { it.copy(currentStepIndex = clamped) }
        viewModelScope.launch {
            progressRepository.saveLessonStepProgress(
                moduleId = moduleId,
                stepId = step.stepId,
                stepIndex = clamped,
                totalSteps = lesson.steps.size
            )
        }
    }

    private fun completeLessonStory(lesson: LessonContent) {
        viewModelScope.launch {
            val awarded = progressRepository.markLessonCompleted(
                moduleId = moduleId,
                lastStepId = lesson.steps.last().stepId,
                totalSteps = lesson.steps.size,
                completionXp = 50
            )
            _uiState.update {
                it.copy(
                    isLessonCompleted = true,
                    completionBannerXp = if (awarded) 50 else 0
                )
            }
        }
    }

    private fun updateCheckpointState(
        checkpointId: String,
        transform: (CheckpointInteractionState) -> CheckpointInteractionState
    ) {
        _uiState.update { current ->
            val prev = current.checkpointStates[checkpointId] ?: CheckpointInteractionState()
            current.copy(
                checkpointStates = current.checkpointStates + (checkpointId to transform(prev))
            )
        }
    }

    class Factory(
        private val moduleId: String,
        private val initialStepId: String?,
        private val lessonRepository: LessonRepository,
        private val progressRepository: ProgressRepository,
        private val checkpointEvaluator: CheckpointEvaluator
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LessonEngineViewModel(
                moduleId = moduleId,
                initialStepId = initialStepId,
                lessonRepository = lessonRepository,
                progressRepository = progressRepository,
                checkpointEvaluator = checkpointEvaluator
            ) as T
        }
    }
}
