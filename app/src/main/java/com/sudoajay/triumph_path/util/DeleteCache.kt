package com.sudoajay.triumph_path.util

import android.content.Context
import com.sudoajay.triumph_path.R
import java.io.File

internal object DeleteCache {
    fun deleteCache(context: Context) {
        try {
            val dir = context.cacheDir
            deleteWithFile(dir)
            Toaster.showToast(
                context,
                context.getString(R.string.successfully_cache_data_is_deleted_text)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteWithFile(dir: File): Boolean {
        return when {
            dir.isDirectory -> {
                val children = dir.listFiles()
                for (i in children!!.indices) {
                    deleteWithFile(children[i])
                }
                dir.delete()
            }
            dir.isFile -> {
                dir.delete()
            }
            else -> {
                return false
            }
        }
    }
}