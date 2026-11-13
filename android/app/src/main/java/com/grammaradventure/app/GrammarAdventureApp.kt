package com.grammaradventure.app

import android.app.Application
import com.grammaradventure.app.di.AppContainer

class GrammarAdventureApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
