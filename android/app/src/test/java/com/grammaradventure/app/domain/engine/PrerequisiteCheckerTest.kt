package com.grammaradventure.app.domain.engine

import com.grammaradventure.app.domain.model.BoardOption
import com.grammaradventure.app.domain.model.GrammarModuleMeta
import com.grammaradventure.app.domain.model.SourceMapping
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PrerequisiteCheckerTest {

    private val checker = PrerequisiteChecker()

    private val mod10 = GrammarModuleMeta(
        id = "MOD_10",
        moduleNumber = 10,
        worldId = "WORLD_3",
        title = "Verbs and Verb Groups",
        summary = "Verbs",
        storyPremise = "Story",
        estimatedMinutes = 15,
        topicStatus = "source_specific",
        lessonStatus = "planned",
        prerequisiteIds = emptyList(),
        applicableClasses = listOf(9),
        sourceMappings = listOf(
            SourceMapping(
                board = "ODISHA",
                classLabel = "IX",
                textbookTitle = "Learn and Practise Grammar",
                originalChapterNumber = 2,
                originalChapterTitle = "Verbs",
                sourceTerm = "Verbs",
                coverageStatus = "source_specific"
            )
        )
    )

    private val mod13Demo = GrammarModuleMeta(
        id = "MOD_13",
        moduleNumber = 13,
        worldId = "WORLD_3",
        title = "Tense, Time and Aspect",
        summary = "Tenses",
        storyPremise = "The Great Tense Robbery",
        estimatedMinutes = 25,
        topicStatus = "shared",
        lessonStatus = "planned",
        isDemoAvailable = true,
        prerequisiteIds = listOf("MOD_10"),
        applicableClasses = listOf(9, 10),
        sourceMappings = listOf(
            SourceMapping(
                board = "ODISHA",
                classLabel = "X",
                textbookTitle = "Learn and Practise Grammar",
                originalChapterNumber = 1,
                originalChapterTitle = "Tense Revisited",
                sourceTerm = "Tense Revisited",
                coverageStatus = "shared"
            )
        )
    )

    private val mod14 = GrammarModuleMeta(
        id = "MOD_14",
        moduleNumber = 14,
        worldId = "WORLD_3",
        title = "Subject-Verb Agreement",
        summary = "SVA",
        storyPremise = "Courtroom",
        estimatedMinutes = 20,
        topicStatus = "shared",
        lessonStatus = "planned",
        isDemoAvailable = false,
        prerequisiteIds = listOf("MOD_13"),
        applicableClasses = listOf(9, 10)
    )

    private val modulesById = listOf(mod10, mod13Demo, mod14).associateBy { it.id }

    @Test
    fun `missing prerequisite warns in advisory mode without hard locking`() {
        val result = checker.evaluate(
            module = mod14,
            allModulesById = modulesById,
            completedModuleIds = emptySet(),
            strictMode = false
        )
        assertFalse(result.isSatisfied)
        assertFalse(result.isHardLocked)
        assertEquals(1, result.missingPrerequisites.size)
        assertEquals("MOD_13", result.missingPrerequisites.first().id)
    }

    @Test
    fun `strict mode locks non-demo chapter when prerequisites are incomplete`() {
        val result = checker.evaluate(
            module = mod14,
            allModulesById = modulesById,
            completedModuleIds = emptySet(),
            strictMode = true
        )
        assertFalse(result.isSatisfied)
        assertTrue(result.isHardLocked)
    }

    @Test
    fun `demo chapter stays unlocked even in strict prerequisite mode`() {
        val result = checker.evaluate(
            module = mod13Demo,
            allModulesById = modulesById,
            completedModuleIds = emptySet(),
            strictMode = true
        )
        assertFalse(result.isSatisfied)
        assertFalse(result.isHardLocked)
    }

    @Test
    fun `board and class matching respects Odisha IX vs Odisha X`() {
        assertTrue(mod10.matchesBoardAndClass(BoardOption.ODISHA, 9))
        assertFalse(mod10.matchesBoardAndClass(BoardOption.ODISHA, 10))
        assertTrue(mod13Demo.matchesBoardAndClass(BoardOption.ODISHA, 10))
    }
}
