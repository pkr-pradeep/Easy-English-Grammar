package com.grammaradventure.app.ui.screens.assessment

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.grammaradventure.app.ui.components.StatusTag
import com.grammaradventure.app.ui.theme.ErrorContainer
import com.grammaradventure.app.ui.theme.ErrorCrimson
import com.grammaradventure.app.ui.theme.IndigoInk
import com.grammaradventure.app.ui.theme.OutlineMuted
import com.grammaradventure.app.ui.theme.SuccessContainer
import com.grammaradventure.app.ui.theme.SuccessGreen
import com.grammaradventure.app.ui.theme.WarningAmber
import com.grammaradventure.app.ui.theme.WarningContainer

private fun formatDuration(seconds: Long): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AssessmentScreen(
    uiState: AssessmentRunnerUiState,
    onSelectOption: (questionId: String, optionIndex: Int) -> Unit,
    onGoToQuestion: (Int) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    var showConfirmSubmit by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Final Assessment",
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (uiState.publicQuestions.isNotEmpty()) {
                            Text(
                                text = "Answered ${uiState.answeredCount} of ${uiState.publicQuestions.size}",
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatDuration(uiState.elapsedSeconds),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (uiState.publicQuestions.isNotEmpty()) {
                Surface(tonalElevation = 3.dp, shadowElevation = 6.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onGoToQuestion(uiState.currentQuestionIndex - 1) },
                            enabled = uiState.currentQuestionIndex > 0,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Prev")
                        }
                        if (uiState.currentQuestionIndex < uiState.publicQuestions.lastIndex) {
                            Button(
                                onClick = { onGoToQuestion(uiState.currentQuestionIndex + 1) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Next")
                            }
                        } else {
                            Button(
                                onClick = { showConfirmSubmit = true },
                                enabled = uiState.canSubmit,
                                modifier = Modifier.weight(1.3f)
                            ) {
                                Text(
                                    if (uiState.canSubmit) {
                                        "Submit Assessment"
                                    } else {
                                        "Answer All (${uiState.answeredCount}/${uiState.publicQuestions.size})"
                                    }
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
                val question = uiState.currentQuestion ?: return@Scaffold
                val selectedIdx = uiState.selectedAnswers[question.id]

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Deferred answer-reveal banner
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = null,
                                    tint = IndigoInk,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Answers and explanations stay hidden until you submit all ${uiState.publicQuestions.size} questions.",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = IndigoInk
                                )
                            }
                        }
                    }

                    // Question Navigator Pills
                    item {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            uiState.publicQuestions.forEachIndexed { idx, q ->
                                val isCurrent = idx == uiState.currentQuestionIndex
                                val isAnswered = uiState.selectedAnswers.containsKey(q.id)
                                FilterChip(
                                    selected = isCurrent || isAnswered,
                                    onClick = { onGoToQuestion(idx) },
                                    label = {
                                        Text(
                                            text = "${idx + 1}",
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                )
                            }
                        }
                    }

                    // Active Question Card
                    item {
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
                                        label = "Q${question.number} • ${question.subtopicTitle}",
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = IndigoInk
                                    )
                                    StatusTag(
                                        label = question.difficulty.name,
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (!question.storyContext.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = question.storyContext,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = question.questionText,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(14.dp))

                                question.options.forEachIndexed { optIdx, optionText ->
                                    val isSelected = selectedIdx == optIdx
                                    Card(
                                        onClick = { onSelectOption(question.id, optIdx) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) {
                                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                            }
                                        ),
                                        border = BorderStroke(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) IndigoInk else OutlineMuted
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { onSelectOption(question.id, optIdx) }
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = optionText,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.canSubmit && uiState.currentQuestionIndex < uiState.publicQuestions.lastIndex) {
                        item {
                            Button(
                                onClick = { showConfirmSubmit = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("All Questions Answered — Submit Now")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showConfirmSubmit) {
        AlertDialog(
            onDismissRequest = { showConfirmSubmit = false },
            title = { Text("Submit Final Assessment?") },
            text = {
                Text("Once submitted, your answers are locked and cannot be edited. You will then see your score, explanations, and weak topics.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmSubmit = false
                        onSubmit()
                    }
                ) {
                    Text("Submit & Reveal Results")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmSubmit = false }) {
                    Text("Review Answers")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentResultScreen(
    uiState: AssessmentResultUiState,
    onReviewStepInLesson: (moduleId: String, stepId: String) -> Unit,
    onRetryAssessment: (moduleId: String) -> Unit,
    onOpenRevisionCenter: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assessment Results") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
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

            uiState.errorMessage != null || uiState.evaluation == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(uiState.errorMessage ?: "No result available.")
                }
            }

            else -> {
                val eval = uiState.evaluation
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Score Summary Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (eval.isPassed) SuccessContainer else WarningContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    StatusTag(
                                        label = if (eval.isPassed) "CHAPTER MASTERED" else "REVISION RECOMMENDED",
                                        containerColor = if (eval.isPassed) SuccessGreen else WarningAmber,
                                        contentColor = Color.White
                                    )
                                    StatusTag(
                                        label = if (uiState.isLocked) "Attempt Locked" else "Recorded",
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "${eval.percentage}% (${eval.correctCount}/${eval.totalQuestions} Correct)",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = if (eval.isPassed) SuccessGreen else WarningAmber
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Correct: ${eval.correctCount} • Incorrect: ${eval.incorrectCount} • Time: ${formatDuration(eval.timeTakenSeconds)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (eval.isPassed) SuccessGreen else WarningAmber
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedButton(
                                        onClick = { onRetryAssessment(uiState.moduleId) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Retry")
                                    }
                                    if (eval.weakSubtopics.isNotEmpty()) {
                                        Button(
                                            onClick = onOpenRevisionCenter,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Revision Centre")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Weak Concepts Recommendations
                    if (eval.weakSubtopics.isNotEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, WarningAmber.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Weak Concepts & Revision Plan",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = WarningAmber
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    eval.weakSubtopics.forEach { weak ->
                                        Text(
                                            text = "• ${weak.subtopicTitle} (missed ${weak.missedCount} question(s))",
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Detailed Question Breakdown",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    // Per-question Explanations & Distractor Rationales
                    items(eval.questionResults, key = { it.questionId }) { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(
                                1.dp,
                                if (item.isCorrect) SuccessGreen.copy(alpha = 0.45f) else ErrorCrimson.copy(alpha = 0.45f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Question ${item.number} • ${item.subtopicTitle}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = IndigoInk,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatusTag(
                                        label = if (item.isCorrect) "Correct" else "Incorrect",
                                        containerColor = if (item.isCorrect) SuccessContainer else ErrorContainer,
                                        contentColor = if (item.isCorrect) SuccessGreen else ErrorCrimson
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(item.questionText, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))

                                val selectedText = item.options.getOrNull(item.selectedOptionIndex) ?: "No answer"
                                val correctText = item.options.getOrNull(item.correctOptionIndex) ?: ""

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (item.isCorrect) Icons.Rounded.CheckCircle else Icons.Rounded.Close,
                                        contentDescription = null,
                                        tint = if (item.isCorrect) SuccessGreen else ErrorCrimson,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Your Answer: $selectedText",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (item.isCorrect) SuccessGreen else ErrorCrimson
                                    )
                                }
                                if (!item.isCorrect) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.CheckCircle,
                                            contentDescription = null,
                                            tint = SuccessGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Correct Answer: $correctText",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = SuccessGreen
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = OutlineMuted)
                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Explanation: ${item.explanation}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Rule Tested: ${item.grammarRule}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = IndigoInk
                                )

                                if (item.distractorRationales.isNotEmpty() && item.options.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Option Breakdown:",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    item.options.forEachIndexed { optIdx, opt ->
                                        val rationale = item.distractorRationales.getOrNull(optIdx)
                                        if (!rationale.isNullOrBlank()) {
                                            Text(
                                                text = "• $opt — $rationale",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(vertical = 1.dp)
                                            )
                                        }
                                    }
                                }

                                if (item.lessonStepRef.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(
                                        onClick = { onReviewStepInLesson(uiState.moduleId, item.lessonStepRef) }
                                    ) {
                                        Icon(Icons.Rounded.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Review Concept in Lesson")
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
