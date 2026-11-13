package com.grammaradventure.app.domain.engine

import com.grammaradventure.app.domain.model.InteractionType
import com.grammaradventure.app.domain.model.InteractiveCheckpointStep
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckpointEvaluatorTest {

    private val evaluator = CheckpointEvaluator()

    @Test
    fun `multiple choice gives targeted feedback for Chikoo distractor`() {
        val step = InteractiveCheckpointStep(
            stepId = "cp1",
            stepTitle = "Past Rescue",
            checkpointId = "MOD13_CP1",
            subtopicId = "past_simple",
            subtopicTitle = "Simple Past Tense",
            interactionType = InteractionType.MULTIPLE_CHOICE,
            storyPrompt = "Yesterday the thief entered the palace.",
            questionPrompt = "What did the thief do yesterday?",
            options = listOf("He steals the crown.", "He stole the crown.", "He stealing the crown."),
            acceptedAnswers = listOf("He stole the crown."),
            hint = "Look at the time word 'yesterday'.",
            feedbackCorrect = "Correct!",
            feedbackWrongDefault = "Check the time word.",
            feedbackWrongByOption = mapOf(
                "He steals the crown." to "'Steals' is present tense, but 'yesterday' is in the past."
            ),
            simplerExample = "Yesterday I ate an apple."
        )

        val wrong = evaluator.evaluate(step, "He steals the crown.")
        assertFalse(wrong.isCorrect)
        assertTrue(wrong.feedbackMessage.contains("yesterday"))
        assertNotNull(wrong.simplerExample)

        val right = evaluator.evaluate(step, "He stole the crown.")
        assertTrue(right.isCorrect)
        assertNull(right.simplerExample)
    }

    @Test
    fun `fill in blank accepts case-insensitive trimmed answer`() {
        val step = InteractiveCheckpointStep(
            stepId = "cp2",
            stepTitle = "Before Guards Arrived",
            checkpointId = "MOD13_CP2",
            subtopicId = "past_perfect",
            subtopicTitle = "Past Perfect",
            interactionType = InteractionType.FILL_IN_BLANK,
            storyPrompt = "Sequence of two past actions",
            questionPrompt = "The thief ______ the crown before the guards arrived.",
            acceptedAnswers = listOf("had stolen"),
            hint = "Use had + past participle.",
            feedbackCorrect = "Well done!",
            feedbackWrongDefault = "Try Past Perfect.",
            simplerExample = "The train had left before we reached."
        )

        val result = evaluator.evaluate(step, "  Had   Stolen ")
        assertTrue(result.isCorrect)
    }

    @Test
    fun `arrange words ignores trailing period variance`() {
        val step = InteractiveCheckpointStep(
            stepId = "cp4",
            stepTitle = "Word Order",
            checkpointId = "MOD13_CP4",
            subtopicId = "present_perfect_progressive",
            subtopicTitle = "Present Perfect Progressive",
            interactionType = InteractionType.ARRANGE_WORDS,
            storyPrompt = "Ongoing investigation",
            questionPrompt = "Arrange the words:",
            wordTokens = listOf("since morning", "has been checking", "Detective Ira", "the clues"),
            acceptedAnswers = listOf("Detective Ira has been checking the clues since morning."),
            hint = "Subject first, then 'has been checking'.",
            feedbackCorrect = "Great!",
            feedbackWrongDefault = "Check subject-verb-object-time order.",
            simplerExample = "I have been reading since 5 p.m."
        )

        val result = evaluator.evaluate(step, "Detective Ira has been checking the clues since morning")
        assertTrue(result.isCorrect)
    }
}
