package com.grammaradventure.app.ui.screens.lesson

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.grammaradventure.app.domain.model.ConceptDiscoveryStep
import com.grammaradventure.app.domain.model.ContentProvenance
import com.grammaradventure.app.domain.model.InteractionType
import com.grammaradventure.app.domain.model.InteractiveCheckpointStep
import com.grammaradventure.app.domain.model.StorySceneStep
import com.grammaradventure.app.ui.components.CharacterAvatarBadge
import com.grammaradventure.app.ui.components.StatusTag
import com.grammaradventure.app.ui.components.characterDisplayName
import com.grammaradventure.app.ui.theme.EmeraldDark
import com.grammaradventure.app.ui.theme.EmeraldTeal
import com.grammaradventure.app.ui.theme.ErrorContainer
import com.grammaradventure.app.ui.theme.ErrorCrimson
import com.grammaradventure.app.ui.theme.IndigoInk
import com.grammaradventure.app.ui.theme.OutlineMuted
import com.grammaradventure.app.ui.theme.RoyalViolet
import com.grammaradventure.app.ui.theme.SaffronWarm
import com.grammaradventure.app.ui.theme.SuccessContainer
import com.grammaradventure.app.ui.theme.SuccessGreen
import com.grammaradventure.app.ui.theme.WarningAmber
import com.grammaradventure.app.ui.theme.WarningContainer

private fun characterAccent(characterId: String): Color = when (characterId) {
    "ACHARYA_VIDYADHAR" -> IndigoInk
    "CHIKOO_BANDAR" -> SaffronWarm
    "DETECTIVE_IRA" -> EmeraldTeal
    "MAHARAJ_VAKYA" -> RoyalViolet
    "VAANI" -> EmeraldDark
    else -> IndigoInk
}

