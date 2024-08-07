package com.sudoajay.triumph_path.data.proto

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.sudoajay.triumph_path.StatePreferences
import com.sudoajay.triumph_path.R

import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ProtoManager @Inject constructor (var context: Context){
    private val dataStoreStatePreferences : DataStore<StatePreferences> = context.stateDataStore

    suspend fun setDefaultValue(){
        dataStoreStatePreferences.updateData { preferences->
            preferences.toBuilder()
                .setFajrTiming(context.getString(R.string.default_prayer_time_proto))
                .setDhuhrTiming(context.getString(R.string.default_prayer_time_proto))
                .setAsrTiming(context.getString(R.string.default_prayer_time_proto))
                .setMaghribTiming(context.getString(R.string.default_prayer_time_proto))
                .setIshaTiming(context.getString(R.string.default_prayer_time_proto))
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










    suspend fun fetchInitialPreferences() = dataStoreStatePreferences.data.first()




    companion object {
        private const val DATA_STORE_FILE_NAME = "state_prefs.pb"

        private val Context.stateDataStore: DataStore<StatePreferences> by dataStore(
            fileName = DATA_STORE_FILE_NAME,
            serializer = StatePreferencesSerializer
        )

    }

}





