package com.grammaradventure.app.ui.screens.notebook

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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.grammaradventure.app.data.repository.LessonRepository
import com.grammaradventure.app.domain.model.LessonContent
import com.grammaradventure.app.ui.components.SectionHeader
import com.grammaradventure.app.ui.theme.ErrorCrimson
import com.grammaradventure.app.ui.theme.IndigoInk
import com.grammaradventure.app.ui.theme.OutlineMuted
import com.grammaradventure.app.ui.theme.SuccessGreen
import com.grammaradventure.app.ui.theme.WarningAmber
import com.grammaradventure.app.ui.theme.WarningContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrammarNotebookScreen(
    moduleId: String,
    lessonRepository: LessonRepository,
    onBack: () -> Unit
) {
    var lesson by remember(moduleId) { mutableStateOf<LessonContent?>(null) }
    var isLoading by remember(moduleId) { mutableStateOf(true) }

    LaunchedEffect(moduleId) {
        lesson = lessonRepository.getLessonForModule(moduleId)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grammar Notebook") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val notebook = lesson?.grammarNotebook
        if (notebook == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Notebook notes for $moduleId will appear once the chapter lesson is published.")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                SectionHeader(
                    title = lesson?.title.orEmpty(),
                    subtitle = "Quick revision notes, formulas, memory aids & textbook provenance"
                )
            }

            item {
                Text("Key Grammar Rules", style = MaterialTheme.typography.titleMedium)
            }

            items(notebook.summaryRules) { rule ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, OutlineMuted)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(rule.heading, style = MaterialTheme.typography.titleMedium, color = IndigoInk)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(rule.ruleSummary, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Pattern: ${rule.formulaOrPattern}",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = "Example: ${rule.example}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = WarningContainer)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Memory Aids (Peculiar Definitions — Not Formal Rules)",
                            style = MaterialTheme.typography.titleMedium,
                            color = WarningAmber
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        notebook.memoryAids.forEach { aid ->
                            Text("• $aid", style = MaterialTheme.typography.bodyMedium, color = WarningAmber, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }

            item {
                Text("Common Mistakes to Avoid", style = MaterialTheme.typography.titleMedium)
            }

            items(notebook.commonMistakes) { mistake ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, OutlineMuted)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Close, contentDescription = null, tint = ErrorCrimson, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(mistake.incorrectSentence, style = MaterialTheme.typography.bodyMedium, color = ErrorCrimson)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(mistake.correctSentence, style = MaterialTheme.typography.bodyMedium, color = SuccessGreen, fontWeight = FontWeight.SemiBold)
                        }
                        Text(
                            text = mistake.whyItHappens,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Exceptions", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        notebook.exceptions.forEach { exc ->
                            Text("• $exc", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Textbook Source Notes", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        notebook.textbookNotes.forEach { note ->
                            Text("• $note", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }
        }
    }
}
