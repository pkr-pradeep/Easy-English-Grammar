# English Grammar Adventure

## Android app product brief and master build prompt

**Purpose:** Build an engaging, story-driven English grammar learning
Android application for secondary-school learners, initially targeting
Classes IX and X.

**Core promise:** Do not make learners memorise grammar rules in
isolation. Put them in situations where they need grammar to solve a
mystery, complete a story, win an argument, report an event or help a
character.

------------------------------------------------------------------------

# 1. Product vision

Create an Android application that teaches English grammar through:

-   Funny stories and role-playing.
-   Memorable, peculiar definitions used as memory aids.
-   Accurate formal grammar explanations.
-   Real-life situations and dialogues.
-   Character-led teaching.
-   Interactive checkpoints during lessons.
-   Story completion and problem-solving.
-   Chapter-end MCQ assessments.
-   Explanations revealed after assessment submission.
-   Progress tracking and revision recommendations.

The humour must support understanding, not replace grammatical accuracy.
The target tone is witty, warm, imaginative and suitable for learners
approximately 13--17 years old. Avoid preschool-style writing.

# 2. Reference textbooks

The project is based on four uploaded reference textbooks:

1.  *A New Method English Grammar* --- Classes IX--X, Assam.
2.  *A Practice Book of English Grammar & Composition* --- Class IX,
    Punjab.
3.  *Learn and Practise Grammar* --- Class IX, Board of Secondary
    Education, Odisha.
4.  *Learn and Practise Grammar* --- Class X, Board of Secondary
    Education, Odisha.

The consolidated curriculum in `01_CONSOLIDATED_CURRICULUM.md` is a
proposed cross-textbook organisation, not a claim that every board
prescribes exactly the same syllabus.

**Source integrity requirements**

-   Preserve the original textbook terminology and subtopics.
-   Map every topic to its source board, class, original chapter and
    page range wherever available.
-   Do not invent page references.
-   Identify shared topics and topics unique to one source.
-   Label additional teaching extensions separately.
-   Do not silently omit any source topic.
-   Where source books use different terminology, retain the source term
    and explain the consolidated label.
-   Treat the PDFs as the authority for what those books contain. Do not
    replace their syllabus with a generic grammar syllabus.

# 3. Consolidated learning worlds

The app organises learning into six worlds and 25 modules:

1.  The Sentence Kingdom
2.  The Word Zoo
3.  The Time Machine
4.  The Word Detective Agency
5.  The Sentence Courtroom
6.  The Vocabulary Carnival

The full module list and source mapping are in
`01_CONSOLIDATED_CURRICULUM.md`.

Composition and communication skills should be a separate section rather
than mixed into the grammar modules.

# 4. Character universe: The Grammar Kingdom

Use these Indian characters consistently throughout the application:

  -----------------------------------------------------------------------
  Character               Role                    Personality
  ----------------------- ----------------------- -----------------------
  **Acharya Vidyadhar**   Main grammar teacher    Intelligent, humorous,
                          and mentor              knowledgeable, slightly
                                                  eccentric; explains
                                                  rules with memorable
                                                  examples

  **Chikoo Bandar**       Mischievous monkey and  Overconfident, funny,
                          comic learner           makes deliberate
                                                  grammatical mistakes
                                                  for learners to correct

  **Detective Ira**       Grammar mystery         Curious, logical,
                          investigator            adventurous; introduces
                                                  clues, puzzles and
                                                  error-detection
                                                  challenges

  **Maharaj Vakya**       King of the Grammar     Dramatic, strict about
                          Kingdom                 sentence construction,
                                                  funny in his reactions
                                                  to incorrect sentences

  **Vaani**               Vocabulary expert       Warm, expressive, loves
                                                  unusual words, idioms
                                                  and phrases
  -----------------------------------------------------------------------

### Character design direction

-   Give characters a consistent Indian visual identity.
-   Use attractive, lightweight 2D cartoon illustrations.
-   Use Indian clothing and subtle cultural details.
-   Avoid overly mythological or excessively traditional styling.
-   Characters should appeal to teenagers, not look like preschool
    mascots.
-   Keep facial features, clothing, proportions and colour palettes
    consistent across lessons.
-   Give each character a distinct dialogue voice and personality.
-   Use simple expressions and lightweight animations.
-   Character names and appearances must remain consistent across the
    app.

# 5. Teaching philosophy

Every concept should follow this learning sequence:

1.  Funny introduction or situation.
2.  Peculiar definition as a memory aid.
3.  Correct formal grammar definition.
4.  Simple explanation in learner-friendly English.
5.  Real-life examples.
6.  Correct-versus-incorrect usage.
7.  Story or role-play.
8.  Learner interaction.
9.  Understanding checkpoint.
10. Short recap and revision notes.

