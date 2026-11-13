package com.grammaradventure.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LearnerProfileDao {
    @Query("SELECT * FROM learner_profile WHERE id = 1")
    fun observeProfile(): Flow<LearnerProfileEntity?>

    @Query("SELECT * FROM learner_profile WHERE id = 1")
    suspend fun getProfileOnce(): LearnerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: LearnerProfileEntity)

    @Query("UPDATE learner_profile SET xp = xp + :delta WHERE id = 1")
    suspend fun addXp(delta: Int)
}

@Dao
interface LessonProgressDao {
    @Query("SELECT * FROM lesson_progress ORDER BY updatedAt DESC")
    fun observeAllProgress(): Flow<List<LessonProgressEntity>>

    @Query("SELECT * FROM lesson_progress WHERE moduleId = :moduleId")
    fun observeModuleProgress(moduleId: String): Flow<LessonProgressEntity?>

    @Query("SELECT * FROM lesson_progress WHERE moduleId = :moduleId")
    suspend fun getProgressOnce(moduleId: String): LessonProgressEntity?

    @Query("SELECT * FROM lesson_progress WHERE status = 'IN_PROGRESS' ORDER BY updatedAt DESC LIMIT 1")
    fun observeLatestInProgress(): Flow<LessonProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: LessonProgressEntity)
}

@Dao
interface CheckpointAttemptDao {
    @Insert
    suspend fun insertAttempt(attempt: CheckpointAttemptEntity)

    @Query("SELECT * FROM checkpoint_attempts WHERE moduleId = :moduleId ORDER BY timestamp DESC")
    fun observeModuleCheckpoints(moduleId: String): Flow<List<CheckpointAttemptEntity>>

    @Query("SELECT COUNT(*) FROM checkpoint_attempts WHERE checkpointId = :checkpointId")
    suspend fun countTotalAttempts(checkpointId: String): Int

    @Query("SELECT COUNT(*) FROM checkpoint_attempts WHERE checkpointId = :checkpointId AND isCorrect = 1")
    suspend fun countCorrectAttempts(checkpointId: String): Int
}

@Dao
interface AssessmentDao {
    @Insert
    suspend fun insertAttempt(attempt: AssessmentAttemptEntity): Long

    @Insert
    suspend fun insertResponses(responses: List<AssessmentQuestionResponseEntity>)

    @Query("SELECT * FROM assessment_attempts WHERE moduleId = :moduleId ORDER BY submittedAt DESC")
    fun observeAttemptsForModule(moduleId: String): Flow<List<AssessmentAttemptEntity>>

    @Query("SELECT * FROM assessment_attempts ORDER BY submittedAt DESC")
    fun observeAllAttempts(): Flow<List<AssessmentAttemptEntity>>

    @Query("SELECT * FROM assessment_attempts WHERE attemptId = :attemptId")
    suspend fun getAttemptById(attemptId: Long): AssessmentAttemptEntity?

    @Query("SELECT * FROM assessment_question_responses WHERE attemptId = :attemptId ORDER BY id ASC")
    suspend fun getResponsesForAttempt(attemptId: Long): List<AssessmentQuestionResponseEntity>

    @Query("SELECT COUNT(*) FROM assessment_attempts WHERE moduleId = :moduleId AND isPassed = 1")
    suspend fun countPassedAttemptsForModule(moduleId: String): Int

    @Query("SELECT DISTINCT moduleId FROM assessment_attempts WHERE isPassed = 1")
    suspend fun getPassedModuleIds(): List<String>
}

@Dao
interface WeakConceptDao {
    @Query("SELECT * FROM weak_concepts WHERE isResolved = 0 ORDER BY errorCount DESC, lastMissedAt DESC")
    fun observeActiveWeakConcepts(): Flow<List<WeakConceptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWeakConcept(entity: WeakConceptEntity)

    @Query("SELECT * FROM weak_concepts WHERE subtopicId = :subtopicId")
    suspend fun getById(subtopicId: String): WeakConceptEntity?
}

@Dao
interface EarnedBadgeDao {
    @Query("SELECT * FROM earned_badges ORDER BY earnedAt DESC")
    fun observeBadges(): Flow<List<EarnedBadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBadge(badge: EarnedBadgeEntity)
}
