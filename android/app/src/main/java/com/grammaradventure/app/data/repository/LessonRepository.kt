package com.grammaradventure.app.data.repository

import com.grammaradventure.app.data.local.assets.JsonAssetLoader
import com.grammaradventure.app.domain.model.LessonContent
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class LessonRepository(private val assetLoader: JsonAssetLoader) {

    private val lessonAssetRegistry: Map<String, String> = mapOf(
        "MOD_01" to "lessons/world1_mod01_sentence_basics.json",
        "MOD_02" to "lessons/world1_mod02_subject_predicate.json",
        "MOD_03" to "lessons/world1_mod03_sentence_types.json",
        "MOD_04" to "lessons/world1_mod04_phrases_clauses.json",
        "MOD_05" to "lessons/world2_mod05_parts_of_speech_nouns.json",
        "MOD_06" to "lessons/world2_mod06_pronouns.json",
        "MOD_07" to "lessons/world2_mod07_determiners_articles.json",
        "MOD_08" to "lessons/world2_mod08_countable_uncountable.json",
        "MOD_09" to "lessons/world2_mod09_adjectives.json",
        "MOD_10" to "lessons/world3_mod10_verbs_verb_groups.json",
        "MOD_11" to "lessons/world3_mod11_finite_non_finite.json",
        "MOD_12" to "lessons/world3_mod12_auxiliaries_modals_special.json",
        "MOD_13" to "lessons/world3_mod13_tense_time_aspect.json",
        "MOD_14" to "lessons/world3_mod14_subject_verb_agreement.json",
        "MOD_15" to "lessons/world4_mod15_noun_phrases_modifiers.json",
        "MOD_16" to "lessons/world4_mod16_predicate_phrases.json",
        "MOD_17" to "lessons/world4_mod17_adverbs_adverbials.json",
        "MOD_18" to "lessons/world4_mod18_prepositions.json",
        "MOD_19" to "lessons/world4_mod19_conjunctions.json",
        "MOD_20" to "lessons/world5_mod20_negatives_interrogatives.json",
        "MOD_21" to "lessons/world5_mod21_active_passive_voice.json",
        "MOD_22" to "lessons/world5_mod22_reported_speech.json",
        "MOD_23" to "lessons/world5_mod23_transformation_conditionals.json",
        "MOD_24" to "lessons/world6_mod24_punctuation_word_formation.json",
        "MOD_25" to "lessons/world6_mod25_vocabulary_idioms_phrasal_verbs.json"
    )

    private val cacheMutex = Mutex()
    private val cachedLessons = mutableMapOf<String, LessonContent>()

    fun hasLessonAsset(moduleId: String): Boolean = lessonAssetRegistry.containsKey(moduleId)

    suspend fun getLessonForModule(moduleId: String): LessonContent? = cacheMutex.withLock {
        cachedLessons[moduleId]?.let { return@withLock it }
        val assetPath = lessonAssetRegistry[moduleId] ?: return@withLock null
        return@withLock assetLoader.loadAsset<LessonContent>(assetPath).also { loaded ->
            cachedLessons[moduleId] = loaded
        }
    }
}
