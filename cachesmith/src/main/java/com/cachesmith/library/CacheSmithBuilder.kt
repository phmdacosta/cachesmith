package com.cachesmith.library

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.cachesmith.library.annotations.Entity
import com.cachesmith.library.config.BuildInfo

class CacheSmithBuilder(val context: Context) : CacheSmith {

    companion object {
        fun build(context: Context): CacheSmith {
            return CacheSmithBuilder(context)
        }
    }

    override fun <T> load(dataSource: Class<T>): T {
        var model: Class<*>? = null
        dataSource.annotations.forEach {
            if (it is Entity) {
                try {
                    model = Class.forName(it.value)
                } catch (e: ClassNotFoundException) {
                    Log.e("CacheSmith", "Could not find class ${it.value}. Please check if it's defined correctly with package and class name.")
                    throw e
                }
            }
        }
        val helper = CacheSmithHelper.create(context, model)
        return dataSource.getConstructor(SQLiteOpenHelper::class.java).newInstance(helper) as T
    }
    
    override fun setManualVersionDefine(manualDefine: Boolean) {
    	PreferencesManager.saveManualVersionCheck(context, manualDefine)
    }

    override fun isManualVersion(): Boolean {
        return PreferencesManager.getManualVersionCheck(context)
    }
    
    override fun setVersion(version: Int) {
    	PreferencesManager.saveVersion(context, version)
    }

    override fun getVersion(): Int {
        var version = PreferencesManager.getVersion(context)
        if (version < 0) {
            version = BuildInfo.getVersionCode(context)
        }
        return version
    }

    override fun setDatabaseName(name: String) {
        PreferencesManager.saveDatabaseName(context, name)
    }

    override fun getDatabaseName(): String {
        return PreferencesManager.getDatabaseName(context)
    }
}