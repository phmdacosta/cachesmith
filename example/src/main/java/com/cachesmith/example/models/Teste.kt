package com.cachesmith.example.models

import android.database.sqlite.SQLiteOpenHelper
import com.cachesmith.library.DataSource
import com.cachesmith.library.annotations.Column
import com.cachesmith.library.annotations.Entity
import com.cachesmith.library.annotations.Table

//@Table("teste")
class Teste(@Column("name") val name: String) {
    val ab: Int = 0
    val ac: Long = 0
    val ss: Char = 'a'
    val dd: Byte = 0
    val ff: Boolean = false
}