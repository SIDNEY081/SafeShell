package com.sidney081.SafeShell

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class SafeShellApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Pre-load Remote Config
        initializeRemoteConfig()
    }

    private fun initializeRemoteConfig() {
        val remoteConfig = FirebaseRemoteConfig.getInstance()

        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600) // 1 hour for production
            .setFetchTimeoutInSeconds(60)
            .build()

        remoteConfig.setConfigSettingsAsync(configSettings)

        // Set default values
        val defaultConfigMap = mapOf(
            "vpn_master_toggle" to true,
            "home_screen_welcome" to "Welcome to SafeShell! Your privacy is protected.",
            "maintenance_mode_active" to false,
            "scan_interval_minutes" to 60,
            "minimum_app_version" to "1.0.0"
        )
        remoteConfig.setDefaultsAsync(defaultConfigMap)

        // Fetch config in background
        remoteConfig.fetch().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                remoteConfig.activate()
            }
        }
    }
}