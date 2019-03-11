package com.pedrocosta.cachesmith.library.models

import com.pedrocosta.cachesmith.library.annotations.Field
import com.pedrocosta.cachesmith.library.annotations.Table

@Table("teste")
class Teste(@Field("name") val name: String) {
    val ab: Int = 0
    val ac: Long = 0
    val ss: Char = 'a'
    val dd: Byte = 0
    val ff: Boolean = false
}