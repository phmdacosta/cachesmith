package com.cachesmith.example.models

import com.cachesmith.library.annotations.Field
import com.cachesmith.library.annotations.Table

@Table("teste")
class Teste(@Field("name") val name: String) {
    val ab: Int = 0
    val ac: Long = 0
    val ss: Char = 'a'
    val dd: Byte = 0
    val ff: Boolean = false
}