package com.grammaradventure.app.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.grammaradventure.app.ui.components.CharacterAvatarBadge
import com.grammaradventure.app.ui.components.GrammarWorldCard
import com.grammaradventure.app.ui.components.SectionHeader
import com.grammaradventure.app.ui.components.StatusTag
import com.grammaradventure.app.ui.theme.EmeraldTeal
import com.grammaradventure.app.ui.theme.IndigoInk
import com.grammaradventure.app.ui.theme.OutlineMuted
import com.grammaradventure.app.ui.theme.SaffronWarm
import com.grammaradventure.app.ui.theme.WarningAmber
import com.grammaradventure.app.ui.theme.WarningContainer

@Composable
fun HomeDashboardScreen(
    uiState: HomeUiState,
    onOpenWorld: (String) -> Unit,
    onOpenModulePreview: (String) -> Unit,
    onOpenRevision: () -> Unit,
    onOpenComposition: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val overallProgress = if (uiState.totalChapters > 0) {
        uiState.completedChapters.toFloat() / uiState.totalChapters
    } else 0f

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header + Class/Board Badge
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Namaste, ${uiState.learnerName}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusTag(
                            label = "Class ${uiState.selectedClass}",
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = IndigoInk
                        )
                        StatusTag(
                            label = uiState.selectedBoard.displayName,
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "Settings"
                    )
                }
            }
        }

        // Stats Strip: Chapters, Streak, XP, Overall Progress
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, OutlineMuted)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatMetric(
                            label = "Chapters",
                            value = "${uiState.completedChapters}/${uiState.totalChapters}"
                        )
                        StatMetric(
                            label = "Streak",
                            value = "${uiState.streakDays}d",
                            icon = Icons.Rounded.LocalFireDepartment
                        )
                        StatMetric(
                            label = "Earned XP",
                            value = "${uiState.xp}",
                            icon = Icons.Rounded.Bolt
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { overallProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(MaterialTheme.shapes.small)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Overall Mastery: ${(overallProgress * 100).toInt()}% • Offline Ready",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Continue Learning / Featured Demo Card
        uiState.continueModule?.let { module ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = IndigoInk),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        StatusTag(
                            label = if (module.isDemoAvailable) "FEATURED • CONTINUE" else "CONTINUE LEARNING",
                            containerColor = SaffronWarm.copy(alpha = 0.25f),
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Module ${module.moduleNumber}: ${module.title}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.continueStepLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { onOpenModulePreview(module.id) },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = SaffronWarm,
                                contentColor = MaterialTheme.colorScheme.onTertiary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Chapter Overview")
                        }
                    }
                }
            }
        }

        // Daily Challenge Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f)
                ),
                border = BorderStroke(1.dp, SaffronWarm.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CharacterAvatarBadge(
                        characterId = "CHIKOO_BANDAR",
                        accentColor = SaffronWarm
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily Grammar Challenge",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.dailyPromptQuestion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                uiState.demoModule?.let { onOpenModulePreview(it.id) }
                            }
                        ) {
                            Text("Inspect Tense Mystery")
                        }
                    }
                }
            }
        }

        // Weak Topics Alert (only shown when Room records actual weak concepts)
        if (uiState.weakConcepts.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = WarningContainer),
                    onClick = onOpenRevision
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.WarningAmber,
                            contentDescription = null,
                            tint = WarningAmber
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Weak Topics to Review (${uiState.weakConcepts.size})",
                                style = MaterialTheme.typography.titleMedium,
                                color = WarningAmber
                            )
                            Text(
                                text = uiState.weakConcepts.joinToString { it.subtopicTitle },
                                style = MaterialTheme.typography.bodyMedium,
                                color = WarningAmber
                            )
                        }
                    }
                }
            }
        }

        // Six Grammar Worlds Header
        item {
            SectionHeader(
                title = "The Six Grammar Worlds",
                subtitle = "25 consolidated modules mapped to Assam, Punjab & Odisha textbooks"
            )
        }

        items(uiState.worldSummaries, key = { it.world.id }) { summary ->
            GrammarWorldCard(
                world = summary.world,
                completedCount = summary.completedModules,
                totalCount = summary.totalModules,
                onClick = { onOpenWorld(summary.world.id) }
            )
        }

        // Separate Section: Composition & Communication
        item {
            Card(
                onClick = onOpenComposition,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, EmeraldTeal.copy(alpha = 0.45f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.EditNote,
                                contentDescription = null,
                                tint = EmeraldTeal
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Composition & Communication",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Separate section: paragraphs, letters, emails, note-making & reading skills",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatMetric(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SaffronWarm,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
