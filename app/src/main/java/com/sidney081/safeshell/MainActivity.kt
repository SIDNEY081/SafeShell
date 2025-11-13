package com.sidney081.SafeShell

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class MainActivity : AppCompatActivity() {

    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        initializeRemoteConfig()
        setupUI()
        loadRemoteConfig()
    }

    private fun initializeRemoteConfig() {
        remoteConfig = FirebaseRemoteConfig.getInstance()

        // FIXED: Use FirebaseRemoteConfigSettings.Builder() instead of remoteConfigSettings
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0) // Development mode
            .setFetchTimeoutInSeconds(60)
            .build()

        remoteConfig.setConfigSettingsAsync(configSettings)

        // Set default values
        val defaultConfigMap = mapOf(
            "vpn_master_toggle" to true,
            "home_screen_welcome" to "Welcome to SafeShell!",
            "maintenance_mode_active" to false,
            "scan_interval_minutes" to 60,
            "minimum_app_version" to "1.0.0"
        )
        remoteConfig.setDefaultsAsync(defaultConfigMap)
    }

    private fun setupUI() {
        val welcomeText = findViewById<TextView>(R.id.tv_welcome)
        val testConfigButton = findViewById<Button>(R.id.btn_test_config)
        val logoutButton = findViewById<Button>(R.id.btn_logout)

        testConfigButton.setOnClickListener {
            startActivity(Intent(this, FeatureTestActivity::class.java))
        }

        logoutButton.setOnClickListener {
            // Logout and return to login
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Set user email if available
        val currentUser = auth.currentUser
        currentUser?.email?.let { email ->
            val userText = findViewById<TextView>(R.id.tv_user_email)
            userText?.text = "Logged in as: $email"
        }
    }

    private fun loadRemoteConfig() {
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                updateUIWithConfig()
            }
        }
    }

    private fun updateUIWithConfig() {
        val welcomeText = findViewById<TextView>(R.id.tv_welcome)
        val vpnButton = findViewById<Button>(R.id.btn_vpn)

        welcomeText?.text = remoteConfig.getString("home_screen_welcome")
        vpnButton?.isEnabled = remoteConfig.getBoolean("vpn_master_toggle")

        // Check for maintenance mode
        if (remoteConfig.getBoolean("maintenance_mode_active")) {
            showMaintenanceMode()
        }
    }

    private fun showMaintenanceMode() {
        val maintenanceLayout = findViewById<android.view.ViewGroup>(R.id.layout_maintenance)
        val mainContentLayout = findViewById<android.view.ViewGroup>(R.id.layout_main_content)

        maintenanceLayout?.visibility = android.view.View.VISIBLE
        mainContentLayout?.visibility = android.view.View.GONE
    }

    override fun onResume() {
        super.onResume()
        // Refresh config when returning to main activity
        loadRemoteConfig()
    }
}