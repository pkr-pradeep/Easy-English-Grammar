package com.grammaradventure.app.domain.engine

import com.grammaradventure.app.domain.model.AssessmentQuestion
import com.grammaradventure.app.domain.model.QuestionDifficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AssessmentScorerTest {

    private val scorer = AssessmentScorer()

    private fun sampleQuestions(): List<AssessmentQuestion> = listOf(
        AssessmentQuestion(
            id = "Q1",
            subtopicId = "time_vs_tense",
            subtopicTitle = "Time vs Tense",
            difficulty = QuestionDifficulty.EASY,
            questionText = "Our train leaves at 6 a.m. tomorrow.",
            options = listOf("Present → Future", "Past → Past", "Future → Past", "Present → Past"),
            correctOptionIndex = 0,
            explanation = "Simple Present for timetable.",
            distractorRationales = listOf("Correct", "Wrong", "Wrong", "Wrong"),
            grammarRule = "Timetable rule",
            lessonStepRef = "step_02"
        ),
        AssessmentQuestion(
            id = "Q2",
            subtopicId = "past_perfect",
            subtopicTitle = "Past Perfect",
            difficulty = QuestionDifficulty.MEDIUM,
            questionText = "He ______ before we arrived.",
            options = listOf("left", "has left", "had left", "leaves"),
            correctOptionIndex = 2,
            explanation = "Earlier of two past events uses had + V3.",
            distractorRationales = listOf("Wrong", "Wrong", "Correct", "Wrong"),
            grammarRule = "Past Perfect sequence",
            lessonStepRef = "step_05"
        ),
        AssessmentQuestion(
            id = "Q3",
            subtopicId = "stative_verbs",
            subtopicTitle = "Stative Verbs",
            difficulty = QuestionDifficulty.HARD,
            questionText = "This crown ______ to the king.",
            options = listOf("is belonging", "belongs", "was belonging", "belonging"),
            correctOptionIndex = 1,
            explanation = "Belong is stative.",
            distractorRationales = listOf("Wrong", "Correct", "Wrong", "Wrong"),
            grammarRule = "Stative verbs avoid -ing",
            lessonStepRef = "step_10"
        )
    )

    @Test
    fun `public questions never contain correct index or explanation and submission requires all answers`() {
        var session = scorer.createSession(sampleQuestions(), requestedCount = 3, randomize = false)
        assertEquals(3, session.publicQuestions.size)
        assertFalse(session.canSubmit)

        session = session.selectAnswer("Q1", 0)
        session = session.selectAnswer("Q2", 2)
        assertFalse(session.canSubmit)

        session = session.selectAnswer("Q3", 1)
        assertTrue(session.canSubmit)
    }

    @Test
    fun `scoring calculates accurate percentage, extracts weak topics, and locks attempt`() {
        var session = scorer.createSession(sampleQuestions(), requestedCount = 3, randomize = false)
        session = session.selectAnswer("Q1", 0) // correct
        session = session.selectAnswer("Q2", 0) // wrong (subtopic: past_perfect)
        session = session.selectAnswer("Q3", 1) // correct

        val (lockedSession, result) = scorer.evaluateAndLock(
            session = session,
            passingThresholdPercent = 70,
            timeTakenSeconds = 95L
        )

        assertTrue(lockedSession.isLocked)
        assertEquals(3, result.totalQuestions)
        assertEquals(2, result.correctCount)
        assertEquals(1, result.incorrectCount)
        assertEquals(67, result.percentage)
        assertFalse(result.isPassed)
        assertEquals(1, result.weakSubtopics.size)
        assertEquals("past_perfect", result.weakSubtopics.first().subtopicId)

        // Verify locked session rejects subsequent edits
        val afterLockAttempt = lockedSession.selectAnswer("Q2", 2)
        assertEquals(0, afterLockAttempt.selectedAnswers["Q2"])
    }
}
