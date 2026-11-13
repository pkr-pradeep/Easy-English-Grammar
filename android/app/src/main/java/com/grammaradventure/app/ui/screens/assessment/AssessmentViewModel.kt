package com.grammaradventure.app.ui.screens.assessment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.grammaradventure.app.data.local.datastore.UserPreferencesManager
import com.grammaradventure.app.data.repository.CurriculumRepository
import com.grammaradventure.app.data.repository.LessonRepository
import com.grammaradventure.app.data.repository.ProgressRepository
import com.grammaradventure.app.domain.engine.AssessmentEvaluationResult
import com.grammaradventure.app.domain.engine.AssessmentScorer
import com.grammaradventure.app.domain.engine.AssessmentSession
import com.grammaradventure.app.domain.engine.PublicAssessmentQuestion
import com.grammaradventure.app.domain.engine.ReviewedQuestionResult
import com.grammaradventure.app.domain.engine.WeakSubtopicSummary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AssessmentRunnerUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val chapterTitle: String = "",
    val publicQuestions: List<PublicAssessmentQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<String, Int> = emptyMap(),
    val answeredCount: Int = 0,
    val canSubmit: Boolean = false,
    val isLocked: Boolean = false,
    val elapsedSeconds: Long = 0L,
    val passingThresholdPercent: Int = 70
) {
    val currentQuestion: PublicAssessmentQuestion?
        get() = publicQuestions.getOrNull(currentQuestionIndex)
}

class AssessmentViewModel(
    private val moduleId: String,
    private val lessonRepository: LessonRepository,
    private val curriculumRepository: CurriculumRepository,
    private val progressRepository: ProgressRepository,
    private val preferencesManager: UserPreferencesManager,
    private val assessmentScorer: AssessmentScorer
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssessmentRunnerUiState())
    val uiState: StateFlow<AssessmentRunnerUiState> = _uiState.asStateFlow()

    private var session: AssessmentSession? = null
    private var worldId: String = ""

    init {
        startNewSession()
        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                delay(1_000L)
                val current = _uiState.value
                if (!current.isLoading && !current.isLocked && current.errorMessage == null) {
                    _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1L) }
                }
            }
        }
    }

    private fun startNewSession() {
        viewModelScope.launch {
            val lesson = lessonRepository.getLessonForModule(moduleId)
            if (lesson == null || lesson.assessment.questions.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Assessment questions for $moduleId are not yet available offline."
                    )
                }
                return@launch
            }

            worldId = lesson.worldId
            val prefs = preferencesManager.preferences.first()
            val targetCount = prefs.defaultAssessmentLength
                .coerceAtMost(lesson.assessment.questions.size)
            val passPercent = lesson.assessment.passingPercentage
                .coerceAtLeast(prefs.assessmentPassThreshold)

            val created = assessmentScorer.createSession(
                allQuestions = lesson.assessment.questions,
                requestedCount = targetCount,
                randomize = true
            )
            session = created

            _uiState.update {
                AssessmentRunnerUiState(
                    isLoading = false,
                    chapterTitle = lesson.title,
                    publicQuestions = created.publicQuestions,
                    currentQuestionIndex = 0,
                    selectedAnswers = created.selectedAnswers,
                    answeredCount = created.answeredCount,
                    canSubmit = created.canSubmit,
                    isLocked = created.isLocked,
                    elapsedSeconds = 0L,
                    passingThresholdPercent = passPercent
                )
            }
        }
    }

    fun selectOption(questionId: String, optionIndex: Int) {
        val currentSession = session ?: return
        if (currentSession.isLocked) return
        val updated = currentSession.selectAnswer(questionId, optionIndex)
        session = updated
        _uiState.update {
            it.copy(
                selectedAnswers = updated.selectedAnswers,
                answeredCount = updated.answeredCount,
                canSubmit = updated.canSubmit
            )
        }
    }

    fun goToQuestion(index: Int) {
        val total = _uiState.value.publicQuestions.size
        if (total == 0) return
        _uiState.update { it.copy(currentQuestionIndex = index.coerceIn(0, total - 1)) }
    }

    fun submitAssessment(onSubmitted: (attemptId: Long) -> Unit) {
        val currentSession = session ?: return
        if (!currentSession.canSubmit) return

        viewModelScope.launch {
            val threshold = _uiState.value.passingThresholdPercent
            val elapsed = _uiState.value.elapsedSeconds
            val (lockedSession, evaluation) = assessmentScorer.evaluateAndLock(
                session = currentSession,
                passingThresholdPercent = threshold,
                timeTakenSeconds = elapsed
            )
            session = lockedSession
            _uiState.update { it.copy(isLocked = true, canSubmit = false) }

            val world = curriculumRepository.getWorlds().find { it.id == worldId }
            val attemptId = progressRepository.saveSubmittedAssessment(
                moduleId = moduleId,
                evaluation = evaluation,
                worldForBadgeCheck = world
            )
            onSubmitted(attemptId)
        }
    }

    class Factory(
        private val moduleId: String,
        private val lessonRepository: LessonRepository,
        private val curriculumRepository: CurriculumRepository,
        private val progressRepository: ProgressRepository,
        private val preferencesManager: UserPreferencesManager,
        private val assessmentScorer: AssessmentScorer
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AssessmentViewModel(
                moduleId = moduleId,
                lessonRepository = lessonRepository,
                curriculumRepository = curriculumRepository,
                progressRepository = progressRepository,
                preferencesManager = preferencesManager,
                assessmentScorer = assessmentScorer
            ) as T
        }
    }
}

