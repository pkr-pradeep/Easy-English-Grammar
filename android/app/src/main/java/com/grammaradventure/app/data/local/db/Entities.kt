package com.grammaradventure.app.data.local.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "learner_profile")
data class LearnerProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val avatarId: String = "ACHARYA_VIDYADHAR",
    val selectedClass: Int = 9,
    val selectedBoard: String = "CONSOLIDATED",
    val xp: Int = 0,
    val currentStreak: Int = 0,
    val lastActiveEpochDay: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey val moduleId: String,
    val lastCompletedStepId: String = "",
    val lastStepIndex: Int = 0,
    val totalSteps: Int = 0,
    val status: String = "NOT_STARTED", // NOT_STARTED, IN_PROGRESS, COMPLETED
    val completedAt: Long? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "checkpoint_attempts",
    indices = [Index(value = ["moduleId", "checkpointId"])]
)
data class CheckpointAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val moduleId: String,
    val checkpointId: String,
    val subtopicId: String,
    val attemptCount: Int,
    val isCorrect: Boolean,
    val selectedAnswer: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "assessment_attempts",
    indices = [Index(value = ["moduleId"])]
)
data class AssessmentAttemptEntity(
    @PrimaryKey(autoGenerate = true) val attemptId: Long = 0,
    val moduleId: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val percentage: Int,
    val timeTakenSeconds: Long,
    val isPassed: Boolean,
    val isLocked: Boolean = true,
    val submittedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "assessment_question_responses",
    foreignKeys = [
        ForeignKey(
            entity = AssessmentAttemptEntity::class,
            parentColumns = ["attemptId"],
            childColumns = ["attemptId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["attemptId"])]
)
data class AssessmentQuestionResponseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val attemptId: Long,
    val questionId: String,
    val subtopicId: String,
    val selectedOptionIndex: Int,
    val correctOptionIndex: Int,
    val isCorrect: Boolean,
    val grammarRule: String
)

@Entity(tableName = "weak_concepts")
data class WeakConceptEntity(
    @PrimaryKey val subtopicId: String,
    val moduleId: String,
    val subtopicTitle: String,
    val errorCount: Int,
    val lastMissedAt: Long = System.currentTimeMillis(),
    val revisionCompletedCount: Int = 0,
    val isResolved: Boolean = false
)

@Entity(tableName = "earned_badges")
data class EarnedBadgeEntity(
    @PrimaryKey val badgeId: String,
    val title: String,
    val worldId: String,
    val earnedAt: Long = System.currentTimeMillis()
)
