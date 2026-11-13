package com.grammaradventure.app.di

import android.content.Context
import com.grammaradventure.app.data.local.assets.JsonAssetLoader
import com.grammaradventure.app.data.local.datastore.UserPreferencesManager
import com.grammaradventure.app.data.local.db.AppDatabase
import com.grammaradventure.app.data.repository.CurriculumRepository
import com.grammaradventure.app.data.repository.LessonRepository
import com.grammaradventure.app.data.repository.ProgressRepository
import com.grammaradventure.app.domain.engine.AssessmentScorer
import com.grammaradventure.app.domain.engine.CheckpointEvaluator
import com.grammaradventure.app.domain.engine.PrerequisiteChecker

class AppContainer(context: Context) {
    private val appDatabase: AppDatabase = AppDatabase.build(context)
    private val assetLoader: JsonAssetLoader = JsonAssetLoader(context)

    val preferencesManager: UserPreferencesManager = UserPreferencesManager(context)
    val curriculumRepository: CurriculumRepository = CurriculumRepository(assetLoader)
    val lessonRepository: LessonRepository = LessonRepository(assetLoader)
    val progressRepository: ProgressRepository = ProgressRepository(appDatabase)
    val prerequisiteChecker: PrerequisiteChecker = PrerequisiteChecker()
    val checkpointEvaluator: CheckpointEvaluator = CheckpointEvaluator()
    val assessmentScorer: AssessmentScorer = AssessmentScorer()
}
