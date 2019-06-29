package com.cachesmith.library

import android.content.Context

interface CacheSmith {

    fun <T> load(dataSource: Class<T>): T
    fun setManualVersionDefine(manualDefine: Boolean)
    fun isManualVersion(): Boolean
    fun setVersion(version: Int)
    fun getVersion(): Int
    fun setDatabaseName(name: String)
    fun getDatabaseName(): String

    class Builder {
        companion object {
            @Volatile private var instance: CacheSmith? = null

            fun build(context: Context): CacheSmith {
                instance ?: synchronized(this) {
                    return CacheSmithBuilder.build(context)
                }
                return instance!!
            }
        }
    }
}