package com.grammaradventure.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.grammaradventure.app.data.local.db.WeakConceptEntity
import com.grammaradventure.app.data.repository.CurriculumRepository
import com.grammaradventure.app.data.repository.ProgressRepository
import com.grammaradventure.app.domain.model.BoardOption
import com.grammaradventure.app.domain.model.GrammarModuleMeta
import com.grammaradventure.app.domain.model.GrammarWorld
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WorldProgressSummary(
    val world: GrammarWorld,
    val completedModules: Int,
    val totalModules: Int
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val learnerName: String = "Grammar Explorer",
    val selectedClass: Int = 9,
    val selectedBoard: BoardOption = BoardOption.CONSOLIDATED,
    val xp: Int = 0,
    val streakDays: Int = 0,
    val completedChapters: Int = 0,
    val totalChapters: Int = 25,
    val continueModule: GrammarModuleMeta? = null,
    val continueStepLabel: String = "",
    val demoModule: GrammarModuleMeta? = null,
    val worldSummaries: List<WorldProgressSummary> = emptyList(),
    val weakConcepts: List<WeakConceptEntity> = emptyList(),
    val dailyPromptQuestion: String = "Spot the tense shift: \"Yesterday Chikoo eat three mangoes before Acharya arrived.\""
)

class HomeViewModel(
    private val curriculumRepository: CurriculumRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val catalogFlow = MutableStateFlow<Pair<List<GrammarWorld>, List<GrammarModuleMeta>>>(
        emptyList<GrammarWorld>() to emptyList()
    )

    init {
        viewModelScope.launch {
            val worlds = curriculumRepository.getWorlds()
            val modules = curriculumRepository.getModules()
            catalogFlow.value = worlds to modules
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        catalogFlow,
        progressRepository.observeProfile(),
        progressRepository.observeAllLessonProgress(),
        progressRepository.observeLatestInProgress(),
        progressRepository.observeActiveWeakConcepts()
    ) { (worlds, modules), profile, allProgress, latestInProgress, weakConcepts ->
        val modulesById = modules.associateBy { it.id }
        val completedIds = allProgress.filter { it.status == "COMPLETED" }.map { it.moduleId }.toSet()

        val board = profile?.selectedBoard?.let { raw ->
            BoardOption.entries.find { it.name == raw }
        } ?: BoardOption.CONSOLIDATED

        val continueMod = latestInProgress?.let { modulesById[it.moduleId] }
            ?: modules.firstOrNull { !completedIds.contains(it.id) }
            ?: modules.firstOrNull { it.isDemoAvailable }
            ?: modules.firstOrNull()

        val continueStepText = when {
            latestInProgress != null && latestInProgress.totalSteps > 0 ->
                "Resume at step ${latestInProgress.lastStepIndex + 1} of ${latestInProgress.totalSteps}"
            continueMod != null ->
                "Recommended next chapter • Module ${continueMod.moduleNumber}"
            else -> "Start your first Grammar Kingdom story"
        }

        val worldSummaries = worlds.map { world ->
            val worldModules = modules.filter { it.worldId == world.id }
            val completedInWorld = worldModules.count { completedIds.contains(it.id) }
            WorldProgressSummary(
                world = world,
                completedModules = completedInWorld,
                totalModules = worldModules.size
            )
        }

        HomeUiState(
            isLoading = worlds.isEmpty(),
            learnerName = profile?.name ?: "Grammar Explorer",
            selectedClass = profile?.selectedClass ?: 9,
            selectedBoard = board,
            xp = profile?.xp ?: 0,
            streakDays = profile?.currentStreak ?: 0,
            completedChapters = completedIds.size,
            totalChapters = modules.size.coerceAtLeast(25),
            continueModule = continueMod,
            continueStepLabel = continueStepText,
            demoModule = modules.find { it.isDemoAvailable },
            worldSummaries = worldSummaries,
            weakConcepts = weakConcepts.take(3)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    class Factory(
        private val curriculumRepository: CurriculumRepository,
        private val progressRepository: ProgressRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(curriculumRepository, progressRepository) as T
        }
    }
}