private fun provenanceLabel(provenance: ContentProvenance): String = when (provenance) {
    ContentProvenance.SOURCE_TEXTBOOK -> "Textbook Rule"
    ContentProvenance.CONSOLIDATED -> "Consolidated"
    ContentProvenance.ADDITIONAL_EXAMPLE -> "Additional Example"
    ContentProvenance.AI_PRACTICE -> "Practice"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    uiState: LessonEngineUiState,
    onSelectOption: (String, String) -> Unit,
    onUpdateTypedAnswer: (String, String) -> Unit,
    onToggleWordToken: (String, String) -> Unit,
    onClearTokens: (String) -> Unit,
    onToggleHint: (String) -> Unit,
    onSubmitCheckpoint: (InteractiveCheckpointStep) -> Unit,
    onRetryCheckpoint: (String) -> Unit,
    onPreviousStep: () -> Unit,
    onNextStep: () -> Unit,
    onOpenNotebook: () -> Unit,
    onStartAssessment: () -> Unit,
    onBack: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.lesson?.storyTitle ?: "Story Lesson",
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (uiState.totalSteps > 0) {
                            Text(
                                text = "Step ${uiState.currentStepIndex + 1} of ${uiState.totalSteps}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.lesson != null) {
                        TextButton(onClick = onOpenNotebook) {
                            Icon(
                                imageVector = Icons.Rounded.MenuBook,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Notebook")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (uiState.lesson != null) {
                Surface(
                    tonalElevation = 3.dp,
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onPreviousStep,
                            enabled = uiState.currentStepIndex > 0,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Previous")
                        }

                        val isLastStep = uiState.currentStepIndex == uiState.totalSteps - 1
                        Button(
                            onClick = onNextStep,
                            enabled = uiState.canAdvanceFromCurrentStep,
                            modifier = Modifier.weight(1.4f)
                        ) {
                            Text(
                                when {
                                    !uiState.canAdvanceFromCurrentStep -> "Solve Checkpoint First"
                                    isLastStep -> "Complete Story"
                                    else -> "Continue"
                                }
                            )
                            if (uiState.canAdvanceFromCurrentStep) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(uiState.errorMessage, style = MaterialTheme.typography.titleMedium)
                }
            }

            else -> {
                val step = uiState.currentStep ?: return@Scaffold
                val progress = if (uiState.totalSteps > 0) {
                    (uiState.currentStepIndex + 1).toFloat() / uiState.totalSteps
                } else 0f

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Column {
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = step.stepTitle,
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                StatusTag(
                                    label = provenanceLabel(step.provenance),
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    when (step) {
                        is StorySceneStep -> {
                            item {
                                StorySceneContent(step = step)
                            }
                        }

                        is ConceptDiscoveryStep -> {
                            item {
                                ConceptDiscoveryContent(step = step)
                            }
                        }

                        is InteractiveCheckpointStep -> {
                            item {
                                val cpState = uiState.checkpointStates[step.checkpointId]
                                    ?: CheckpointInteractionState()
                                val alreadyPassed = uiState.completedCheckpointIds.contains(step.checkpointId)
                                InteractiveCheckpointContent(
                                    step = step,
                                    state = cpState,
                                    alreadyPassed = alreadyPassed,
                                    onSelectOption = { onSelectOption(step.checkpointId, it) },
                                    onUpdateTypedAnswer = { onUpdateTypedAnswer(step.checkpointId, it) },
                                    onToggleWordToken = { onToggleWordToken(step.checkpointId, it) },
                                    onClearTokens = { onClearTokens(step.checkpointId) },
                                    onToggleHint = { onToggleHint(step.checkpointId) },
                                    onSubmit = { onSubmitCheckpoint(step) },
                                    onRetry = { onRetryCheckpoint(step.checkpointId) }
                                )
                            }
                        }
                    }

                    if (uiState.isLessonCompleted && uiState.currentStepIndex == uiState.totalSteps - 1) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SuccessContainer),
                                border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.4f))
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.CheckCircle,
                                            contentDescription = null,
                                            tint = SuccessGreen
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Story Completed!" +
                                                if (uiState.completionBannerXp > 0) " (+${uiState.completionBannerXp} XP)" else "",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = SuccessGreen
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Review your Grammar Notebook or prepare for the 15-question Final Assessment.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = SuccessGreen
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        OutlinedButton(
                                            onClick = onOpenNotebook,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Grammar Notebook")
                                        }
                                        Button(
                                            onClick = onStartAssessment,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Quiz,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Final Assessment")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StorySceneContent(step: StorySceneStep) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                StatusTag(
                    label = step.sceneTag,
                    containerColor = IndigoInk,
                    contentColor = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = step.settingDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic
                )
            }
        }

        step.dialogues.forEach { dialogue ->
            val accent = characterAccent(dialogue.characterId)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, OutlineMuted)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    CharacterAvatarBadge(
                        characterId = dialogue.characterId,
                        accentColor = accent
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = characterDisplayName(dialogue.characterId),
                                style = MaterialTheme.typography.titleMedium,
                                color = accent
                            )
                            Text(
                                text = "• ${dialogue.expression}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dialogue.line,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        if (step.timelinePanels.isNotEmpty()) {
            Text(
                text = "Time Machine Timeline Panels",
                style = MaterialTheme.typography.titleMedium
            )
            step.timelinePanels.forEach { panel ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                    ),
                    border = BorderStroke(1.dp, EmeraldTeal.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        StatusTag(
                            label = panel.timeLabel,
                            containerColor = EmeraldTeal,
                            contentColor = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = panel.exampleSentence,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${panel.tenseUsed} — ${panel.explanation}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (!step.storyCallout.isNullOrBlank()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = WarningContainer)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoStories,
                        contentDescription = null,
                        tint = WarningAmber
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = step.storyCallout,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = WarningAmber
                    )
                }
            }
        }
    }
}

