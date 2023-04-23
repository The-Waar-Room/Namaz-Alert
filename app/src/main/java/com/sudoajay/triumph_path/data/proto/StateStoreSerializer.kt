package com.sudoajay.triumph_path.data.proto

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.sudoajay.triumph_path.StatePreferences

import java.io.InputStream
import java.io.OutputStream


object StatePreferencesSerializer : Serializer<StatePreferences> {
    override val defaultValue: StatePreferences = StatePreferences.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): StatePreferences {
        
        try {

            return StatePreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: StatePreferences, output: OutputStream) = t.writeTo(output)
}