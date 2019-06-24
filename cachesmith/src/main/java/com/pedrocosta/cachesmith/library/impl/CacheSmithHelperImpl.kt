package com.pedrocosta.cachesmith.library.impl

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.pedrocosta.cachesmith.library.PreferencesManager
import com.pedrocosta.cachesmith.library.annotations.*
import com.pedrocosta.cachesmith.library.config.DataTypes
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class CacheSmithHelperImpl private constructor(val context: Context, val name: String, val version: Int):
        SQLiteOpenHelper(context, name, null, version), CacheSmithHelper() {

    companion object {
        private val DEFAULT_VERSION = 1
        private val VERSION_CACHED_KEY = "sp-database-version"
        private val SHARED_PREF_SUFIX_FIELDS = "_fields"
    }

    protected var entity: Class<*>? = null

    override fun onCreate(db: SQLiteDatabase?) {
        Log.i("TESTE", "CacheSmithHelperImpl.onCreate")

        val sql = ""

        if (entity != null && entity!!.annotations != null) {
            val fieldNames = ArrayList<String>()
            var jsonFields = JSONObject()

            sql.plus("CREATE TABLE ")
            val tableAnnot = entity!!.annotations.find { it is Table } as Table
            if (!tableAnnot.name.isBlank()) {
            	sql.plus(tableAnnot.name)
            } else {
            	sql.plus(entity!!.name)
            }
            sql.plus(" ( ")
            
            var fieldsToJson = ArrayList<string>()

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
                            is Field -> {
                            	fieldsToJson.add(field.name)
                                var columnName = field.name
                                if (!annotation.name.isBlank()) {
                                    columnName = annotation.name
                                }
                                sql.plus(columnName)
                            }
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
            
            jsonFields.put("name", entity!!.name)
            jsonFields.put("columns", fieldsToJson)

            PreferencesManager.putString(context,
                    "cacheSmithDbTablesKey", jsonFields.toString())
            
            //TODO
            /*
            var dbTable = DBTable(entity!!.name)
            dbTable.columns = fieldsToJson
            PreferencesManager.addDBTable(dbTable)
            */
            
            /*
            JSON:
            [
            	{
            		"name": "Entidade01",
            		"columns": [
            						"coluna1",
            						"coluna2",
            						"coluna3",
            						"coluna4"
            				   ]
            	},
            	{
            		"name": "Entidade02",
            		"columns": [
            						"coluna1",
            						"coluna2"
            				   ]
            	},
            	{
            		"name": "Entidade03",
            		"columns": [
            						"coluna1",
            						"coluna2",
            						"coluna3"
            				   ]
            	},
            ]
            */
        }

        db!!.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.i("TESTE", "CacheSmithHelperImpl.onUpgrade")
        
        
        
        sql.plus("ALTER TABLE ")
        
        
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
