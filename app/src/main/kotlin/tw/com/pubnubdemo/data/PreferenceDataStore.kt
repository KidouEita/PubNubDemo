package tw.com.pubnubdemo.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Properties can be checked in "/data/data/{packageName}/files/datastore/settings.preferences_pb" with Device File Explorer
 * */
@Reusable
class PreferencesDataStore @Inject constructor(@ApplicationContext private val context: Context) {

    suspend fun setFCMToken(newFCMToken: String) {
        context.dataStore.edit { settings -> settings[FCM_TOKEN] = newFCMToken }
    }

    fun getFCMToken(): String? = runBlocking { context.dataStore.data.first()[FCM_TOKEN] }

    suspend fun setUserId(newUserId: String) {
        context.dataStore.edit { settings -> settings[USER_ID] = newUserId }
    }

    fun getUserId() = context.dataStore.data.map { settings -> settings[USER_ID] }

    fun getChannels() = context.dataStore.data.map { settings -> settings[CHANNELS]?.toList() }

    suspend fun setChannels(newChannels: List<String>) {
        context.dataStore.edit { settings -> settings[CHANNELS] = newChannels.toSet() }
    }

    fun isLoggedIn() = runBlocking { !context.dataStore.data.first()[USER_ID].isNullOrBlank() }

    suspend fun clear() {
        context.dataStore.edit { settings ->
            settings.clear()
        }
    }

    companion object {
        private val FCM_TOKEN = stringPreferencesKey("fcm_token")
        private val USER_ID = stringPreferencesKey("user_id")
        private val CHANNELS = stringSetPreferencesKey("channels")
    }
}