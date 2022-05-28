package com.sudoajay.namaz_alert.data.proto

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.StatePreferences
import com.sudoajay.namaz_alert.util.PhoneMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProtoManager @Inject constructor (var context: Context){
    val dataStoreStatePreferences : DataStore<StatePreferences> = context.stateDataStore

    suspend fun setDefaultValue(){
        dataStoreStatePreferences.updateData { preferences->
            preferences.toBuilder()
                .setSetPhoneMode(PhoneMode.Vibrate.toString())
                .setFajrTiming(context.getString(R.string.default_prayer_time_proto))
                .setDhuhrTiming(context.getString(R.string.default_prayer_time_proto))
                .setAsrTiming(context.getString(R.string.default_prayer_time_proto))
                .setMaghribTiming(context.getString(R.string.default_prayer_time_proto))
                .setIshaTiming(context.getString(R.string.default_prayer_time_proto))
                .setIsWorkMangerRunning(false)
                .setIsWorkMangerCancel(false)
                .setNotificationRingtone(0)
                .setIsPermissionAsked(false)
                .build()
        }
    }

    suspend fun setPhoneMode(phoneMode: String) {
        dataStoreStatePreferences.updateData { preferences ->
            preferences.toBuilder()
                .setSetPhoneMode(phoneMode)
                .build()
        }
    }

    suspend fun setFajrTiming(fajrTiming: String) {
        dataStoreStatePreferences.updateData { preferences ->
            preferences.toBuilder()
                .setFajrTiming(fajrTiming)
                .build()
        }
    }

    suspend fun setDhuhrTiming(dhuhrTiming: String) {
        dataStoreStatePreferences.updateData { preferences ->
            preferences.toBuilder()
                .setDhuhrTiming(dhuhrTiming)
                .build()
        }
    }

    suspend fun setAsrTiming(asrTiming: String) {
        dataStoreStatePreferences.updateData { preferences ->
            preferences.toBuilder()
                .setAsrTiming(asrTiming)
                .build()
        }
    }

    suspend fun setMaghribTiming(maghribTiming: String) {
        dataStoreStatePreferences.updateData { preferences ->
            preferences.toBuilder()
                .setMaghribTiming(maghribTiming)
                .build()
        }
    }
    suspend fun setIshaTiming(ishaTiming: String) {
        dataStoreStatePreferences.updateData { preferences ->
            preferences.toBuilder()
                .setIshaTiming(ishaTiming)
                .build()
        }
    }

    suspend fun setIsWorkMangerRunning(isWorkMangerRunning:Boolean){
        dataStoreStatePreferences.updateData { preferences ->
            preferences.toBuilder()
                .setIsWorkMangerRunning(isWorkMangerRunning)
                .build()
        }
    }

    suspend fun setIsWorkMangerCancel(isWorkMangerCancel:Boolean){
        dataStoreStatePreferences.updateData { preferences ->
            preferences.toBuilder()
                .setIsWorkMangerCancel(isWorkMangerCancel)
                .build()
        }
    }
    suspend fun setPreviousMode(phoneMode: String){
        dataStoreStatePreferences.updateData { preferences ->
            preferences.toBuilder()
                .setPreviousPhoneMode(phoneMode)
                .build()
        }
    }

    suspend fun setIsPermissionAsked(isPermissionAsked:Boolean){
        dataStoreStatePreferences.updateData { preferences ->
            preferences.toBuilder()
                .setIsPermissionAsked(isPermissionAsked)
                .build()
        }
    }



    suspend fun setNotificationRingtone(notificationRingtone: Int){
        dataStoreStatePreferences.updateData { preferences ->
            preferences.toBuilder()
                .setNotificationRingtone(notificationRingtone)
                .build()
        }
    }

    suspend fun fetchInitialPreferences() = dataStoreStatePreferences.data.first()




    companion object {
        private const val DATA_STORE_FILE_NAME = "state_prefs.pb"

        private val Context.stateDataStore: DataStore<StatePreferences> by dataStore(
            fileName = DATA_STORE_FILE_NAME,
            serializer = StatePreferencesSerializer
        )

    }

}





