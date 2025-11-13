package com.sidney081.SafeShell

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class DynamicUIManager(private val remoteConfig: FirebaseRemoteConfig) {

    fun setupMainActivityUI(activity: AppCompatActivity) {
        // Show/hide features based on Remote Config
        updateVPNUI(activity)
        updateWelcomeMessageUI(activity)
        updateMaintenanceModeUI(activity)
        updateSafetyFeaturesUI(activity)
    }

    private fun updateVPNUI(activity: AppCompatActivity) {
        val vpnButton = findViewByString<Button>(activity, "btn_vpn")
        vpnButton?.visibility = if (remoteConfig.getBoolean("vpn_master_toggle")) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun updateWelcomeMessageUI(activity: AppCompatActivity) {
        val welcomeText = findViewByString<TextView>(activity, "tv_welcome")
        welcomeText?.text = remoteConfig.getString("home_screen_welcome")
    }

    private fun updateMaintenanceModeUI(activity: AppCompatActivity) {
        val maintenanceLayout = findViewByString<LinearLayout>(activity, "layout_maintenance")
        val mainContentLayout = findViewByString<ViewGroup>(activity, "layout_main_content")

        val isMaintenanceMode = remoteConfig.getBoolean("maintenance_mode_active")

        maintenanceLayout?.visibility = if (isMaintenanceMode) View.VISIBLE else View.GONE
        mainContentLayout?.visibility = if (isMaintenanceMode) View.GONE else View.VISIBLE

        if (isMaintenanceMode) {
            showMaintenanceMessage(activity)
        }
    }

    private fun updateSafetyFeaturesUI(activity: AppCompatActivity) {
        val scanInterval = remoteConfig.getLong("scan_interval_minutes")
        val scanButton = findViewByString<Button>(activity, "btn_scan")

        scanButton?.text = "Scan Now (Every ${scanInterval}min)"

        // Update min version check
        checkAppVersion(activity)
    }

    private fun showMaintenanceMessage(activity: AppCompatActivity) {
        val maintenanceMessage = findViewByString<TextView>(activity, "tv_maintenance_message")
        maintenanceMessage?.text = "SafeShell is undergoing maintenance. Please check back soon."
    }

    private fun checkAppVersion(activity: AppCompatActivity) {
        val minVersion = remoteConfig.getString("minimum_app_version")
        val currentVersion = getAppVersion(activity)

        if (isVersionOutdated(currentVersion, minVersion)) {
            showUpdateRequiredDialog(activity)
        }
    }

    // Helper function to find views by string name (avoids R.id issues)
    private inline fun <reified T : View> findViewByString(activity: AppCompatActivity, viewName: String): T? {
        return try {
            val resourceId = activity.resources.getIdentifier(viewName, "id", activity.packageName)
            if (resourceId != 0) {
                activity.findViewById(resourceId)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Add dynamic button if feature is enabled but doesn't exist in layout
    fun addDynamicFeatureButton(activity: AppCompatActivity, featureName: String, buttonText: String, onClick: () -> Unit) {
        val mainLayout = findViewByString<ViewGroup>(activity, "main_layout")
        mainLayout?.let { layout ->
            // Check if button already exists
            val existingButtonId = activity.resources.getIdentifier("btn_$featureName", "id", activity.packageName)
            if (existingButtonId == 0) {
                val newButton = Button(activity).apply {
                    text = buttonText
                    id = View.generateViewId() // Generate unique ID
                    setOnClickListener { onClick() }
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(16, 8, 16, 8)
                    }
                }
                layout.addView(newButton)
            }
        }
    }

    private fun getAppVersion(activity: AppCompatActivity): String {
        return try {
            val packageInfo = activity.packageManager.getPackageInfo(activity.packageName, 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }

    // FIXED VERSION: Proper version comparison
    private fun isVersionOutdated(current: String, minRequired: String): Boolean {
        return try {
            compareVersions(current, minRequired) < 0
        } catch (e: Exception) {
            false
        }
    }

    // Proper version string comparison
    private fun compareVersions(version1: String, version2: String): Int {
        val v1Parts = version1.split('.').map { it.toInt() }
        val v2Parts = version2.split('.').map { it.toInt() }

        val maxLength = maxOf(v1Parts.size, v2Parts.size)

        for (i in 0 until maxLength) {
            val v1Part = v1Parts.getOrElse(i) { 0 }
            val v2Part = v2Parts.getOrElse(i) { 0 }

            when {
                v1Part < v2Part -> return -1
                v1Part > v2Part -> return 1
            }
        }
        return 0
    }

    private fun showUpdateRequiredDialog(activity: AppCompatActivity) {
        androidx.appcompat.app.AlertDialog.Builder(activity)
            .setTitle("Update Required")
            .setMessage("Please update SafeShell to the latest version for security improvements.")
            .setPositiveButton("Update") { _, _ ->
                // Open app store
                // Intent(Intent.ACTION_VIEW, "market://details?id=${activity.packageName}".toUri())
            }
            .setCancelable(false)
            .show()
    }

    // Method to update UI when Remote Config changes
    fun onConfigUpdated(activity: AppCompatActivity) {
        setupMainActivityUI(activity)
    }
}