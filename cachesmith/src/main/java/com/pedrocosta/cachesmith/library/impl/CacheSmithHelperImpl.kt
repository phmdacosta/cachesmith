package com.pedrocosta.cachesmith.library.impl

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.pedrocosta.cachesmith.library.PreferencesManager
import com.pedrocosta.cachesmith.library.annotations.*
import com.pedrocosta.cachesmith.library.config.DataTypes
import java.lang.Exception

class CacheSmithHelperImpl private constructor(val context: Context, val name: String, val version: Int):
        SQLiteOpenHelper(context, name, null, version) {

    companion object {
        private val DEFAULT_VERSION = 1
        private val VERSION_CACHED_KEY = "sp-database-version"
    }

    protected var entity: Class<*>? = null

    override fun onCreate(db: SQLiteDatabase?) {
        Log.i("TESTE", "CacheSmithHelperImpl.onCreate")

        val sql = ""

        if (entity != null && entity!!.annotations != null) {
            sql.plus("CREATE TABLE ")
            val tableAnnot = entity!!.annotations.find { it is Table } as Table
            sql.plus(tableAnnot.name)
            sql.plus(" ( ")

            var i = 0
            entity!!.declaredFields.forEach { field ->
                try {
                    entity!!.getMethod("get".plus(field.name.capitalize()))
                } catch (e: Exception) {
                    return
                }

                if (i > 0) {
                    sql.plus(", ")
                }

                when {
                    field.type.name.contains("int", true) -> sql.plus(DataTypes.INTEGER)
                    field.type.name.contains("long", true) -> sql.plus(DataTypes.INTEGER)
                    field.type.name.contains("double", true) -> sql.plus(DataTypes.REAL)
                    field.type.name.contains("float", true) -> sql.plus(DataTypes.REAL)
                    field.type.name.contains("string", true) -> sql.plus(DataTypes.TEXT)
                    field.type.name.contains("char", true) -> sql.plus(DataTypes.TEXT)
                    field.type.name.contains("byte", true) -> sql.plus(DataTypes.BLOB)
                }

                sql.plus(" ")

                if (field.annotations != null) {
                    field.annotations.forEach {annotation ->
                        when(annotation) {
                            is Field -> sql.plus(annotation.name)
                            is PrimaryKey -> sql.plus(" PRIMARY KEY ")
                            is Unique -> sql.plus(" UNIQUE ")
                            is AutoIncrement -> sql.plus(" AUTO_INCREMENT ")
                            is ForeignKey -> sql.plus(" FOREIGN KEY ")
                            is NotNullable -> sql.plus(" NOT NULL ")
                        }
                    }
                }

                i++
            }

            sql.plus(" ) ")

            Log.i("TESTE", sql)
        }

        db!!.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.i("TESTE", "CacheSmithHelperImpl.onUpgrade")
    }

    class Builder(private val context: Context, private val dbName: String) {

        var newVersion: Int = DEFAULT_VERSION

        constructor(context: Context, dbName: String, newVersion: Int) : this(context, dbName) {
            this.newVersion = newVersion
        }

        fun buid(): CacheSmithHelperImpl {
            val sPref = PreferencesManager.open(context)

            Log.i("TESTE", "CacheSmithHelperImpl.Builder().build()")

            if (sPref.contains(VERSION_CACHED_KEY)) {
                val currentVersion = sPref.getInt(VERSION_CACHED_KEY, DEFAULT_VERSION)
                if (newVersion <= currentVersion) {
                    newVersion = currentVersion
                }
            }

            return CacheSmithHelperImpl(context, dbName, newVersion)
        }
    }
}