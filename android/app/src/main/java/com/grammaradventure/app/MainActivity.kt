package com.grammaradventure.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.grammaradventure.app.ui.navigation.AppNavHost
import com.grammaradventure.app.ui.theme.EnglishGrammarAdventureTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as GrammarAdventureApp).container
        setContent {
            EnglishGrammarAdventureTheme {
                AppNavHost(appContainer = appContainer)
            }
        }
    }
}
