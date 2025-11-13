import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.tasks.await
import android.content.Context
import android.util.Log

class FeatureFlagManager private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: FeatureFlagManager? = null

        fun getInstance(context: Context): FeatureFlagManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FeatureFlagManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val remoteConfig = Firebase.remoteConfig
    private val sharedPrefs = context.getSharedPreferences("safeshell_features", Context.MODE_PRIVATE)

    // Define all your feature flags here
    object Features {
        const val EMERGENCY_CONTACTS = "enable_emergency_contacts"
        const val LOCATION_SHARING = "enable_location_sharing"
        const val SAFETY_TIMER = "enable_safety_timer"
        const val LOCATION_HISTORY = "enable_location_history"
        const val QUICK_SOS = "enable_quick_sos"
        const val AUDIO_RECORDING = "enable_audio_recording"
    }

    // Default values for features
    private val defaultFeatureValues = mapOf(
        Features.EMERGENCY_CONTACTS to true,
        Features.LOCATION_SHARING to true,
        Features.SAFETY_TIMER to false,
        Features.LOCATION_HISTORY to true,
        Features.QUICK_SOS to true,
        Features.AUDIO_RECORDING to false
    )

    init {
        setupRemoteConfig()
    }

    private fun setupRemoteConfig() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // 1 hour minimum
            fetchTimeoutInSeconds = 30
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Set default values
        val defaults = defaultFeatureValues.mapValues { it.value.toString() }
        remoteConfig.setDefaultsAsync(defaults)
    }

    suspend fun fetchFeatureFlags(): Boolean {
        return try {
            val fetchResult = remoteConfig.fetchAndActivate().await()
            if (fetchResult) {
                Log.d("FeatureFlagManager", "Successfully fetched and activated config")
                saveFeaturesToPrefs()
            }
            fetchResult
        } catch (e: Exception) {
            Log.e("FeatureFlagManager", "Failed to fetch feature flags", e)
            false
        }
    }

    fun isFeatureEnabled(featureKey: String): Boolean {
        return try {
            remoteConfig.getBoolean(featureKey)
        } catch (e: Exception) {
            // Fallback to shared preferences or default
            sharedPrefs.getBoolean(featureKey, defaultFeatureValues[featureKey] ?: false)
        }
    }

    fun getFeatureConfigString(featureKey: String): String {
        return remoteConfig.getString(featureKey)
    }

    fun getFeatureConfigLong(featureKey: String): Long {
        return remoteConfig.getLong(featureKey)
    }

    private fun saveFeaturesToPrefs() {
        val editor = sharedPrefs.edit()
        defaultFeatureValues.keys.forEach { key ->
            editor.putBoolean(key, remoteConfig.getBoolean(key))
        }
        editor.apply()
    }

    fun forceFetch(completion: (Boolean) -> Unit) {
        remoteConfig.fetch(0) // 0 means no cache
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    remoteConfig.activate().addOnCompleteListener { activationTask ->
                        if (activationTask.isSuccessful) {
                            saveFeaturesToPrefs()
                            completion(true)
                        } else {
                            completion(false)
                        }
                    }
                } else {
                    completion(false)
                }
            }
    }
}