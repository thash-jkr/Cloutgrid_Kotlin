package com.cloutgrid.androidapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp // 🚀 This is the magic switch for Hilt!
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        // This runs ONCE when the app boots up.
        // Perfect place for initializing global tools later (like Firebase or analytics)
    }
}