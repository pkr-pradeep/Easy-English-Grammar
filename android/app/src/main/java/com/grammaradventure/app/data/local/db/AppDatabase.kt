package com.grammaradventure.app.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        LearnerProfileEntity::class,
        LessonProgressEntity::class,
        CheckpointAttemptEntity::class,
        AssessmentAttemptEntity::class,
        AssessmentQuestionResponseEntity::class,
        WeakConceptEntity::class,
        EarnedBadgeEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun learnerProfileDao(): LearnerProfileDao
    abstract fun lessonProgressDao(): LessonProgressDao
    abstract fun checkpointAttemptDao(): CheckpointAttemptDao
    abstract fun assessmentDao(): AssessmentDao
    abstract fun weakConceptDao(): WeakConceptDao
    abstract fun earnedBadgeDao(): EarnedBadgeDao

    companion object {
        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "english_grammar_adventure.db"
            ).build()
    }
}
