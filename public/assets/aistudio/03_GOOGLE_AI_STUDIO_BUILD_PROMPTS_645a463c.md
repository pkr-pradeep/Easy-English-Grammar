# Google AI Studio --- Development Prompt Sequence

## English Grammar Adventure Android app

Use the master prompt in `00_PROJECT_OVERVIEW_AND_MASTER_PROMPT.md`
first. Then use these prompts one at a time. Do not ask the model to
generate the entire application and all 25 full chapters in a single
response.

------------------------------------------------------------------------

# Prompt 1 --- Build the Android foundation

Build the native Android project foundation for English Grammar
Adventure.

Use Kotlin, Jetpack Compose, Material 3, Navigation Compose, MVVM, Room,
DataStore, Coroutines and Flow.

Implement: - Project structure. - Theme and reusable design system. -
Navigation. - Home dashboard. - Six grammar worlds. - All 25
consolidated modules as curriculum metadata. - Class and syllabus
selection. - Chapter listing. - Prerequisite metadata.

Do not generate all lesson content yet.

Ensure the application builds and navigation works. Do not use a web app
or PWA as a substitute for the native Android project.

# Prompt 2 --- Build the reusable lesson engine

Implement a reusable story-driven grammar lesson engine.

Support: - Character dialogue. - Story scenes. - Peculiar definitions. -
Formal grammar definitions. - Explanations and examples. - Correct and
incorrect usage. - Branching interactions. - Multiple interaction
types. - Checkpoints. - Feedback and retries. - Lesson progress and
resume.

Keep lesson content separate from UI code using a validated JSON schema.

Implement the complete demonstration lesson: World 3, The Time Machine,
Tense, Time and Aspect.

Use Acharya Vidyadhar, Chikoo Bandar, Detective Ira, Maharaj Vakya and
Vaani consistently.

# Prompt 3 --- Build the assessment engine

Implement the chapter assessment engine.

Requirements: - Default 15 MCQs per chapter, configurable. - Four
options per question. - One correct answer. - Mixed difficulty. -
Randomised question order. - Persisted assessment attempts. - Learner
can answer all questions before submission. - Do not reveal correct
answers before submission. - Lock the submitted attempt against
accidental changes. - After submission, show selected answer, correct
answer, explanation, grammar rule and useful explanations for
distractors. - Calculate score and percentage accurately. - Show time
taken, weak topics and revision recommendations. - Allow retry and
preserve attempt history.

Add tests for scoring and answer-reveal behaviour.

# Prompt 4 --- Populate the consolidated curriculum

Populate all 25 consolidated grammar modules using the four supplied
textbooks.

For each topic: - Map the source board. - Map the source class. - Map
the original textbook chapter and page references wherever verified. -
Identify shared and source-specific topics. - Preserve important
subtopics. - Identify prerequisites. - Create accurate learner-friendly
explanations. - Add a peculiar definition as a memory aid. - Add a story
or role-play. - Add examples and common errors. - Add checkpoints and a
chapter assessment.

Do not fabricate page references or textbook claims.

Do not use generic placeholder content. If a source detail cannot be
verified, mark it for review.

Work module by module. Validate content before moving to the next
module.

# Prompt 5 --- Add Indian character identity

Update the character universe with these exact names:

1.  Acharya Vidyadhar --- grammar teacher and mentor.
2.  Chikoo Bandar --- mischievous monkey who makes funny grammar
    mistakes.
3.  Detective Ira --- investigator who solves grammar mysteries.
4.  Maharaj Vakya --- king who maintains sentence discipline.
5.  Vaani --- vocabulary expert.

Give all characters a consistent Indian visual identity using
teen-friendly 2D illustrations and subtle Indian clothing details.

Avoid preschool mascot styling and excessive mythological styling.

Keep facial features, clothing, colours, proportions, personality and
dialogue style consistent across lessons.

Update existing scripts and references without changing the grammar
curriculum.

# Prompt 6 --- Add progress and revision

Implement: - Local learner progress. - Resume lesson. - Completed
chapters. - Checkpoint history. - Assessment history. - Weak topic
detection. - Revision missions. - XP and badges. - Learning streak. -
World completion progress.

Do not award progress for merely opening a chapter. Tie rewards to
completed learning activities.

Ensure all progress is available offline.

# Prompt 7 --- Quality audit

Perform a complete quality audit.

Test: - Native Android build. - Navigation. - Different Android screen
sizes. - Offline lessons. - Local persistence. - Resume lesson. -
Checkpoint feedback. - Assessment answer locking. - Deferred answer
revelation. - Score calculation. - Answer explanations. - Prerequisite
logic. - Revision recommendations. - Content traceability. - Grammar
accuracy.

Fix actual defects rather than merely listing them.

Do not claim a feature is complete unless it is implemented and tested.

------------------------------------------------------------------------

# Recommended workflow

1.  Upload the four source PDFs to the Google AI Studio project.
2.  Upload `00_PROJECT_OVERVIEW_AND_MASTER_PROMPT.md`.
3.  Upload `01_CONSOLIDATED_CURRICULUM.md`.
4.  Upload `02_CHARACTERS_STORIES_AND_LESSON_DESIGN.md`.
5.  Upload this prompt sequence.
6.  Ask the model to analyse all files and propose the Android
    architecture.
7.  Execute prompts 1--3 and test the foundation.
8.  Populate the curriculum module by module.
9.  Run the quality audit.

**Important:** The Markdown files define product requirements and
content structure. They do not replace the original textbooks. Keep the
source PDFs available to the AI Studio project so it can verify chapter
details and page references.
