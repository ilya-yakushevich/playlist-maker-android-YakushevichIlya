package com.example.playlistmaker.ui

import android.app.Application
import com.example.playlistmaker.dependencyobject.DependencyContainer

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DependencyContainer.provideDatabase(this)
    }
}