data class AssessmentResultUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val moduleId: String = "",
    val chapterTitle: String = "",
    val isLocked: Boolean = true,
    val evaluation: AssessmentEvaluationResult? = null
)

class AssessmentResultViewModel(
    private val attemptId: Long,
    private val lessonRepository: LessonRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssessmentResultUiState())
    val uiState: StateFlow<AssessmentResultUiState> = _uiState.asStateFlow()

    init {
        loadResult()
    }

    private fun loadResult() {
        viewModelScope.launch {
            val attempt = progressRepository.getAttemptById(attemptId)
            if (attempt == null) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Assessment attempt not found.")
                }
                return@launch
            }

            val responses = progressRepository.getResponsesForAttempt(attemptId)
            val lesson = lessonRepository.getLessonForModule(attempt.moduleId)
            val questionMap = lesson?.assessment?.questions?.associateBy { it.id }.orEmpty()

            val reviewed = responses.mapIndexed { idx, resp ->
                val q = questionMap[resp.questionId]
                ReviewedQuestionResult(
                    questionId = resp.questionId,
                    number = idx + 1,
                    subtopicId = resp.subtopicId,
                    subtopicTitle = q?.subtopicTitle ?: resp.subtopicId,
                    storyContext = q?.storyContext,
                    questionText = q?.questionText ?: "Question ${idx + 1}",
                    options = q?.options ?: emptyList(),
                    selectedOptionIndex = resp.selectedOptionIndex,
                    correctOptionIndex = resp.correctOptionIndex,
                    isCorrect = resp.isCorrect,
                    explanation = q?.explanation ?: "Explanation unavailable.",
                    distractorRationales = q?.distractorRationales ?: emptyList(),
                    grammarRule = resp.grammarRule,
                    lessonStepRef = q?.lessonStepRef.orEmpty()
                )
            }

            val weakSubtopics = reviewed
                .filter { !it.isCorrect }
                .groupBy { it.subtopicId to it.subtopicTitle }
                .map { (key, items) ->
                    WeakSubtopicSummary(
                        subtopicId = key.first,
                        subtopicTitle = key.second,
                        missedCount = items.size
                    )
                }
                .sortedByDescending { it.missedCount }

            _uiState.update {
                AssessmentResultUiState(
                    isLoading = false,
                    moduleId = attempt.moduleId,
                    chapterTitle = lesson?.title ?: attempt.moduleId,
                    isLocked = attempt.isLocked,
                    evaluation = AssessmentEvaluationResult(
                        totalQuestions = attempt.totalQuestions,
                        correctCount = attempt.correctCount,
                        incorrectCount = (attempt.totalQuestions - attempt.correctCount).coerceAtLeast(0),
                        percentage = attempt.percentage,
                        isPassed = attempt.isPassed,
                        timeTakenSeconds = attempt.timeTakenSeconds,
                        weakSubtopics = weakSubtopics,
                        questionResults = reviewed
                    )
                )
            }
        }
    }

    class Factory(
        private val attemptId: Long,
        private val lessonRepository: LessonRepository,
        private val progressRepository: ProgressRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AssessmentResultViewModel(attemptId, lessonRepository, progressRepository) as T
        }
    }
}
