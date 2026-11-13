package com.grammaradventure.app.domain.engine

import com.grammaradventure.app.domain.model.InteractionType
import com.grammaradventure.app.domain.model.InteractiveCheckpointStep

data class CheckpointEvaluationResult(
    val isCorrect: Boolean,
    val normalizedSubmittedAnswer: String,
    val feedbackMessage: String,
    val simplerExample: String?
)

class CheckpointEvaluator {

    fun evaluate(
        checkpoint: InteractiveCheckpointStep,
        rawAnswer: String
    ): CheckpointEvaluationResult {
        val cleanedInput = normalizeAnswer(rawAnswer, checkpoint.interactionType)
        if (cleanedInput.isBlank()) {
            return CheckpointEvaluationResult(
                isCorrect = false,
                normalizedSubmittedAnswer = "",
                feedbackMessage = "Please choose or enter an answer before checking.",
                simplerExample = null
            )
        }

        val isCorrect = checkpoint.acceptedAnswers.any { accepted ->
            normalizeAnswer(accepted, checkpoint.interactionType)
                .equals(cleanedInput, ignoreCase = true)
        }

        if (isCorrect) {
            return CheckpointEvaluationResult(
                isCorrect = true,
                normalizedSubmittedAnswer = rawAnswer.trim(),
                feedbackMessage = checkpoint.feedbackCorrect,
                simplerExample = null
            )
        }

        val specificFeedback = checkpoint.feedbackWrongByOption.entries.firstOrNull { (key, _) ->
            normalizeAnswer(key, checkpoint.interactionType)
                .equals(cleanedInput, ignoreCase = true)
        }?.value ?: checkpoint.feedbackWrongDefault

        return CheckpointEvaluationResult(
            isCorrect = false,
            normalizedSubmittedAnswer = rawAnswer.trim(),
            feedbackMessage = specificFeedback,
            simplerExample = checkpoint.simplerExample
        )
    }

    private fun normalizeAnswer(raw: String, type: InteractionType): String {
        val collapsedWhitespace = raw.trim().replace(Regex("\\s+"), " ")
        return when (type) {
            InteractionType.ARRANGE_WORDS,
            InteractionType.FILL_IN_BLANK,
            InteractionType.SENTENCE_CONVERSION ->
                collapsedWhitespace.trimEnd('.', '!', '?')
            InteractionType.MULTIPLE_CHOICE,
            InteractionType.ERROR_CORRECTION,
            InteractionType.IDENTIFY_ROLE ->
                collapsedWhitespace
        }
    }
}
