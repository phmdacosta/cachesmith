package com.pedrocosta.cachesmith.library

import android.content.Context
import android.content.SharedPreferences
import com.pedrocosta.cachesmith.library.config.Config

object PreferencesManager {
    private var mPref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    fun open(context: Context): SharedPreferences {
        mPref ?: synchronized(this) {
            mPref ?: context.getSharedPreferences(Config.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).also {
                mPref = it
            }
        }

        return mPref!!
    }

    fun putString(context: Context, key: String, value: String) {
        synchronized(this) {
            context.getSharedPreferences(Config.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit().also {
                it.putString(key, value)
                it.apply()
            }
        }
    }

    fun getString(context: Context, key: String): String {
        var value: String? = null
        value ?: synchronized(this) {
            value ?: open(context).also {
                value = it.getString(key, "")
            }
        }
        return value!!
    }
}