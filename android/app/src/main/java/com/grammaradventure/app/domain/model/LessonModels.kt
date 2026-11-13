package com.grammaradventure.app.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ContentProvenance {
    SOURCE_TEXTBOOK,
    CONSOLIDATED,
    ADDITIONAL_EXAMPLE,
    AI_PRACTICE
}

@Serializable
enum class InteractionType {
    MULTIPLE_CHOICE,
    FILL_IN_BLANK,
    ARRANGE_WORDS,
    ERROR_CORRECTION,
    IDENTIFY_ROLE,
    SENTENCE_CONVERSION
}

@Serializable
enum class QuestionDifficulty {
    EASY,
    MEDIUM,
    HARD
}

@Serializable
data class LessonContent(
    val schemaVersion: Int,
    val moduleId: String,
    val worldId: String,
    val title: String,
    val storyTitle: String,
    val estimatedMinutes: Int,
    val charactersInvolved: List<String>,
    val steps: List<LessonStep>,
    val grammarNotebook: GrammarNotebook,
    val assessment: ChapterAssessmentSpec
)

@Serializable
sealed interface LessonStep {
    val stepId: String
    val stepTitle: String
    val provenance: ContentProvenance
}

@Serializable
@SerialName("story_scene")
data class StorySceneStep(
    override val stepId: String,
    override val stepTitle: String,
    override val provenance: ContentProvenance = ContentProvenance.CONSOLIDATED,
    val sceneTag: String,
    val settingDescription: String,
    val dialogues: List<DialogueLine>,
    val timelinePanels: List<TimelinePanel> = emptyList(),
    val storyCallout: String? = null
) : LessonStep

@Serializable
data class DialogueLine(
    val characterId: String,
    val expression: String = "neutral",
    val line: String,
    val note: String? = null
)

@Serializable
data class TimelinePanel(
    val timeLabel: String,
    val exampleSentence: String,
    val tenseUsed: String,
    val explanation: String
)

@Serializable
@SerialName("concept_discovery")
data class ConceptDiscoveryStep(
    override val stepId: String,
    override val stepTitle: String,
    override val provenance: ContentProvenance = ContentProvenance.CONSOLIDATED,
    val subtopicId: String,
    val subtopicTitle: String,
    val memoryAidLabel: String = "Memory Aid (Peculiar Definition — Not a Formal Rule)",
    val peculiarDefinition: String,
    val formalDefinition: String,
    val simpleExplanation: String,
    val structurePatterns: List<StructurePattern> = emptyList(),
    val realLifeExamples: List<UsageExample> = emptyList(),
    val correctVsIncorrect: List<ErrorComparison> = emptyList(),
    val exceptions: List<String> = emptyList()
) : LessonStep

@Serializable
data class StructurePattern(
    val formName: String,
    val formula: String,
    val sample: String
)

@Serializable
data class UsageExample(
    val sentence: String,
    val contextNote: String,
    val provenance: ContentProvenance = ContentProvenance.ADDITIONAL_EXAMPLE
)

@Serializable
data class ErrorComparison(
    val incorrectSentence: String,
    val correctSentence: String,
    val whyItHappens: String,
    val culpritCharacterId: String = "CHIKOO_BANDAR"
)

@Serializable
@SerialName("interactive_checkpoint")
data class InteractiveCheckpointStep(
    override val stepId: String,
    override val stepTitle: String,
    override val provenance: ContentProvenance = ContentProvenance.CONSOLIDATED,
    val checkpointId: String,
    val subtopicId: String,
    val subtopicTitle: String,
    val interactionType: InteractionType,
    val characterId: String = "DETECTIVE_IRA",
    val storyPrompt: String,
    val questionPrompt: String,
    val options: List<String> = emptyList(),
    val wordTokens: List<String> = emptyList(),
    val sentenceTemplate: String? = null,
    val acceptedAnswers: List<String>,
    val hint: String,
    val feedbackCorrect: String,
    val feedbackWrongDefault: String,
    val feedbackWrongByOption: Map<String, String> = emptyMap(),
    val simplerExample: String,
    val xpReward: Int = 20
) : LessonStep

@Serializable
data class GrammarNotebook(
    val summaryRules: List<NotebookRule>,
    val memoryAids: List<String>,
    val commonMistakes: List<ErrorComparison>,
    val exceptions: List<String>,
    val textbookNotes: List<String>
)

@Serializable
data class NotebookRule(
    val heading: String,
    val ruleSummary: String,
    val formulaOrPattern: String,
    val example: String
)

@Serializable
data class ChapterAssessmentSpec(
    val defaultQuestionCount: Int = 15,
    val passingPercentage: Int = 70,
    val questions: List<AssessmentQuestion>
)

@Serializable
data class AssessmentQuestion(
    val id: String,
    val subtopicId: String,
    val subtopicTitle: String,
    val difficulty: QuestionDifficulty,
    val storyContext: String? = null,
    val questionText: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String,
    val distractorRationales: List<String> = emptyList(),
    val grammarRule: String,
    val lessonStepRef: String,
    val provenance: ContentProvenance = ContentProvenance.CONSOLIDATED
)
