package com.pedrocosta.cachesmith.example

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule
import com.pedrocosta.cachesmith.library.impl.CacheSmithHelperImpl
import org.mockito.Mockito

@RunWith(MockitoJUnitRunner::class)
class TestCacheSmithHelper {

    @get:Rule
    val rule: MockitoRule = MockitoJUnit.rule()

    @Mock
    var context: Context? = null
    @Mock
    lateinit var dbMock: SQLiteDatabase

    @Before
    fun setup() {
        context = Mockito.mock(Context::class.java)
        dbMock = Mockito.mock(SQLiteDatabase::class.java)
    }

    @Test
    fun testOnCreateDataBase() {
//        assert(0 == 0)
        val helper = CacheSmithHelperImpl.Builder(context!!, "test.db").buid()
        helper.onCreate(dbMock)
    }
}