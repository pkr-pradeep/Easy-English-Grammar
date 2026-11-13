package com.grammaradventure.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ega_preferences")

data class UserPreferences(
    val onboardingCompleted: Boolean = false,
    val strictPrerequisites: Boolean = false,
    val assessmentPassThreshold: Int = 70,
    val defaultAssessmentLength: Int = 15
)

class UserPreferencesManager(private val context: Context) {

    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val STRICT_PREREQUISITES = booleanPreferencesKey("strict_prerequisites")
        val ASSESSMENT_PASS_THRESHOLD = intPreferencesKey("assessment_pass_threshold")
        val DEFAULT_ASSESSMENT_LENGTH = intPreferencesKey("default_assessment_length")
    }

    val preferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            onboardingCompleted = prefs[Keys.ONBOARDING_COMPLETED] ?: false,
            strictPrerequisites = prefs[Keys.STRICT_PREREQUISITES] ?: false,
            assessmentPassThreshold = prefs[Keys.ASSESSMENT_PASS_THRESHOLD] ?: 70,
            defaultAssessmentLength = prefs[Keys.DEFAULT_ASSESSMENT_LENGTH] ?: 15
        )
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setStrictPrerequisites(strict: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.STRICT_PREREQUISITES] = strict
        }
    }

    suspend fun setAssessmentPassThreshold(threshold: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ASSESSMENT_PASS_THRESHOLD] = threshold.coerceIn(50, 95)
        }
    }
}