@Composable
private fun ConceptDiscoveryContent(step: ConceptDiscoveryStep) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Peculiar Memory Aid (explicitly labelled!)
        Card(
            colors = CardDefaults.cardColors(containerColor = WarningContainer),
            border = BorderStroke(1.dp, SaffronWarm.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                StatusTag(
                    label = step.memoryAidLabel,
                    containerColor = SaffronWarm,
                    contentColor = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"${step.peculiarDefinition}\"",
                    style = MaterialTheme.typography.titleMedium,
                    color = WarningAmber
                )
            }
        }

        // Formal Definition & Learner-Friendly Explanation
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, OutlineMuted)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                StatusTag(
                    label = "Formal Grammar Definition",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = IndigoInk
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = step.formalDefinition,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = OutlineMuted)
                Spacer(modifier = Modifier.height(10.dp))
                Text("In Simple Words", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step.simpleExplanation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Structure Patterns
        if (step.structurePatterns.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, OutlineMuted)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Forms & Patterns", style = MaterialTheme.typography.titleMedium)
                    step.structurePatterns.forEach { pattern ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                        ) {
                            Text(pattern.formName, style = MaterialTheme.typography.labelLarge, color = IndigoInk)
                            Text(pattern.formula, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text(pattern.sample, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Real-life Examples
        if (step.realLifeExamples.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, OutlineMuted)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Real-Life Examples", style = MaterialTheme.typography.titleMedium)
                    step.realLifeExamples.forEach { ex ->
                        Column {
                            Text("• ${ex.sentence}", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = "  ${ex.contextNote} (${provenanceLabel(ex.provenance)})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Correct vs Incorrect Usage
        if (step.correctVsIncorrect.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, OutlineMuted)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Common Mistake vs. Correct Usage", style = MaterialTheme.typography.titleMedium)
                    step.correctVsIncorrect.forEach { pair ->
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Close, contentDescription = null, tint = ErrorCrimson, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(pair.incorrectSentence, style = MaterialTheme.typography.bodyMedium, color = ErrorCrimson)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(pair.correctSentence, style = MaterialTheme.typography.bodyMedium, color = SuccessGreen, fontWeight = FontWeight.SemiBold)
                            }
                            Text(
                                text = "Why: ${pair.whyItHappens}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Exceptions
        if (step.exceptions.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Watch Out — Exceptions & Nuances", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    step.exceptions.forEach { exc ->
                        Text("• $exc", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InteractiveCheckpointContent(
    step: InteractiveCheckpointStep,
    state: CheckpointInteractionState,
    alreadyPassed: Boolean,
    onSelectOption: (String) -> Unit,
    onUpdateTypedAnswer: (String) -> Unit,
    onToggleWordToken: (String) -> Unit,
    onClearTokens: () -> Unit,
    onToggleHint: () -> Unit,
    onSubmit: () -> Unit,
    onRetry: () -> Unit
) {
    val accent = characterAccent(step.characterId)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.08f)),
            border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CharacterAvatarBadge(characterId = step.characterId, accentColor = accent)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "${characterDisplayName(step.characterId)} • Checkpoint",
                        style = MaterialTheme.typography.labelLarge,
                        color = accent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(step.storyPrompt, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

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
                    StatusTag(
                        label = step.interactionType.name.replace('_', ' '),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = IndigoInk
                    )
                    if (alreadyPassed) {
                        StatusTag(
                            label = "Passed",
                            containerColor = SuccessContainer,
                            contentColor = SuccessGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(step.questionPrompt, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(14.dp))

                when (step.interactionType) {
                    InteractionType.MULTIPLE_CHOICE,
                    InteractionType.ERROR_CORRECTION,
                    InteractionType.IDENTIFY_ROLE -> {
                        step.options.forEach { option ->
                            val selected = state.selectedOption == option
                            Card(
                                onClick = { onSelectOption(option) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selected) {
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                    }
                                ),
                                border = BorderStroke(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected) IndigoInk else OutlineMuted
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selected,
                                        onClick = { onSelectOption(option) }
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(option, style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }

                    InteractionType.FILL_IN_BLANK,
                    InteractionType.SENTENCE_CONVERSION -> {
                        OutlinedTextField(
                            value = state.typedAnswer,
                            onValueChange = onUpdateTypedAnswer,
                            label = { Text("Type your answer") },
                            placeholder = { Text("e.g., had stolen") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    InteractionType.ARRANGE_WORDS -> {
                        Text(
                            text = "Your sentence:",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small,
                            border = BorderStroke(1.dp, OutlineMuted)
                        ) {
                            Text(
                                text = if (state.arrangedTokens.isEmpty()) {
                                    "Tap word tiles below in order..."
                                } else {
                                    state.arrangedTokens.joinToString(" ")
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            step.wordTokens.forEach { token ->
                                val picked = state.arrangedTokens.contains(token)
                                FilterChip(
                                    selected = picked,
                                    onClick = { onToggleWordToken(token) },
                                    label = { Text(token) }
                                )
                            }
                        }
                        if (state.arrangedTokens.isNotEmpty()) {
                            TextButton(onClick = onClearTokens) {
                                Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Clear Order")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AssistChip(
                        onClick = onToggleHint,
                        label = { Text(if (state.showHint) "Hide Hint" else "Need a Hint?") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Lightbulb,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = onSubmit) {
                        Text("Check Answer")
                    }
                }

                if (state.showHint) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = WarningContainer)) {
                        Text(
                            text = "Hint: ${step.hint}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WarningAmber,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                state.lastEvaluation?.let { eval ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (eval.isCorrect) SuccessContainer else ErrorContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (eval.isCorrect) {
                                    "Correct!" + if (state.xpEarnedThisCheckpoint > 0) " (+${state.xpEarnedThisCheckpoint} XP)" else ""
                                } else {
                                    "Not Quite — Let's Inspect Why"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = if (eval.isCorrect) SuccessGreen else ErrorCrimson
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = eval.feedbackMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (eval.isCorrect) SuccessGreen else ErrorCrimson
                            )
                            if (!eval.isCorrect && !eval.simplerExample.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Simpler Example: ${eval.simplerExample}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ErrorCrimson
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedButton(onClick = onRetry) {
                                    Text("Try Again")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
