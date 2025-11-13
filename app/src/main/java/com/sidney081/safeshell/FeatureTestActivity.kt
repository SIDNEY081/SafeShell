package com.sidney081.SafeShell

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FeatureTestActivity : AppCompatActivity() {

    private lateinit var remoteConfig: FirebaseRemoteConfig
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // UI elements
    private lateinit var tvFeatures: TextView
    private lateinit var btnRefresh: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feature_test)

        initializeUI()
        setupRemoteConfig()
        setupClickListeners()
        displayCurrentFeatures()
    }

    private fun initializeUI() {
        tvFeatures = findViewById(R.id.tv_features)
        btnRefresh = findViewById(R.id.btn_test_refresh)
        btnBack = findViewById(R.id.btn_back)
    }

    private fun setupRemoteConfig() {
        remoteConfig = FirebaseRemoteConfig.getInstance()
    }

    private fun setupClickListeners() {
        btnRefresh.setOnClickListener {
            refreshFeatures()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun displayCurrentFeatures() {
        val features = """
            🔧 SafeShell Remote Config Test
            ==============================
            
            ✅ VPN Master Toggle: ${remoteConfig.getBoolean("vpn_master_toggle")}
            📝 Welcome Message: "${remoteConfig.getString("home_screen_welcome")}"
            🚧 Maintenance Mode: ${remoteConfig.getBoolean("maintenance_mode_active")}
            ⏰ Scan Interval: ${remoteConfig.getLong("scan_interval_minutes")} minutes
            📱 Min App Version: "${remoteConfig.getString("minimum_app_version")}"
            
            Last Checked: ${java.text.SimpleDateFormat("HH:mm:ss").format(java.util.Date())}
        """.trimIndent()

        tvFeatures.text = features
    }

    private fun refreshFeatures() {
        coroutineScope.launch {
            showLoading("Fetching from Firebase...")

            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    displayCurrentFeatures()
                    showSuccess("Config updated! 🎉")
                    logCurrentValues()
                } else {
                    showError("Fetch failed: ${task.exception?.message}")
                }
            }
        }
    }

    private fun logCurrentValues() {
        println("=== REMOTE CONFIG VALUES ===")
        println("VPN Enabled: ${remoteConfig.getBoolean("vpn_master_toggle")}")
        println("Welcome: ${remoteConfig.getString("home_screen_welcome")}")
        println("Maintenance: ${remoteConfig.getBoolean("maintenance_mode_active")}")
        println("Scan Interval: ${remoteConfig.getLong("scan_interval_minutes")}")
        println("Min Version: ${remoteConfig.getString("minimum_app_version")}")
        println("============================")
    }

    private fun showLoading(message: String) {
        Toast.makeText(this, "⏳ $message", Toast.LENGTH_SHORT).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, "✅ $message", Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, "❌ $message", Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        displayCurrentFeatures()
    }
}