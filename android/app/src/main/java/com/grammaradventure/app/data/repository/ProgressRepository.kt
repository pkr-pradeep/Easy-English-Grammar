package com.grammaradventure.app.data.repository

import com.grammaradventure.app.data.local.db.AppDatabase
import com.grammaradventure.app.data.local.db.AssessmentAttemptEntity
import com.grammaradventure.app.data.local.db.AssessmentQuestionResponseEntity
import com.grammaradventure.app.data.local.db.CheckpointAttemptEntity
import com.grammaradventure.app.data.local.db.EarnedBadgeEntity
import com.grammaradventure.app.data.local.db.LearnerProfileEntity
import com.grammaradventure.app.data.local.db.LessonProgressEntity
import com.grammaradventure.app.data.local.db.WeakConceptEntity
import com.grammaradventure.app.domain.engine.AssessmentEvaluationResult
import com.grammaradventure.app.domain.model.BoardOption
import com.grammaradventure.app.domain.model.GrammarWorld
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class ProgressRepository(private val db: AppDatabase) {

    fun observeProfile(): Flow<LearnerProfileEntity?> = db.learnerProfileDao().observeProfile()

    fun observeAllLessonProgress(): Flow<List<LessonProgressEntity>> =
        db.lessonProgressDao().observeAllProgress()

    fun observeModuleProgress(moduleId: String): Flow<LessonProgressEntity?> =
        db.lessonProgressDao().observeModuleProgress(moduleId)

    suspend fun getModuleProgressOnce(moduleId: String): LessonProgressEntity? =
        db.lessonProgressDao().getProgressOnce(moduleId)

    fun observeLatestInProgress(): Flow<LessonProgressEntity?> =
        db.lessonProgressDao().observeLatestInProgress()

    fun observeModuleCheckpoints(moduleId: String): Flow<List<CheckpointAttemptEntity>> =
        db.checkpointAttemptDao().observeModuleCheckpoints(moduleId)

    fun observeAllAttempts(): Flow<List<AssessmentAttemptEntity>> =
        db.assessmentDao().observeAllAttempts()

    fun observeActiveWeakConcepts(): Flow<List<WeakConceptEntity>> =
        db.weakConceptDao().observeActiveWeakConcepts()

    fun observeEarnedBadges(): Flow<List<EarnedBadgeEntity>> =
        db.earnedBadgeDao().observeBadges()

    suspend fun saveProfileSetup(
        name: String,
        selectedClass: Int,
        board: BoardOption,
        avatarId: String = "ACHARYA_VIDYADHAR"
    ) {
        val existing = db.learnerProfileDao().getProfileOnce()
        val trimmed = name.trim().ifEmpty { "Grammar Explorer" }
        val updated = existing?.copy(
            name = trimmed,
            selectedClass = selectedClass,
            selectedBoard = board.name,
            avatarId = avatarId
        ) ?: LearnerProfileEntity(
            id = 1,
            name = trimmed,
            avatarId = avatarId,
            selectedClass = selectedClass,
            selectedBoard = board.name,
            xp = 0,
            currentStreak = 0,
            lastActiveEpochDay = 0L
        )
        db.learnerProfileDao().upsertProfile(updated)
    }

    suspend fun saveLessonStepProgress(
        moduleId: String,
        stepId: String,
        stepIndex: Int,
        totalSteps: Int
    ) {
        val existing = db.lessonProgressDao().getProgressOnce(moduleId)
        val preservedStatus = if (existing?.status == "COMPLETED") "COMPLETED" else "IN_PROGRESS"
        val updated = LessonProgressEntity(
            moduleId = moduleId,
            lastCompletedStepId = stepId,
            lastStepIndex = stepIndex,
            totalSteps = totalSteps,
            status = preservedStatus,
            completedAt = existing?.completedAt,
            updatedAt = System.currentTimeMillis()
        )
        db.lessonProgressDao().upsertProgress(updated)
    }

    suspend fun markLessonCompleted(
        moduleId: String,
        lastStepId: String,
        totalSteps: Int,
        completionXp: Int = 50
    ): Boolean {
        val existing = db.lessonProgressDao().getProgressOnce(moduleId)
        val alreadyCompleted = existing?.status == "COMPLETED"
        val updated = LessonProgressEntity(
            moduleId = moduleId,
            lastCompletedStepId = lastStepId,
            lastStepIndex = (totalSteps - 1).coerceAtLeast(0),
            totalSteps = totalSteps,
            status = "COMPLETED",
            completedAt = existing?.completedAt ?: System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        db.lessonProgressDao().upsertProgress(updated)

        if (!alreadyCompleted) {
            awardXpAndUpdateStreak(completionXp)
        }
        return !alreadyCompleted
    }

    suspend fun recordCheckpointAttempt(
        moduleId: String,
        checkpointId: String,
        subtopicId: String,
        subtopicTitle: String,
        isCorrect: Boolean,
        selectedAnswer: String,
        xpReward: Int
    ): Int {
        val priorAttempts = db.checkpointAttemptDao().countTotalAttempts(checkpointId)
        val priorCorrect = db.checkpointAttemptDao().countCorrectAttempts(checkpointId)

        db.checkpointAttemptDao().insertAttempt(
            CheckpointAttemptEntity(
                moduleId = moduleId,
                checkpointId = checkpointId,
                subtopicId = subtopicId,
                attemptCount = priorAttempts + 1,
                isCorrect = isCorrect,
                selectedAnswer = selectedAnswer
            )
        )

        if (!isCorrect) {
            val existingWeak = db.weakConceptDao().getById(subtopicId)
            db.weakConceptDao().upsertWeakConcept(
                WeakConceptEntity(
                    subtopicId = subtopicId,
                    moduleId = moduleId,
                    subtopicTitle = subtopicTitle,
                    errorCount = (existingWeak?.errorCount ?: 0) + 1,
                    lastMissedAt = System.currentTimeMillis(),
                    revisionCompletedCount = existingWeak?.revisionCompletedCount ?: 0,
                    isResolved = false
                )
            )
            return 0
        }

        val xpAwarded = if (priorCorrect == 0) xpReward else 0
        if (xpAwarded > 0) {
            awardXpAndUpdateStreak(xpAwarded)
        } else {
            touchStreakOnly()
        }
        return xpAwarded
    }

    suspend fun saveSubmittedAssessment(
        moduleId: String,
        evaluation: AssessmentEvaluationResult,
        worldForBadgeCheck: GrammarWorld?
    ): Long {
        val priorPasses = db.assessmentDao().countPassedAttemptsForModule(moduleId)

        val attemptId = db.assessmentDao().insertAttempt(
            AssessmentAttemptEntity(
                moduleId = moduleId,
                totalQuestions = evaluation.totalQuestions,
                correctCount = evaluation.correctCount,
                percentage = evaluation.percentage,
                timeTakenSeconds = evaluation.timeTakenSeconds,
                isPassed = evaluation.isPassed,
                isLocked = true
            )
        )

        val responseEntities = evaluation.questionResults.map { q ->
            AssessmentQuestionResponseEntity(
                attemptId = attemptId,
                questionId = q.questionId,
                subtopicId = q.subtopicId,
                selectedOptionIndex = q.selectedOptionIndex,
                correctOptionIndex = q.correctOptionIndex,
                isCorrect = q.isCorrect,
                grammarRule = q.grammarRule
            )
        }
        db.assessmentDao().insertResponses(responseEntities)

        // Update weak concepts: increment on errors, mark resolved if mastered in a passed attempt
        val missedSubtopicIds = evaluation.questionResults.filter { !it.isCorrect }.map { it.subtopicId }.toSet()
        evaluation.questionResults.forEach { q ->
            val existingWeak = db.weakConceptDao().getById(q.subtopicId)
            if (!q.isCorrect) {
                db.weakConceptDao().upsertWeakConcept(
                    WeakConceptEntity(
                        subtopicId = q.subtopicId,
                        moduleId = moduleId,
                        subtopicTitle = q.subtopicTitle,
                        errorCount = (existingWeak?.errorCount ?: 0) + 1,
                        lastMissedAt = System.currentTimeMillis(),
                        revisionCompletedCount = existingWeak?.revisionCompletedCount ?: 0,
                        isResolved = false
                    )
                )
            } else if (existingWeak != null && evaluation.isPassed && !missedSubtopicIds.contains(q.subtopicId)) {
                db.weakConceptDao().upsertWeakConcept(
                    existingWeak.copy(
                        revisionCompletedCount = existingWeak.revisionCompletedCount + 1,
                        isResolved = true
                    )
                )
            }
        }

        // Award mastery XP once per module pass; otherwise touch streak for genuine assessment effort
        if (evaluation.isPassed && priorPasses == 0) {
            awardXpAndUpdateStreak(75)
        } else {
            touchStreakOnly()
        }

        // Check world badge unlock if every module in this world has been passed
        if (evaluation.isPassed && worldForBadgeCheck != null && worldForBadgeCheck.moduleIds.isNotEmpty()) {
            val passedModules = db.assessmentDao().getPassedModuleIds().toSet()
            if (worldForBadgeCheck.moduleIds.all { passedModules.contains(it) }) {
                db.earnedBadgeDao().insertBadge(
                    EarnedBadgeEntity(
                        badgeId = "BADGE_${worldForBadgeCheck.id}",
                        title = worldForBadgeCheck.badgeTitle,
                        worldId = worldForBadgeCheck.id
                    )
                )
            }
        }

        return attemptId
    }

    suspend fun getAttemptById(attemptId: Long): AssessmentAttemptEntity? =
        db.assessmentDao().getAttemptById(attemptId)

    suspend fun getResponsesForAttempt(attemptId: Long): List<AssessmentQuestionResponseEntity> =
        db.assessmentDao().getResponsesForAttempt(attemptId)

    private suspend fun awardXpAndUpdateStreak(xpDelta: Int) {
        val profile = db.learnerProfileDao().getProfileOnce() ?: LearnerProfileEntity(
            id = 1,
            name = "Grammar Explorer"
        )
        val today = LocalDate.now().toEpochDay()
        val newStreak = when {
            profile.lastActiveEpochDay == today -> profile.currentStreak.coerceAtLeast(1)
            profile.lastActiveEpochDay == today - 1 -> profile.currentStreak + 1
            else -> 1
        }
        db.learnerProfileDao().upsertProfile(
            profile.copy(
                xp = profile.xp + xpDelta,
                currentStreak = newStreak,
                lastActiveEpochDay = today
            )
        )
    }

    private suspend fun touchStreakOnly() {
        val profile = db.learnerProfileDao().getProfileOnce() ?: return
        val today = LocalDate.now().toEpochDay()
        if (profile.lastActiveEpochDay != today) {
            val newStreak = if (profile.lastActiveEpochDay == today - 1) {
                profile.currentStreak + 1
            } else {
                1
            }
            db.learnerProfileDao().upsertProfile(
                profile.copy(currentStreak = newStreak, lastActiveEpochDay = today)
            )
        }
    }
}
