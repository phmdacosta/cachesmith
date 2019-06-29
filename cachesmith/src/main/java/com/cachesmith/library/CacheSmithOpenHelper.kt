package com.cachesmith.library

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.cachesmith.library.annotations.*
import com.cachesmith.library.config.DataTypes
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import com.cachesmith.library.config.BuildInfo
import com.cachesmith.library.exceptions.NoVersionException

class CacheSmithOpenHelper private constructor(val context: Context, val name: String, val version: Int, private val entity: Class<*>?):
        SQLiteOpenHelper(context, name, null, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        Log.i("TESTE", "CacheSmithOpenHelper.onCreate")

        if (entity != null && entity.annotations != null) {
            val sql = ""

            val fieldsJsonArray = JSONArray()
            var jsonFields = JSONObject()

            sql.plus("CREATE TABLE ")
			
			var tableName = entity.name
            val tableAnnot = entity.annotations.find { it is Table } as Table
            if (!tableAnnot.name.isBlank()) {
            	tableName = tableAnnot.name
            }
			jsonFields.put("name", tableName)
			sql.plus(tableName)
            sql.plus(" ( ")
            
            var fieldsToSave = mutableListOf<String>()

            var i = 0
            entity.declaredFields.forEach { field ->
                try {
                    entity.getMethod("get".plus(field.name.capitalize()))
                } catch (e: Exception) {
                    return@forEach
                }

                if (i > 0) {
                    sql.plus(", ")
                }
				
				var columntype = ""

                when {
                    field.type.name.contains("int", true) -> columntype = DataTypes.INTEGER
                    field.type.name.contains("long", true) -> columntype = DataTypes.INTEGER
                    field.type.name.contains("double", true) -> columntype = DataTypes.REAL
                    field.type.name.contains("float", true) -> columntype = DataTypes.REAL
                    field.type.name.contains("string", true) -> columntype = DataTypes.TEXT
                    field.type.name.contains("char", true) -> columntype = DataTypes.TEXT
                    field.type.name.contains("byte", true) -> columntype = DataTypes.BLOB
                }
				
				val annotationsField = mutableListOf<String>()

                if (field.annotations != null) {
					var jsonField = JSONObject()
                    field.annotations.forEach {annotation ->
						
						annotationsField.add(annotation.annotationClass.simpleName!!)
						
                        when(annotation) {
                            is Field -> {
								i++
								
                            	fieldsToSave.add(field.name)
                                var columnName = field.name
                                if (!annotation.name.isBlank()) {
                                    columnName = annotation.name
                                }
                                sql.plus(columnName)
								sql.plus(" ")
								sql.plus(columntype)
								
								jsonField.put("name", columnName)
								jsonField.put("type", field.type.name)
                            }
                            is PrimaryKey -> sql.plus(" PRIMARY KEY ")
                            is Unique -> sql.plus(" UNIQUE ")
                            is AutoIncrement -> sql.plus(" AUTO_INCREMENT ")
                            is ForeignKey -> sql.plus(" FOREIGN KEY ")
                            is NotNullable -> sql.plus(" NOT NULL ")
                        }
                    }
					
					if (jsonField.getString("name") != null) {
						jsonField.put("annotations", annotationsField.toList())
						fieldsJsonArray.put(jsonField)
					}
                }
            }
			
			jsonFields.put("quantity", i)
			jsonFields.put("columns", fieldsJsonArray)

            sql.plus(" ) ")

            Log.i("TESTE", sql)
            
//            jsonFields.put("name", entity!!.name)
//            jsonFields.put("columns", fieldsToSave)
			
			PreferencesManager.putString(context, entity.name, jsonFields.toString())

//            PreferencesManager.putString(context,
//                    "cacheSmithDbTablesKey", jsonFields.toString())
            
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

            db!!.execSQL(sql)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.i("TESTE", "CacheSmithOpenHelper.onUpgrade")
		
		var update = true
        
		var jsonTable: JSONObject? = null
		
		if (!PreferencesManager.getManualVersionCheck(context)) {
			jsonTable = JSONObject(PreferencesManager.getString(context, entity!!.name))
			if (!jsonTable.toString().isBlank()) {
				update = checkEntityChanged(jsonTable)
			}
		}
		
		if (update) {
			
			val sql = ""
			
			sql.plus("ALTER TABLE ")
		
			val tableAnnot = entity!!.annotations.find { it is Table } as Table
            if (!tableAnnot.name.isBlank()) {
            	sql.plus(tableAnnot.name)
            } else {
            	sql.plus(entity!!.name)
            }
            sql.plus(" ( ")
            
            var fieldsToSave = mutableListOf<String>()

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
				
				var columntype = ""

                when {
                    field.type.name.contains("int", true) -> columntype = DataTypes.INTEGER
                    field.type.name.contains("long", true) -> columntype = DataTypes.INTEGER
                    field.type.name.contains("double", true) -> columntype = DataTypes.REAL
                    field.type.name.contains("float", true) -> columntype = DataTypes.REAL
                    field.type.name.contains("string", true) -> columntype = DataTypes.TEXT
                    field.type.name.contains("char", true) -> columntype = DataTypes.TEXT
                    field.type.name.contains("byte", true) -> columntype = DataTypes.BLOB
                }
				
				val annotationsField = mutableListOf<String>()

                if (field.annotations != null) {
					var jsonField = JSONObject()
                    field.annotations.forEach {annotation ->
						
						annotationsField.add(annotation.annotationClass.simpleName!!)
						
                        when(annotation) {
                            is Field -> {
								i++
								
								jsonField.put("name", field.name)
								jsonField.put("type", field.type.name)
								
                            	fieldsToSave.add(field.name)
                                var columnName = field.name
                                if (!annotation.name.isBlank()) {
                                    columnName = annotation.name
                                }
                                sql.plus(columnName)
								sql.plus(" ")
								sql.plus(columntype)
                            }
                            is PrimaryKey -> sql.plus(" PRIMARY KEY ")
                            is Unique -> sql.plus(" UNIQUE ")
                            is AutoIncrement -> sql.plus(" AUTO_INCREMENT ")
                            is ForeignKey -> sql.plus(" FOREIGN KEY ")
                            is NotNullable -> sql.plus(" NOT NULL ")
                        }
                    }
					
					if (jsonField.getString("name") != null) {
						jsonField.put("annotations", annotationsField.toList())
//						fieldsJsonArray.put(jsonField)
					}
                }
            }
		}
    }
	
	fun checkEntityChanged(jsonTable: JSONObject): Boolean {
			
        val tableAnnot = entity!!.annotations.find { it is Table } as Table
        if (tableAnnot == null
			|| (!tableAnnot.name.isBlank() && !tableAnnot.equals(jsonTable.getString("name")))
			|| (!entity!!.name.equals(jsonTable.getString("name")))) {
			return true
        }
		
		val jsonColumns = jsonTable.getJSONArray("columns")
		
		for (j in 0..jsonColumns.length()) {
			val jsonColumn = jsonColumns.get(j) as JSONObject
			val jsonAnnotations = jsonColumn.get("annotations") as List<String>
			
			entity!!.declaredFields.forEach { field ->
	            try {
	                entity!!.getMethod("get".plus(field.name.capitalize()))
	            } catch (e: Exception) {
	               return@forEach
	            }
				
				if (!field!!.name.equals(jsonColumn.getString("name"))) {
					
				}
				
				if (field.annotations != null) {
	                field.annotations.forEach {annotation ->
						if (AnnotationValidator.isValid(annotation)) {
							if (!jsonAnnotations.contains(annotation.annotationClass.simpleName)) {
								return true
							}
						}
					}
				}
			}
		}
		

        var i = 0
		listOf(1, 2, 3, 4, 5).forEach {
	        if (it == 3) return@forEach // non-local return directly to the caller of foo()
	        print(it)
	    }
        entity!!.declaredFields.forEach { field ->
            try {
                entity!!.getMethod("get".plus(field.name.capitalize()))
            } catch (e: Exception) {
               return@forEach
            }
			
			if (field.type.name.equals(jsonTable.get("name"))) {

            }
			
			var columntype = ""

            when {
                field.type.name.contains("int", true) -> columntype = DataTypes.INTEGER
                field.type.name.contains("long", true) -> columntype = DataTypes.INTEGER
                field.type.name.contains("double", true) -> columntype = DataTypes.REAL
                field.type.name.contains("float", true) -> columntype = DataTypes.REAL
                field.type.name.contains("string", true) -> columntype = DataTypes.TEXT
                field.type.name.contains("char", true) -> columntype = DataTypes.TEXT
                field.type.name.contains("byte", true) -> columntype = DataTypes.BLOB
            }
			
			val annotationsField = mutableListOf<String>()

            if (field.annotations != null) {
				var jsonField = JSONObject()
                field.annotations.forEach {annotation ->
					
					annotationsField.add(annotation.annotationClass.simpleName!!)
					
                    when(annotation) {
                        is Field -> {
                        }
                        is PrimaryKey -> {}
                        is Unique -> {}
                        is AutoIncrement -> {}
                        is ForeignKey -> {}
                        is NotNullable -> {}
                    }
                }
				
				if (jsonField.getString("name") != null) {
					jsonField.put("annotations", annotationsField.toList())
				}
            }
        }
		
		return false
	}

    class Builder {

        companion object {

            fun buid(context: Context, entity: Class<*>?): CacheSmithOpenHelper {

                val dbName = PreferencesManager.getDatabaseName(context)

                var newVersion = BuildInfo.getVersionCode(context)

                if (PreferencesManager.getManualVersionCheck(context)) {
                    newVersion = PreferencesManager.getVersion(context)
                    if (newVersion < 0) {
                        throw NoVersionException("Could not get version for database.")
                    }
                }

                return CacheSmithOpenHelper(context, dbName, newVersion, entity)
            }
        }
    }
}