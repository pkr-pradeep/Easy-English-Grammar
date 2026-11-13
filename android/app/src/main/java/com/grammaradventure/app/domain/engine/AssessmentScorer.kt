package com.grammaradventure.app.domain.engine

import com.grammaradventure.app.domain.model.AssessmentQuestion
import com.grammaradventure.app.domain.model.QuestionDifficulty
import kotlin.math.roundToInt

/**
 * Sanitized question model exposed to the UI BEFORE assessment submission.
 * Deliberately excludes correctOptionIndex, explanation, and distractorRationales
 * to enforce the deferred answer-reveal rule at the type level.
 */
data class PublicAssessmentQuestion(
    val id: String,
    val number: Int,
    val subtopicTitle: String,
    val difficulty: QuestionDifficulty,
    val storyContext: String?,
    val questionText: String,
    val options: List<String>
)

data class ReviewedQuestionResult(
    val questionId: String,
    val number: Int,
    val subtopicId: String,
    val subtopicTitle: String,
    val storyContext: String?,
    val questionText: String,
    val options: List<String>,
    val selectedOptionIndex: Int,
    val correctOptionIndex: Int,
    val isCorrect: Boolean,
    val explanation: String,
    val distractorRationales: List<String>,
    val grammarRule: String,
    val lessonStepRef: String
)

data class WeakSubtopicSummary(
    val subtopicId: String,
    val subtopicTitle: String,
    val missedCount: Int
)

data class AssessmentEvaluationResult(
    val totalQuestions: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val percentage: Int,
    val isPassed: Boolean,
    val timeTakenSeconds: Long,
    val weakSubtopics: List<WeakSubtopicSummary>,
    val questionResults: List<ReviewedQuestionResult>
)

data class AssessmentSession(
    val questions: List<AssessmentQuestion>,
    val selectedAnswers: Map<String, Int> = emptyMap(),
    val isLocked: Boolean = false
) {
    val publicQuestions: List<PublicAssessmentQuestion>
        get() = questions.mapIndexed { index, q ->
            PublicAssessmentQuestion(
                id = q.id,
                number = index + 1,
                subtopicTitle = q.subtopicTitle,
                difficulty = q.difficulty,
                storyContext = q.storyContext,
                questionText = q.questionText,
                options = q.options
            )
        }

    val answeredCount: Int
        get() = questions.count { selectedAnswers.containsKey(it.id) }

    val canSubmit: Boolean
        get() = !isLocked && questions.isNotEmpty() && answeredCount == questions.size

    fun selectAnswer(questionId: String, optionIndex: Int): AssessmentSession {
        if (isLocked) return this
        val target = questions.find { it.id == questionId } ?: return this
        if (optionIndex !in target.options.indices) return this
        return copy(selectedAnswers = selectedAnswers + (questionId to optionIndex))
    }
}

class AssessmentScorer {

    fun createSession(
        allQuestions: List<AssessmentQuestion>,
        requestedCount: Int = 15,
        randomize: Boolean = true
    ): AssessmentSession {
        val pool = if (randomize) allQuestions.shuffled() else allQuestions
        val selected = pool.take(requestedCount.coerceAtLeast(1).coerceAtMost(pool.size))
        return AssessmentSession(questions = selected)
    }

    fun evaluateAndLock(
        session: AssessmentSession,
        passingThresholdPercent: Int,
        timeTakenSeconds: Long
    ): Pair<AssessmentSession, AssessmentEvaluationResult> {
        require(session.canSubmit) {
            "Cannot submit assessment until all questions are answered and session is unlocked."
        }

        val reviewed = session.questions.mapIndexed { index, q ->
            val selectedIdx = session.selectedAnswers[q.id] ?: -1
            val isCorrect = selectedIdx == q.correctOptionIndex
            ReviewedQuestionResult(
                questionId = q.id,
                number = index + 1,
                subtopicId = q.subtopicId,
                subtopicTitle = q.subtopicTitle,
                storyContext = q.storyContext,
                questionText = q.questionText,
                options = q.options,
                selectedOptionIndex = selectedIdx,
                correctOptionIndex = q.correctOptionIndex,
                isCorrect = isCorrect,
                explanation = q.explanation,
                distractorRationales = q.distractorRationales,
                grammarRule = q.grammarRule,
                lessonStepRef = q.lessonStepRef
            )
        }

        val total = reviewed.size
        val correctCount = reviewed.count { it.isCorrect }
        val incorrectCount = total - correctCount
        val percentage = if (total > 0) ((correctCount * 100.0) / total).roundToInt() else 0
        val isPassed = percentage >= passingThresholdPercent

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

        val lockedSession = session.copy(isLocked = true)
        val result = AssessmentEvaluationResult(
            totalQuestions = total,
            correctCount = correctCount,
            incorrectCount = incorrectCount,
            percentage = percentage,
            isPassed = isPassed,
            timeTakenSeconds = timeTakenSeconds.coerceAtLeast(0L),
            weakSubtopics = weakSubtopics,
            questionResults = reviewed
        )
        return lockedSession to result
    }
}
