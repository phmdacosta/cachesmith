package com.pedrocosta.cachesmith.library

import android.content.Context

interface CacheSmithHelper {
    fun create(context: Context, databaseName: String, clazz: Class<*>)
}