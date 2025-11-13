import com.google.gson.Gson

data class AppFeaturesConfig(
    val features: Map<String, FeatureConfig>,
    val uiSettings: UISettings,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class FeatureConfig(
    val enabled: Boolean,
    val version: String = "1.0",
    val config: Map<String, Any> = emptyMap()
)

data class UISettings(
    val showNewOnboarding: Boolean = false,
    val enableDarkMode: Boolean = true,
    val maxEmergencyContacts: Int = 5,
    val locationUpdateInterval: Long = 30000, // 30 seconds
    val safetyTimerMaxDuration: Int = 3600 // 1 hour in seconds
)

// Extension to parse JSON config
fun String.toFeatureConfig(): FeatureConfig? {
    return try {
        Gson().fromJson(this, FeatureConfig::class.java)
    } catch (e: Exception) {
        null
    }
}