Peculiar definitions must never be presented as formal textbook
definitions.

Examples:

-   **Noun:** "A noun is the name tag of the universe. Everything that
    can be named wants one."
-   **Verb:** "A verb is the engine of a sentence. Without it, your
    sentence is a parked vehicle."
-   **Adjective:** "An adjective is a personal stylist. It gives a noun
    its appearance, personality and attitude."
-   **Preposition:** "A preposition is a GPS operator. It tells you
    where something is hiding."
-   **Adverb:** "An adverb is the dramatic director of a verb. It tells
    us how, when, where or to what extent something happens."

Always pair these with accurate formal explanations and examples.

# 6. Story and role-play engine

Lessons should place learners inside a situation. Suggested story
premises:

-   **Nouns:** The Grammar Kingdom has lost its identity cards. Identify
    people, places, animals and things.
-   **Pronouns:** The Royal Court is tired of repeating names. Introduce
    pronouns.
-   **Determiners:** A shopping mall has thousands of items. Choose
    correct quantities and descriptions.
-   **Tense:** A time machine has malfunctioned. Repair past, present
    and future timelines.
-   **Prepositions:** A diamond has disappeared. Use location clues to
    find it.
-   **Adjectives:** A royal fashion competition needs precise
    descriptions.
-   **Adverbs:** Describe the action in a football match.
-   **Subject--verb agreement:** Maharaj Vakya refuses to approve
    sentences because subjects and verbs are arguing.
-   **Active and passive voice:** Explain an incident from two different
    perspectives in a courtroom.
-   **Reported speech:** The Royal News Channel needs accurate reports
    of conversations.
-   **Phrasal verbs:** Decode expressions used by a detective
    organisation.

### Interaction types

Support more than MCQs:

-   Multiple choice.
-   Fill in the blanks.
-   Arrange words in order.
-   Correct grammatical mistakes.
-   Choose a response in a conversation.
-   Complete a dialogue.
-   Identify the grammatical role of a word.
-   Complete a story.
-   Convert sentences.
-   Create a sentence independently.
-   Choose a story continuation.

### Checkpoint behaviour

-   Insert short checkpoints after meaningful concept sections.
-   Evaluate the answer and identify likely misunderstandings.
-   Explain why the answer is right or wrong.
-   Offer a simpler example when needed.
-   Allow another attempt.
-   Adapt subsequent practice to learner performance.
-   Continue the story naturally after the checkpoint.
-   Do not punish learners for mistakes; use them as teaching
    opportunities.

# 7. Standard chapter experience

Each chapter should contain:

1.  **Chapter introduction:** title, world, objectives, characters,
    estimated time and prerequisites.
2.  **Enter the story:** start with dialogue and an illustrated
    situation, not a wall of theory.
3.  **Discover the concept:** peculiar definition, formal definition,
    explanation, examples, incorrect usage and exceptions.
4.  **Interactive story:** learner participates in the situation.
5.  **Understanding checkpoints:** typically 2--4 questions after a
    concept section.
6.  **Grammar notebook:** rules, examples, common mistakes, exceptions
    and memory aids.
7.  **Final assessment:** default 15 MCQs, configurable.

# 8. Final assessment requirements

Default chapter assessment:

-   15 MCQs.
-   Four options per question.
-   One correct answer.
-   Easy, medium and difficult questions.
-   Coverage of all important subtopics.
-   Application-based and story-based questions.
-   Error identification and sentence correction.

**Critical answer-reveal rule:** Do not reveal correct answers while the
learner is attempting the final assessment. The learner must answer all
questions and press Submit first.

After submission, show:

-   Learner's selected answer.
-   Correct answer.
-   Correct/incorrect status.
-   Detailed explanation.
-   Why other options are incorrect where useful.
-   Grammar rule being tested.
-   Relevant lesson reference.

Results should show total questions, correct and incorrect answers,
percentage, time taken, weak concepts and recommended revision. Allow
retries. Chapter unlocking should use configurable mastery criteria, not
mere click-through.

# 9. Android technology and architecture

Build a **native Android application**, not a web app or PWA substitute.

Preferred stack:

-   Kotlin.
-   Jetpack Compose.
-   Material 3.
-   Navigation Compose.
-   Room for local persistence.
-   DataStore for preferences.
-   MVVM architecture.
-   Repository pattern.
-   Kotlin Coroutines and Flow.

Offline-first requirements:

-   Core lessons, stories, questions and explanations must be available
    locally.
