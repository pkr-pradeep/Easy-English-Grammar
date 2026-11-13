package com.grammaradventure.app.data.local.assets

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class JsonAssetLoader(private val context: Context) {

    val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    suspend inline fun <reified T> loadAsset(assetPath: String): T =
        withContext(Dispatchers.IO) {
            val raw = context.assets.open(assetPath).bufferedReader().use { it.readText() }
            json.decodeFromString<T>(raw)
        }
}
