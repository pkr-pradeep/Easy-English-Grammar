package com.grammaradventure.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CharacterCatalog(
    val schemaVersion: Int,
    val characters: List<GrammarCharacter>
)

@Serializable
data class GrammarCharacter(
    val id: String,
    val name: String,
    val role: String,
    val personality: String,
    val dialogueStyle: String,
    val signatureQuote: String,
    val visualIdentity: CharacterVisualIdentity
)

@Serializable
data class CharacterVisualIdentity(
    val outfit: String,
    val accentColorHex: String,
    val secondaryColorHex: String,
    val expressions: List<String>
)
