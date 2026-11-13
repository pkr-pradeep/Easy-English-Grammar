package com.grammaradventure.app.ui.screens.progress

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.grammaradventure.app.data.local.datastore.UserPreferences
import com.grammaradventure.app.data.local.datastore.UserPreferencesManager
import com.grammaradventure.app.data.local.db.AssessmentAttemptEntity
import com.grammaradventure.app.data.local.db.EarnedBadgeEntity
import com.grammaradventure.app.data.local.db.LearnerProfileEntity
import com.grammaradventure.app.data.local.db.WeakConceptEntity
import com.grammaradventure.app.data.repository.ProgressRepository
import com.grammaradventure.app.ui.components.SectionHeader
import com.grammaradventure.app.ui.components.StatusTag
import com.grammaradventure.app.ui.theme.OutlineMuted
import com.grammaradventure.app.ui.theme.SuccessContainer
import com.grammaradventure.app.ui.theme.SuccessGreen
import com.grammaradventure.app.ui.theme.WarningAmber
import com.grammaradventure.app.ui.theme.WarningContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProgressAndSettingsUiState(
    val profile: LearnerProfileEntity? = null,
    val preferences: UserPreferences = UserPreferences(),
    val completedChaptersCount: Int = 0,
    val attempts: List<AssessmentAttemptEntity> = emptyList(),
    val weakConcepts: List<WeakConceptEntity> = emptyList(),
    val badges: List<EarnedBadgeEntity> = emptyList()
)

class ProgressAndSettingsViewModel(
    private val progressRepository: ProgressRepository,
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    val uiState: StateFlow<ProgressAndSettingsUiState> = combine(
        progressRepository.observeProfile(),
        preferencesManager.preferences,
        progressRepository.observeAllLessonProgress(),
        progressRepository.observeAllAttempts(),
        progressRepository.observeActiveWeakConcepts(),
        progressRepository.observeEarnedBadges()
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val profile = values[0] as LearnerProfileEntity?
        val prefs = values[1] as UserPreferences
        @Suppress("UNCHECKED_CAST")
        val progress = values[2] as List<com.grammaradventure.app.data.local.db.LessonProgressEntity>
        @Suppress("UNCHECKED_CAST")
        val attempts = values[3] as List<AssessmentAttemptEntity>
        @Suppress("UNCHECKED_CAST")
        val weak = values[4] as List<WeakConceptEntity>
        @Suppress("UNCHECKED_CAST")
        val badges = values[5] as List<EarnedBadgeEntity>

        ProgressAndSettingsUiState(
            profile = profile,
            preferences = prefs,
            completedChaptersCount = progress.count { it.status == "COMPLETED" },
            attempts = attempts,
            weakConcepts = weak,
            badges = badges
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProgressAndSettingsUiState()
    )

    fun setStrictPrerequisites(strict: Boolean) {
        viewModelScope.launch {
            preferencesManager.setStrictPrerequisites(strict)
        }
    }

    class Factory(
        private val progressRepository: ProgressRepository,
        private val preferencesManager: UserPreferencesManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProgressAndSettingsViewModel(progressRepository, preferencesManager) as T
        }
    }
}

@Composable
fun RevisionCenterScreen(
    viewModel: ProgressAndSettingsViewModel,
    onOpenChapter: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "Revision Centre",
                subtitle = "Targeted missions generated from your checkpoints & assessments"
            )
        }

        if (state.weakConcepts.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, OutlineMuted)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "No weak concepts recorded yet",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "As you complete checkpoints and chapter assessments, any missed grammar rules will automatically appear here for focused revision.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(onClick = { onOpenChapter("MOD_01") }) {
                            Text("Start Module 1: Sentence Basics")
                        }
                    }
                }
            }
        } else {
            items(state.weakConcepts, key = { it.subtopicId }) { weak ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = WarningContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = weak.subtopicTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = WarningAmber
                        )
                        Text(
                            text = "Missed ${weak.errorCount} time(s) • Module ${weak.moduleId}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WarningAmber
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { onOpenChapter(weak.moduleId) }) {
                            Text("Review Module")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProgressDashboardScreen(
    viewModel: ProgressAndSettingsViewModel,
    onOpenAttemptResult: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "Progress & Mastery",
                subtitle = "All progress is stored locally on your device"
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, OutlineMuted)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = state.profile?.name ?: "Grammar Explorer",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "XP: ${state.profile?.xp ?: 0} • Streak: ${state.profile?.currentStreak ?: 0} day(s) • Completed Chapters: ${state.completedChaptersCount}/25",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            SectionHeader(
                title = "World Badges (${state.badges.size}/6)",
                subtitle = "Earned by completing grammar worlds with mastery"
            )
        }

        if (state.badges.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, OutlineMuted)
                ) {
                    Text(
                        text = "Complete every module in a Grammar World to unlock its crest badge.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(state.badges, key = { it.badgeId }) { badge ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SuccessContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(badge.title, style = MaterialTheme.typography.titleMedium, color = SuccessGreen)
                        StatusTag(
                            label = "Earned",
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = SuccessGreen
                        )
                    }
                }
            }
        }

        item {
            SectionHeader(
                title = "Assessment History (${state.attempts.size})",
                subtitle = "Recent chapter assessment attempts"
            )
        }

        if (state.attempts.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, OutlineMuted)
                ) {
                    Text(
                        text = "No chapter assessments submitted yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(state.attempts, key = { it.attemptId }) { attempt ->
                Card(
                    onClick = { onOpenAttemptResult(attempt.attemptId) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, OutlineMuted)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(attempt.moduleId, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "${attempt.correctCount}/${attempt.totalQuestions} (${attempt.percentage}%)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        StatusTag(
                            label = if (attempt.isPassed) "Passed" else "Review",
                            containerColor = if (attempt.isPassed) SuccessContainer else WarningContainer,
                            contentColor = if (attempt.isPassed) SuccessGreen else WarningAmber
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ProgressAndSettingsViewModel,
    onEditProfile: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, OutlineMuted)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Learner Profile & Syllabus", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${state.profile?.name ?: "Explorer"} • Class ${state.profile?.selectedClass ?: 9} • ${state.profile?.selectedBoard ?: "CONSOLIDATED"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(onClick = onEditProfile) {
                            Text("Change Class / Board / Name")
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, OutlineMuted)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Enforce Strict Prerequisites", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "Lock later chapters until prerequisite modules are completed (Demo chapter remains accessible).",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = state.preferences.strictPrerequisites,
                            onCheckedChange = { viewModel.setStrictPrerequisites(it) }
                        )
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, OutlineMuted)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Offline & Privacy Guarantee", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "English Grammar Adventure requests zero network permissions, collects zero telemetry, and runs 100% offline in airplane mode.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
