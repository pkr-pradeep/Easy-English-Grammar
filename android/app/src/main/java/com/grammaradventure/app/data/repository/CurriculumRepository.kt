package com.grammaradventure.app.data.repository

import com.grammaradventure.app.data.local.assets.JsonAssetLoader
import com.grammaradventure.app.domain.model.CharacterCatalog
import com.grammaradventure.app.domain.model.CompositionCatalog
import com.grammaradventure.app.domain.model.GrammarCharacter
import com.grammaradventure.app.domain.model.GrammarModuleMeta
import com.grammaradventure.app.domain.model.GrammarWorld
import com.grammaradventure.app.domain.model.ModulesCatalog
import com.grammaradventure.app.domain.model.WorldsCatalog
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CurriculumRepository(private val assetLoader: JsonAssetLoader) {

    private val cacheMutex = Mutex()
    private var cachedWorlds: List<GrammarWorld>? = null
    private var cachedModules: List<GrammarModuleMeta>? = null
    private var cachedCharacters: List<GrammarCharacter>? = null
    private var cachedComposition: CompositionCatalog? = null

    suspend fun getWorlds(): List<GrammarWorld> = cacheMutex.withLock {
        cachedWorlds ?: assetLoader
            .loadAsset<WorldsCatalog>("curriculum/worlds.json")
            .worlds
            .sortedBy { it.order }
            .also { cachedWorlds = it }
    }

    suspend fun getModules(): List<GrammarModuleMeta> = cacheMutex.withLock {
        cachedModules ?: assetLoader
            .loadAsset<ModulesCatalog>("curriculum/modules_metadata.json")
            .modules
            .sortedBy { it.moduleNumber }
            .also { cachedModules = it }
    }

    suspend fun getModulesForWorld(worldId: String): List<GrammarModuleMeta> =
        getModules().filter { it.worldId == worldId }

    suspend fun getModuleById(moduleId: String): GrammarModuleMeta? =
        getModules().find { it.id == moduleId }

    suspend fun getCharacters(): List<GrammarCharacter> = cacheMutex.withLock {
        cachedCharacters ?: assetLoader
            .loadAsset<CharacterCatalog>("characters/characters.json")
            .characters
            .also { cachedCharacters = it }
    }

    suspend fun getCompositionCatalog(): CompositionCatalog = cacheMutex.withLock {
        cachedComposition ?: assetLoader
            .loadAsset<CompositionCatalog>("curriculum/composition_topics.json")
            .also { cachedComposition = it }
    }
}
