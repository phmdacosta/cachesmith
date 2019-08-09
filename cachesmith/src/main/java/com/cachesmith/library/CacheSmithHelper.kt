package com.cachesmith.library

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import com.cachesmith.library.exceptions.NoVersionException
import com.cachesmith.library.util.ObjectClass

object CacheSmithHelper {

    @Volatile private var instance: SQLiteOpenHelper? = null

    @Throws(NoVersionException::class)
    fun create(context: Context, entities: List<ObjectClass>): SQLiteOpenHelper {
   		instance ?: synchronized(this) {
        	instance ?: CacheSmithOpenHelper.Builder.buid(context, entities).also { instance = it }
        }
        return instance!!
    }
}