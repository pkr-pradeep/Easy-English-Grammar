package com.grammaradventure.app.domain.model

import kotlinx.serialization.Serializable

enum class BoardOption(val displayName: String, val subtitle: String) {
    CONSOLIDATED("All Boards (Consolidated)", "Full 25-module Grammar Kingdom sequence"),
    ASSAM("Assam (SEBA)", "A New Method English Grammar — Classes IX–X"),
    PUNJAB("Punjab (PSEB)", "A Practice Book of English Grammar & Composition — Class IX"),
    ODISHA("Odisha (BSE)", "Learn and Practise Grammar — Classes IX & X")
}

@Serializable
data class WorldsCatalog(
    val schemaVersion: Int,
    val worlds: List<GrammarWorld>
)

@Serializable
data class GrammarWorld(
    val id: String,
    val order: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val leadCharacterId: String,
    val accentColorHex: String,
    val badgeTitle: String,
    val moduleIds: List<String>
)

@Serializable
data class ModulesCatalog(
    val schemaVersion: Int,
    val modules: List<GrammarModuleMeta>
)

@Serializable
data class GrammarModuleMeta(
    val id: String,
    val moduleNumber: Int,
    val worldId: String,
    val title: String,
    val summary: String,
    val storyPremise: String,
    val estimatedMinutes: Int,
    val topicStatus: String,
    val lessonStatus: String,
    val isDemoAvailable: Boolean = false,
    val prerequisiteIds: List<String> = emptyList(),
    val applicableClasses: List<Int> = listOf(9, 10),
    val subtopics: List<String> = emptyList(),
    val learningObjectives: List<String> = emptyList(),
    val sourceMappings: List<SourceMapping> = emptyList()
) {
    fun matchesBoardAndClass(board: BoardOption, classLevel: Int): Boolean {
        val classMatches = applicableClasses.contains(classLevel)
        if (board == BoardOption.CONSOLIDATED) return classMatches
        val boardKey = board.name
        return sourceMappings.any { mapping ->
            mapping.board.equals(boardKey, ignoreCase = true) &&
                when (classLevel) {
                    9 -> mapping.classLabel.contains("IX")
                    10 -> mapping.classLabel.contains("X") && mapping.classLabel != "IX"
                    else -> true
                }
        }
    }
}

@Serializable
data class SourceMapping(
    val board: String,
    val classLabel: String,
    val textbookTitle: String,
    val originalChapterNumber: Int? = null,
    val originalChapterTitle: String,
    val sourceTerm: String,
    val pageRange: String = "not_verified",
    val coverageStatus: String
)

@Serializable
data class CompositionCatalog(
    val schemaVersion: Int,
    val sectionTitle: String,
    val note: String,
    val categories: List<CompositionCategory>
)

@Serializable
data class CompositionCategory(
    val id: String,
    val title: String,
    val sourceBoard: String,
    val sourceClass: Int,
    val topics: List<String>,
    val status: String
)
