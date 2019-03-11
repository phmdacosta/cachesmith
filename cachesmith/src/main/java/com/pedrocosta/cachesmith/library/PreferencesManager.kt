package com.pedrocosta.cachesmith.library

import android.content.Context
import android.content.SharedPreferences
import com.pedrocosta.cachesmith.library.config.Config

object PreferencesManager {
    private var mPref: SharedPreferences? = null

    fun open(context: Context): SharedPreferences {
        mPref ?: synchronized(this) {
            mPref ?: context.getSharedPreferences(Config.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).also {
                mPref = it
            }
        }

        return mPref!!
    }
}