package com.sudoajay.namaz_alert.util.proto

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.StatePreferences
import kotlinx.coroutines.flow.first
import java.util.concurrent.Flow
import javax.inject.Inject

class ProtoManager @Inject constructor (var context: Context){
    val dataStoreStatePreferences : DataStore<StatePreferences> = context.stateDataStore
    suspend fun setDefaultValue(){
        dataStoreStatePreferences.updateData { preferences->
            preferences.toBuilder()
                .setSetPhoneMode(context.getString(R.string.vibrate_mode))
                .build()
        }
    }

    suspend fun setPhoneMode(database: String) {
        dataStoreStatePreferences.updateData { preferences ->
            preferences.toBuilder()
                .setSetPhoneMode(database)
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





