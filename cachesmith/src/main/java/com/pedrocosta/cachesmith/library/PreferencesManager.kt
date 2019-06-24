package com.pedrocosta.cachesmith.library

import android.content.Context
import android.content.SharedPreferences
import com.pedrocosta.cachesmith.library.config.Config

object PreferencesManager {

	companion object {
        private val SHARED_PREFERENCES_NAME = "cacheSmithSharedPref"
        private val DB_TABLES_KEY = "cacheSmithDbTablesKey"
    }
	
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
    
    fun addDBTable(context: Context, table: DBTable) {
    	synchronized(this) {
            context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit().also {
            	var jsonTables: JSONArray = JSONArray(it.getString(DB_TABLES_KEY, ""))
            	
            	for (JSONObject jObj: jsonTables) {
            		var jsonColumns = jObj.getJSONArray(table.name)
            		if (jsonColumns != null && jsonColumns.size() > 0) {
            			
            		}
            	}
                it.putString(DB_TABLES_KEY, tables)
                it.apply()
            }
        }
    }
    
    fun getDBTables(context: Context): List {
    	var tables: JSONArray? = null
        value ?: synchronized(this) {
            value ?: open(context).also {
                val value = it.getString(DB_TABLES_KEY, "")
                val jsonArray = JSONArray(value)
                
                for (i in 0 until jsonArray.length()) {
				    val tableJson = jsonArray.getJSONObject(i)
				    var table = tableJson.
				}
            }
        }
        return tables!!
    }
    
    class DBTable(name: String) {
    	val columns: List<String> = ArrayList<String>()
    	
    	fun getColumnsJson(): JSONArray {
    		return JSONArray(Gson().toJson(columns))
    	}
    }
}