-   Normal lesson display must not require an AI API call.
-   Keep educational content separate from UI code.
-   Optional future Gemini integration may support personalised
    practice, but the app must work without it.
-   Never expose API keys in the Android application source.

# 10. Main application screens

1.  Splash screen.
2.  Onboarding.
3.  Learner profile.
4.  Class selection.
5.  Board/syllabus selection.
6.  Home dashboard.
7.  Grammar worlds.
8.  Chapter list.
9.  Story lesson.
10. Interactive checkpoint.
11. Grammar notebook.
12. Final assessment.
13. Assessment results.
14. Revision centre.
15. Progress dashboard.
16. Settings.

### Home dashboard

Show a welcome message, selected class and syllabus, Continue Learning,
six Grammar Worlds, Daily Challenge, chapters completed, learning
streak, overall progress and weak topics for revision.

Use a clean, modern, playful educational design. Avoid clutter, tiny
text and excessive animation. Design for Android phones first.

# 11. Gamification

-   Award XP for completing lessons.
-   Award additional XP for correct checkpoint answers.
-   Give badges for completing grammar worlds.
-   Maintain a learning streak.
-   Provide a progress map.
-   Unlock stories as learners progress.
-   Provide revision missions for weak concepts.

Do not reward random clicking or simply opening lessons. Rewards must
connect to learning. No advertisements, payments or subscription
features in the MVP.

# 12. Progress and persistence

Store locally:

-   Learner profile.
-   Selected class and syllabus.
-   Completed chapters.
-   Lesson progress and last completed section.
-   Checkpoint performance.
-   Assessment attempts.
-   Correct and incorrect answers.
-   Weak grammar concepts.
-   Revision history.
-   XP and badges.

Learners must be able to exit and resume from their last completed
section.

# 13. Content architecture

Use a reusable, versioned JSON-based lesson system. Each lesson should
support:

-   Lesson metadata.
-   Source textbook references.
-   Learning objectives.
-   Prerequisites.
-   Character definitions/references.
-   Story scenes.
-   Dialogue.
-   Grammar explanations.
-   Peculiar definitions.
-   Examples.
-   Checkpoints.
-   Assessment questions.
-   Answer explanations.
-   Revision notes.

Distinguish clearly between: - Textbook-derived content. - Consolidated
content. - Additional examples. - AI-generated practice content.

Validate all educational content for grammatical accuracy before
publication.

# 14. Demonstration chapter

Before creating all 25 modules, implement one complete demonstration
chapter:

**World 3 --- The Time Machine**\
**Chapter --- Tense, Time and Aspect**

Include: - Funny introduction. - Acharya Vidyadhar, Chikoo Bandar and
Detective Ira. - Complete story. - Accurate grammar explanations. -
Present, past and future time expressions. - Simple, progressive,
perfect and perfect progressive aspects. - Examples and common errors. -
At least three interactive checkpoints. - Final 15-question MCQ
assessment. - Answers and explanations revealed only after submission. -
Revision notebook.

The chapter must be appropriate for Classes IX--X and grounded in the
supplied textbooks.

# 15. Development stages

**Stage 1 --- Foundation** - Native Android project. - Navigation and
design system. - Home dashboard. - Six worlds and 25-module
curriculum. - Chapter listing and metadata.

**Stage 2 --- Lesson engine** - Content model. - Character dialogue. -
Story scenes. - Grammar explanations. - Interactive checkpoints.

**Stage 3 --- Assessment** - MCQ engine. - Answer locking until
submission. - Explanations and results. - Retry and weak-topic tracking.

**Stage 4 --- Persistence and rewards** - Room database. - Progress
tracking. - Resume learning. - Gamification. - Revision centre.

**Stage 5 --- Curriculum expansion** - Populate all modules. - Validate
source mappings. - Add stories and exercises. - Test offline operation
and screen sizes.

# 16. Acceptance criteria for the first working MVP

The MVP must demonstrate:

1.  A functioning native Android application.
2.  Six grammar worlds.
3.  All 25 consolidated modules represented.
4.  One fully implemented demonstration chapter.
5.  Story-driven lesson navigation.
6.  Interactive checkpoints and immediate checkpoint feedback.
7.  Final assessment with deferred answer revelation.
8.  Detailed answer explanations.
9.  Local progress persistence.
10. Offline operation.
11. Responsive Android interface.

Do not generate incomplete placeholder functionality and call it
complete.

**First analyse the requirements, propose the architecture and
implementation plan, then build the reusable lesson engine. Validate it
with the demonstration chapter before expanding the curriculum.**
