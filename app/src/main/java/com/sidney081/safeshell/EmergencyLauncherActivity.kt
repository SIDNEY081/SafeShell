package com.sidney081.SafeShell

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

class EmergencyLauncherActivity : AppCompatActivity() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    // Banking app keywords (will be detected and hidden)
    private val bankingKeywords = listOf(
        "bank", "pay", "wallet", "finance", "investment", "credit",
        "loan", "mortgage", "stock", "trading", "crypto", "bitcoin",
        "capital", "wealth", "money", "transfer", "venmo", "paypal",
        "cashapp", "zelle", "chase", "boa", "wells fargo", "savings"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_launcher)

        setupBiometricAuth()
        loadSafeApps()
        setupEmergencyUI()
    }

    private fun setupEmergencyUI() {
        // Show emergency message
        Toast.makeText(this, "🔒 Emergency Mode Active", Toast.LENGTH_LONG).show()

        //  emergency contacts, fake screens, etc.
    }

    private fun loadSafeApps() {
        val safeApps = getInstalledApps().filter { app ->
            // Filter out banking apps
            !isBankingApp(app)
        }

        val gridView = findViewById<GridView>(R.id.appsGridView)
        gridView.adapter = AppAdapter(this, safeApps)

        gridView.setOnItemClickListener { _, _, position, _ ->
            val app = safeApps[position]
            launchApp(app.packageName)
        }
    }

    private fun getInstalledApps(): List<AppInfo> {
        val apps = mutableListOf<AppInfo>()
        val pm = packageManager

        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolvedApps = pm.queryIntentActivities(mainIntent, 0)
        for (ri in resolvedApps) {
            val app = AppInfo(
                name = ri.loadLabel(pm).toString(),
                packageName = ri.activityInfo.packageName,
                icon = ri.loadIcon(pm)
            )
            apps.add(app)
        }

        return apps.sortedBy { it.name }
    }

    private fun isBankingApp(app: AppInfo): Boolean {
        val appName = app.name.lowercase()
        val packageName = app.packageName.lowercase()

        return bankingKeywords.any { keyword ->
            appName.contains(keyword) || packageName.contains(keyword)
        }
    }

    private fun launchApp(packageName: String) {
        try {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            startActivity(launchIntent)
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot open app", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBiometricAuth() {
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // Exit emergency mode - go back to normal launcher
                    exitEmergencyMode()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Stay in emergency mode if auth fails
                    Toast.makeText(this@EmergencyLauncherActivity,
                        "Authentication required to exit emergency mode",
                        Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Exit Emergency Mode")
            .setSubtitle("Biometric authentication required")
            .setNegativeButtonText("Cancel")
            .build()
    }

    override fun onBackPressed() {
        // Trigger biometric auth when back button is pressed
        val biometricManager = BiometricManager.from(this)
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
            BiometricManager.BIOMETRIC_SUCCESS) {
            biometricPrompt.authenticate(promptInfo)
        } else {
            Toast.makeText(this, "Authentication required to exit", Toast.LENGTH_SHORT).show()
        }
    }

    private fun exitEmergencyMode() {
        // Launch the default launcher to exit emergency mode
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}

// Data class for app information
data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: android.graphics.drawable.Drawable
)