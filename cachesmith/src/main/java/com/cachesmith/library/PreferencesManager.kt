package com.cachesmith.library

import android.content.Context
import android.content.SharedPreferences
import com.cachesmith.library.config.Config

object PreferencesManager {
	
    private var mPref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    fun open(context: Context): SharedPreferences {
		mPref ?: synchronized(this) {
			if (mPref == null) {
	            mPref ?: context.getSharedPreferences(Config.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).also {
	                mPref = it
	            }
	        }
		}
        return mPref!!
    }
    
    fun saveTableColumns(context: Context, tableName: String, values: List<String>) {
        synchronized(this) {			
            open(context).edit().also { sPref -> 
            	val strBuilder = StringBuilder()
            	values.forEach { s ->
            		strBuilder.append(s)
            		strBuilder.append(";")
            	}
            	sPref.putString(tableName.plus("_key"), strBuilder.toString())
            	sPref.apply()
            }
        }
    }
    
    fun getTableColumns(context: Context, tableName: String): List<String> {
        var value: MutableList<String>? = null
        value ?: synchronized(this) {
            value ?: open(context).also { sPref ->
				value = mutableListOf<String>()
            	val s = sPref.getString(tableName.plus("_key"), "")
            	s.split(";").forEach {
            		value!!.add(it)
            	}
            }
        }
        return value!!.toList()
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
}