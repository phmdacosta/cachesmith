package com.cachesmith.library.kotlin

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.cachesmith.library.CacheSmith
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LibraryInstanceKotlinTest {

    lateinit var appContext: Context
    lateinit var cacheSmith: CacheSmith

    @Before
    fun prepareTest() {
        appContext = InstrumentationRegistry.getTargetContext()
        cacheSmith = CacheSmith.build(appContext)
    }

    @Test
    fun testLibraryBuild() {
        assertNotNull(cacheSmith)
    }

    @Test
    fun testDatabaseDefaultNameDefined() {
        assertNotNull(cacheSmith.getDatabaseName())
    }
}