package com.cachesmith.example.dao

import android.database.sqlite.SQLiteOpenHelper
import com.cachesmith.example.models.Teste
import com.cachesmith.library.DataSource
import com.cachesmith.library.annotations.Entity

@Entity("com.cachesmith.example.models.Teste")
class TesteDataSource(dbHelper: SQLiteOpenHelper) : DataSource(dbHelper) {
    fun insert(teste: Teste) {
        database.execSQL("")
    }
}