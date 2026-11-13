package com.grammaradventure.app.ui.screens.worlds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.grammaradventure.app.data.local.datastore.UserPreferencesManager
import com.grammaradventure.app.data.local.db.LessonProgressEntity
import com.grammaradventure.app.data.repository.CurriculumRepository
import com.grammaradventure.app.data.repository.ProgressRepository
import com.grammaradventure.app.domain.engine.PrerequisiteChecker
import com.grammaradventure.app.domain.engine.PrerequisiteEvaluation
import com.grammaradventure.app.domain.model.BoardOption
import com.grammaradventure.app.domain.model.CompositionCatalog
import com.grammaradventure.app.domain.model.GrammarCharacter
import com.grammaradventure.app.domain.model.GrammarModuleMeta
import com.grammaradventure.app.domain.model.GrammarWorld
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChapterListItemUi(
    val module: GrammarModuleMeta,
    val progress: LessonProgressEntity?,
    val prerequisiteEvaluation: PrerequisiteEvaluation,
    val matchesSelectedSyllabus: Boolean
)

data class CurriculumUiState(
    val isLoading: Boolean = true,
    val selectedClass: Int = 9,
    val selectedBoard: BoardOption = BoardOption.CONSOLIDATED,
    val strictPrerequisites: Boolean = false,
    val filterBySyllabusOnly: Boolean = false,
    val worlds: List<GrammarWorld> = emptyList(),
    val allModules: List<GrammarModuleMeta> = emptyList(),
    val characters: List<GrammarCharacter> = emptyList(),
    val compositionCatalog: CompositionCatalog? = null,
    val progressByModuleId: Map<String, LessonProgressEntity> = emptyMap(),
    val completedModuleIds: Set<String> = emptySet()
) {
    fun chaptersForWorld(worldId: String, prerequisiteChecker: PrerequisiteChecker): List<ChapterListItemUi> {
        val byId = allModules.associateBy { it.id }
        return allModules
            .filter { it.worldId == worldId }
            .map { mod ->
                ChapterListItemUi(
                    module = mod,
                    progress = progressByModuleId[mod.id],
                    prerequisiteEvaluation = prerequisiteChecker.evaluate(
                        module = mod,
                        allModulesById = byId,
                        completedModuleIds = completedModuleIds,
                        strictMode = strictPrerequisites
                    ),
                    matchesSelectedSyllabus = mod.matchesBoardAndClass(selectedBoard, selectedClass)
                )
            }
            .filter { !filterBySyllabusOnly || it.matchesSelectedSyllabus }
    }

    fun chapterDetail(moduleId: String, prerequisiteChecker: PrerequisiteChecker): ChapterListItemUi? {
        val byId = allModules.associateBy { it.id }
        val mod = byId[moduleId] ?: return null
        return ChapterListItemUi(
            module = mod,
            progress = progressByModuleId[mod.id],
            prerequisiteEvaluation = prerequisiteChecker.evaluate(
                module = mod,
                allModulesById = byId,
                completedModuleIds = completedModuleIds,
                strictMode = strictPrerequisites
            ),
            matchesSelectedSyllabus = mod.matchesBoardAndClass(selectedBoard, selectedClass)
        )
    }
}

class CurriculumViewModel(
    private val curriculumRepository: CurriculumRepository,
    private val progressRepository: ProgressRepository,
    private val preferencesManager: UserPreferencesManager,
    val prerequisiteChecker: PrerequisiteChecker
) : ViewModel() {

    private val filterSyllabusOnly = MutableStateFlow(false)
    private val loadedData = MutableStateFlow<LoadedCurriculumData?>(null)

    private data class LoadedCurriculumData(
        val worlds: List<GrammarWorld>,
        val modules: List<GrammarModuleMeta>,
        val characters: List<GrammarCharacter>,
        val composition: CompositionCatalog
    )

    init {
        viewModelScope.launch {
            loadedData.value = LoadedCurriculumData(
                worlds = curriculumRepository.getWorlds(),
                modules = curriculumRepository.getModules(),
                characters = curriculumRepository.getCharacters(),
                composition = curriculumRepository.getCompositionCatalog()
            )
        }
    }

    val uiState: StateFlow<CurriculumUiState> = combine(
        loadedData,
        progressRepository.observeProfile(),
        progressRepository.observeAllLessonProgress(),
        preferencesManager.preferences,
        filterSyllabusOnly
    ) { catalog, profile, progressList, prefs, syllabusOnly ->
        val board = profile?.selectedBoard?.let { raw ->
            BoardOption.entries.find { it.name == raw }
        } ?: BoardOption.CONSOLIDATED

        val progressMap = progressList.associateBy { it.moduleId }
        val completedIds = progressList.filter { it.status == "COMPLETED" }.map { it.moduleId }.toSet()

        CurriculumUiState(
            isLoading = catalog == null,
            selectedClass = profile?.selectedClass ?: 9,
            selectedBoard = board,
            strictPrerequisites = prefs.strictPrerequisites,
            filterBySyllabusOnly = syllabusOnly,
            worlds = catalog?.worlds.orEmpty(),
            allModules = catalog?.modules.orEmpty(),
            characters = catalog?.characters.orEmpty(),
            compositionCatalog = catalog?.composition,
            progressByModuleId = progressMap,
            completedModuleIds = completedIds
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CurriculumUiState()
    )

    fun setFilterBySyllabusOnly(enabled: Boolean) {
        filterSyllabusOnly.value = enabled
    }

    class Factory(
        private val curriculumRepository: CurriculumRepository,
        private val progressRepository: ProgressRepository,
        private val preferencesManager: UserPreferencesManager,
        private val prerequisiteChecker: PrerequisiteChecker
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CurriculumViewModel(
                curriculumRepository,
                progressRepository,
                preferencesManager,
                prerequisiteChecker
            ) as T
        }
    }
}
