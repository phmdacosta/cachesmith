package com.cachesmith.library.kotlin

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.cachesmith.library.CacheSmith
import com.cachesmith.library.util.PreferencesManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LibraryAttributesKotlinTest {

    lateinit var appContext: Context
    lateinit var cacheSmith: CacheSmith

    @Before
    fun setUpLibrary() {
        appContext = InstrumentationRegistry.getTargetContext()
        cacheSmith = CacheSmith.build(appContext)
        cacheSmith.setDatabaseName("Database_Test_Name")
        cacheSmith.setManualVersion(true)
        cacheSmith.setVersion(23)
    }

    @Test
    fun testAttributesFromInstance() {
        assertEquals("Database_Test_Name", cacheSmith.getDatabaseName())
        assertTrue(cacheSmith.isManualVersionDefined())
        assertEquals(23, cacheSmith.getVersion().toLong())
    }

    @Test
    fun testAttributesFromContext() {
        val localInstance = CacheSmith.build(appContext)
        assertEquals("Database_Test_Name", localInstance.getDatabaseName())
        assertTrue(localInstance.isManualVersionDefined())
        assertEquals(23, localInstance.getVersion().toLong())
    }

    @Test
    fun testLibraryManualKotlinModelList(){
        val cacheSmith = CacheSmith.build(appContext)
        cacheSmith.addModel(String::class)
        cacheSmith.addModel(Context::class)

        val listModelsNames = PreferencesManager.getModels(appContext)

        assertTrue(listModelsNames.contains(String::class.qualifiedName))
        assertTrue(listModelsNames.contains(Context::class.qualifiedName))
    }

    @Test
    fun testLibraryManualJavaModelList(){
        val cacheSmith = CacheSmith.build(appContext)
        cacheSmith.addModel(String.javaClass)
        cacheSmith.addModel(Context::class.java)

        val listModelsNames = PreferencesManager.getModels(appContext)

        assertTrue(listModelsNames.contains(String.javaClass.name))
        assertTrue(listModelsNames.contains(Context::class.java.name))
    }


}