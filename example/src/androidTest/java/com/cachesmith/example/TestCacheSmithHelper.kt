package com.cachesmith.example

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.cachesmith.example.dao.TesteDataSource
import com.cachesmith.library.CacheSmith
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule
import com.cachesmith.library.CacheSmithOpenHelper
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
        val dataSource = CacheSmith.create(context!!).load(TesteDataSource::class)
        assert(dataSource is TesteDataSource)
//        val helper = CacheSmithOpenHelper.Builder(context!!, "test.db").buid()
//        helper.onCreate(dbMock)
    }
}