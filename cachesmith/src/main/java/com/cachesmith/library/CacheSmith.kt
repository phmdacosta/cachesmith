package com.cachesmith.library

import android.content.Context
import kotlin.reflect.KClass

interface CacheSmith {

    fun <T : DataSource> load(dataSource: Class<T>): T
    fun <T : DataSource> load(dataSource: KClass<T>): T
    fun setVersion(version: Int)
    fun getVersion(): Int
    fun setDatabaseName(name: String)
    fun getDatabaseName(): String

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