package com.pedrocosta.cachesmith.library

import android.content.Context
import com.pedrocosta.cachesmith.library.config.Config
import com.pedrocosta.cachesmith.library.impl.CacheSmithHelperImpl

class CacheSmith(val helperImpl: CacheSmithHelperImpl) {

    var dataSource: DataSource? = null

    companion object {
        @Volatile private var instance: CacheSmith? = null

        fun create(context: Context): CacheSmith {
            instance ?: synchronized(this) {
//                val provider = ChacheSmithProvider()
                instance ?: Builder(context).build().also { instance = it }
            }
            return instance!!
        }
    }

    private fun load(clazz: Class<*>): DataSource {
        dataSource ?: DataSource(clazz, helperImpl).also { dataSource = it }
        return dataSource!!
    }

    private class Builder(val context: Context) {
        fun build(): CacheSmith {
            val helperImpl = CacheSmithHelperImpl.Builder(context, Config.DATA_BASE_NAME).buid()
            return CacheSmith(helperImpl)
        }
    }
}