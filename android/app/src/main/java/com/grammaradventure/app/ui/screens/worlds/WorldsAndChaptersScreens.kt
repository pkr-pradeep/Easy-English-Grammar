package com.grammaradventure.app.ui.screens.worlds

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.grammaradventure.app.domain.engine.PrerequisiteChecker
import com.grammaradventure.app.domain.model.BoardOption
import com.grammaradventure.app.ui.components.CharacterAvatarBadge
import com.grammaradventure.app.ui.components.GrammarWorldCard
import com.grammaradventure.app.ui.components.SectionHeader
import com.grammaradventure.app.ui.components.StatusTag
import com.grammaradventure.app.ui.components.parseHexColor
import com.grammaradventure.app.ui.theme.EmeraldDark
import com.grammaradventure.app.ui.theme.EmeraldTeal
import com.grammaradventure.app.ui.theme.IndigoInk
import com.grammaradventure.app.ui.theme.OutlineMuted
import com.grammaradventure.app.ui.theme.SaffronWarm
import com.grammaradventure.app.ui.theme.SuccessContainer
import com.grammaradventure.app.ui.theme.SuccessGreen
import com.grammaradventure.app.ui.theme.WarningAmber
import com.grammaradventure.app.ui.theme.WarningContainer

@Composable
fun WorldsOverviewScreen(
    uiState: CurriculumUiState,
    onOpenWorld: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Grammar Worlds",
                subtitle = "Six themed realms covering all 25 consolidated grammar modules"
            )
        }

        item {
            SectionHeader(
                title = "Cast of The Grammar Kingdom",
                subtitle = "Consistent mentors & companions across your adventures"
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.characters.forEach { character ->
                    val accent = parseHexColor(character.visualIdentity.accentColorHex)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, OutlineMuted)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CharacterAvatarBadge(
                                characterId = character.id,
                                accentColor = accent
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = character.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = character.role,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = accent
                                )
                                Text(
                                    text = "\"${character.signatureQuote}\"",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        items(uiState.worlds, key = { it.id }) { world ->
            val worldModules = uiState.allModules.filter { it.worldId == world.id }
            val completed = worldModules.count { uiState.completedModuleIds.contains(it.id) }
            GrammarWorldCard(
                world = world,
                completedCount = completed,
                totalCount = worldModules.size,
                onClick = { onOpenWorld(world.id) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChapterListScreen(
    worldId: String,
    uiState: CurriculumUiState,
    prerequisiteChecker: PrerequisiteChecker,
    onToggleSyllabusFilter: (Boolean) -> Unit,
    onSelectChapter: (String) -> Unit,
    onBack: () -> Unit
) {
    val world = uiState.worlds.find { it.id == worldId }
    val chapters = remember(worldId, uiState) {
        uiState.chaptersForWorld(worldId, prerequisiteChecker)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(world?.title ?: "Chapters") },
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
            if (world != null) {
                item {
                    val accent = parseHexColor(world.accentColorHex)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.09f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "World ${world.order} • ${world.subtitle}",
                                style = MaterialTheme.typography.labelLarge,
                                color = accent
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = world.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            if (uiState.selectedBoard != BoardOption.CONSOLIDATED) {
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = !uiState.filterBySyllabusOnly,
                            onClick = { onToggleSyllabusFilter(false) },
                            label = { Text("Show All Modules in World") }
                        )
                        FilterChip(
                            selected = uiState.filterBySyllabusOnly,
                            onClick = { onToggleSyllabusFilter(true) },
                            label = {
                                Text("Only ${uiState.selectedBoard.displayName.substringBefore(" ")} Class ${uiState.selectedClass}")
                            }
                        )
                    }
                }
            }

            items(chapters, key = { it.module.id }) { item ->
                val mod = item.module
                val isCompleted = item.progress?.status == "COMPLETED"
                val isInProgress = item.progress?.status == "IN_PROGRESS"

                Card(
                    onClick = { onSelectChapter(mod.id) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, OutlineMuted)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusTag(
                                label = "Module ${mod.moduleNumber}",
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = IndigoInk
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (mod.isDemoAvailable) {
                                    StatusTag(
                                        label = "Featured",
                                        containerColor = WarningContainer,
                                        contentColor = WarningAmber
                                    )
                                }
                                if (isCompleted) {
                                    StatusTag(
                                        label = "Completed",
                                        containerColor = SuccessContainer,
                                        contentColor = SuccessGreen
                                    )
                                } else if (isInProgress) {
                                    StatusTag(
                                        label = "In Progress",
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = EmeraldDark
                                    )
                                } else if (item.prerequisiteEvaluation.isHardLocked) {
                                    Icon(
                                        imageVector = Icons.Rounded.Lock,
                                        contentDescription = "Prerequisites required",
                                        tint = WarningAmber,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = mod.title,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = mod.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.AccessTime,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${mod.estimatedMinutes} min",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "• ${mod.topicStatus.replace('_', ' ').uppercase()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (uiState.selectedBoard != BoardOption.CONSOLIDATED && !item.matchesSelectedSyllabus) {
                                Text(
                                    text = "• Extra / Other Board",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = WarningAmber
                                )
                            }
                        }

                        if (!item.prerequisiteEvaluation.isSatisfied) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Recommended first: " +
                                    item.prerequisiteEvaluation.missingPrerequisites.joinToString {
                                        "Mod ${it.moduleNumber}: ${it.title}"
                                    },
                                style = MaterialTheme.typography.labelSmall,
                                color = WarningAmber
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterOverviewScreen(
    moduleId: String,
    uiState: CurriculumUiState,
    prerequisiteChecker: PrerequisiteChecker,
    hasLessonContent: Boolean,
    onStartLesson: (String) -> Unit,
    onOpenNotebook: (String) -> Unit,
    onStartAssessment: (String) -> Unit,
    onOpenPrerequisite: (String) -> Unit,
    onBack: () -> Unit
) {
    val detail = remember(moduleId, uiState) {
        uiState.chapterDetail(moduleId, prerequisiteChecker)
    }
    var showEnginePendingMessage by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(detail?.module?.title ?: "Chapter Overview") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (detail == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Module not found.", style = MaterialTheme.typography.titleMedium)
            }
            return@Scaffold
        }

        val mod = detail.module
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = IndigoInk)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        StatusTag(
                            label = "Module ${mod.moduleNumber} • ${mod.estimatedMinutes} min",
                            containerColor = SaffronWarm.copy(alpha = 0.28f),
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = mod.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Story Premise: ${mod.storyPremise}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Prerequisites Card
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (detail.prerequisiteEvaluation.isSatisfied) {
                            SuccessContainer
                        } else {
                            WarningContainer
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (detail.prerequisiteEvaluation.isSatisfied) {
                                    Icons.Rounded.CheckCircle
                                } else {
                                    Icons.Rounded.Info
                                },
                                contentDescription = null,
                                tint = if (detail.prerequisiteEvaluation.isSatisfied) SuccessGreen else WarningAmber
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (mod.prerequisiteIds.isEmpty()) {
                                    "No Prerequisites Required"
                                } else if (detail.prerequisiteEvaluation.isSatisfied) {
                                    "All Prerequisites Completed"
                                } else {
                                    "Recommended Prerequisites"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = if (detail.prerequisiteEvaluation.isSatisfied) SuccessGreen else WarningAmber
                            )
                        }
                        if (detail.prerequisiteEvaluation.missingPrerequisites.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            detail.prerequisiteEvaluation.missingPrerequisites.forEach { prereq ->
                                OutlinedButton(
                                    onClick = { onOpenPrerequisite(prereq.id) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                ) {
                                    Text("Review Module ${prereq.moduleNumber}: ${prereq.title}")
                                }
                            }
                        }
                    }
                }
            }

            // Learning Objectives & Subtopics
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, OutlineMuted)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Learning Objectives", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        mod.learningObjectives.forEach { obj ->
                            Text(
                                text = "• $obj",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = OutlineMuted)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Subtopics Covered", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        mod.subtopics.forEach { sub ->
                            Text(
                                text = "• $sub",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Source Textbook Traceability
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, OutlineMuted)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.MenuBook,
                                contentDescription = null,
                                tint = EmeraldTeal
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Textbook Source Mapping", style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        mod.sourceMappings.forEach { mapping ->
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Text(
                                    text = "${mapping.board} (Class ${mapping.classLabel}) — ${mapping.textbookTitle}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                val chNum = mapping.originalChapterNumber?.let { "Ch. $it: " } ?: ""
                                Text(
                                    text = "${chNum}${mapping.originalChapterTitle} | Source term: \"${mapping.sourceTerm}\"",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Page reference: ${mapping.pageRange} • Status: ${mapping.coverageStatus}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Launch Story Lesson & Grammar Notebook
            item {
                val progress = detail.progress
                val buttonLabel = when {
                    detail.prerequisiteEvaluation.isHardLocked -> "Complete Prerequisites to Unlock"
                    !hasLessonContent -> "Lesson Content Scheduled (Status: ${mod.lessonStatus})"
                    progress?.status == "IN_PROGRESS" ->
                        "Resume Story Lesson (Step ${progress.lastStepIndex + 1}/${progress.totalSteps})"
                    progress?.status == "COMPLETED" -> "Replay Story Lesson"
                    else -> "Enter the Story Lesson"
                }

                Button(
                    onClick = {
                        if (hasLessonContent) {
                            onStartLesson(mod.id)
                        } else {
                            showEnginePendingMessage = true
                        }
                    },
                    enabled = !detail.prerequisiteEvaluation.isHardLocked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(buttonLabel)
                }

                if (hasLessonContent) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onOpenNotebook(mod.id) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Grammar Notebook")
                        }
                        OutlinedButton(
                            onClick = { onStartAssessment(mod.id) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Final Assessment")
                        }
                    }
                }

                if (showEnginePendingMessage) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(
                            text = "Module ${mod.moduleNumber} metadata and textbook mappings are wired. Full story scenes and checkpoints will be populated during curriculum expansion after Assessment Engine verification.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompositionSectionScreen(
    uiState: CurriculumUiState,
    onBack: () -> Unit
) {
    val catalog = uiState.compositionCatalog
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Composition & Communication") },
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
            if (catalog != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                        )
                    ) {
                        Text(
                            text = catalog.note,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                items(catalog.categories, key = { it.id }) { category ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, OutlineMuted)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(category.title, style = MaterialTheme.typography.titleLarge)
                                StatusTag(
                                    label = "${category.sourceBoard} IX",
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = IndigoInk
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            category.topics.forEach { topic ->
                                Text(
                                    text = "• $topic",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
