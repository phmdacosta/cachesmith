package com.cachesmith.library.util

import android.content.Context
import android.content.SharedPreferences
import com.cachesmith.library.config.Config

object PreferencesManager {

    private const val SUFIX_TABLE = "_Table"
    private const val MODELS_KEY = "modelsKey"

    @Volatile private var mPref: SharedPreferences? = null

    fun open(context: Context): SharedPreferences {
		mPref ?: synchronized(this) {
            mPref ?: context.getSharedPreferences(Config.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).also {
                mPref = it
            }
		}
        return mPref!!
    }

    fun saveTableJson(context: Context, key: String, json: JSONTable) {
        synchronized(this) {
            open(context).edit().also { sPref ->
                val fullKey = key.plus(SUFIX_TABLE)
                sPref.putString(fullKey, json.toString())
                sPref.apply()
            }
        }
    }

    fun getTableJson(context: Context, key: String): JSONTable {
        var jsonTable: JSONTable
        synchronized(this) {
            open(context).also { sPref ->
                val fullKey = key.plus(SUFIX_TABLE)
                val s = sPref.getString(fullKey, "")
                jsonTable = JSONTable(s ?: "")
            }
        }
        return jsonTable
    }

    fun putString(context: Context, key: String, value: String) {
        synchronized(this) {
            open(context).edit().also {
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
    
    fun saveManualVersionCheck(context: Context, value: Boolean) {
        synchronized(this) {
            open(context).edit().also {
                it.putBoolean(Config.MANUAL_VERSION_KEY, value)
                it.apply()
            }
        }
    }

    fun getManualVersionCheck(context: Context): Boolean {
        var value: Boolean? = null
        value ?: synchronized(this) {
            value ?: open(context).also {
            	value = it.getBoolean(Config.MANUAL_VERSION_KEY, false)
            }
        }
        return value!!
    }
    
    fun saveVersion(context: Context, value: Int) {
        synchronized(this) {
            open(context).edit().also {
                it.putInt(Config.VERSION_CACHED_KEY, value)
                it.apply()
            }
        }
    }

    fun getVersion(context: Context): Int {
        var value: Int? = null
        value ?: synchronized(this) {
            value ?: open(context).also {
            	value = it.getInt(Config.VERSION_CACHED_KEY, -1)
            }
        }
        return value!!
    }

    fun saveDatabaseName(context: Context, value: String) {
        synchronized(this) {
            open(context).edit().also {
                it.putString(Config.DB_NAME_KEY, value)
                it.apply()
            }
        }
    }

    fun getDatabaseName(context: Context): String {
        var value: String? = null
        value ?: synchronized(this) {
            value ?: open(context).also {
                value = it.getString(Config.DB_NAME_KEY, Config.DATA_BASE_DEFAULT_NAME)
            }
        }
        return value!!
    }

    fun saveModels(context: Context, modelNames: List<String>) {
        synchronized(this) {
            open(context).edit().also {
                val iterator = modelNames.iterator()
                while (iterator.hasNext()) {
                    var names = iterator.next()
                    if (iterator.hasNext()) {
                        names = names.plus(";")
                    }
                    it.putString(MODELS_KEY, names)
                }
                it.apply()
            }
        }
    }

    fun getModels(context: Context): MutableList<String> {
        var array: List<String> = mutableListOf()
        synchronized(this) {
            open(context).also {
                val value = it.getString(MODELS_KEY, "")
                if (value != null && !value.isBlank()) {
                    array = value.split(";")
                }
            }
        }
        return array.toMutableList()
    }
}