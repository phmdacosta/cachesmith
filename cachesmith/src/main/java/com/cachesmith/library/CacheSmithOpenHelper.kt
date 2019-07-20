package com.cachesmith.library

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.cachesmith.library.annotations.*
import java.lang.Exception
import com.cachesmith.library.config.BuildInfo
import com.cachesmith.library.exceptions.NoVersionException
import com.cachesmith.library.util.*
import java.lang.reflect.Field

class CacheSmithOpenHelper private constructor(val context: Context, val name: String, val version: Int, private val entity: Class<*>):
        SQLiteOpenHelper(context, name, null, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        Log.i("TESTE", "CacheSmithOpenHelper.onCreate")

		val jsonTable = JSONTable()
		val queryBuilder = CreateQueryBuilder()

		var tableName = entity.name
		val tableAnnot = entity.annotations.find { it is Table } as Table
		if (!tableAnnot.name.isBlank()) {
			tableName = tableAnnot.name
		}
		jsonTable.name = tableName
		queryBuilder.tableName = tableName

		entity.declaredFields.forEach { field ->
			val columnObj = ColumnObject()
			val jsonColumn = JSONColumn()

			try {
				entity.getMethod("get".plus(field.name.capitalize()))
			} catch (e: Exception) {
				return@forEach
			}

			columnObj.typeClass = field.type

			val jsonAnnotation = JSONAnnotation()

			columnObj.name = getColumnNameFromField(field)

			field.annotations.forEach {annotation ->

				jsonAnnotation.name = annotation.annotationClass.simpleName!!

				when(annotation) {
					is Column -> {

						if (annotation.type != DataType.NONE) {
							columnObj.typeName = annotation.type.value
						}

						jsonColumn.name = columnObj.name
						jsonColumn.type = field.type.name
					}
					is Relationship -> {
						if (!annotation.query.isBlank()) {
							columnObj.foreignKeyQuery = annotation.query
						} else {
							if (annotation.type == RelationType.ONE_TO_ONE
								|| annotation.type == RelationType.MANY_TO_ONE) {

								val foreignKey = getForeignKeyObject(annotation, field.type)
								columnObj.foreignKey = foreignKey
							}
							else if (annotation.type == RelationType.MANY_TO_MANY) {
								createRelationalTable(db, field.type)
							}
						}
					}
					is PrimaryKey -> columnObj.isPrimaryKey = true
					is Unique -> columnObj.isUnique = true
					is AutoIncrement -> columnObj.isAutoIncrement = true
					is NotNullable -> columnObj.isNotNull = true
				}

				jsonColumn.addAnnotationJson(jsonAnnotation)
			}

			if (!"".equals(jsonColumn.name)) {
				jsonTable.addColumnJson(jsonColumn)
			}

			queryBuilder.addColumn(columnObj)
		}

		val sql = queryBuilder.build()

		Log.i("TESTE", sql)

		PreferencesManager.putString(context, entity.name, jsonTable.toString())

//            PreferencesManager.putString(context,
//                    "cacheSmithDbTablesKey", jsonFields.toString())

		//TODO
		/*
		var dbTable = DBTable(entity.name)
		dbTable.columns = fieldsToJson
		PreferencesManager.addDBTable(dbTable)
		*/

		/*
		key = com.projeto.models.Pessoa_Table
		JSON:
		{
			"name": "PESSOA",
			"quantity": 3,
			"columns": [
				{
					"name": "ID",
					"field": "id",
					"type": "long",
					"annotations": [
						{
							"annotation": "Field"
						},
						{
							"annotation": "PrimaryKey"
						},
						{
							"annotation": "Unique"
						}
					]
				},
				{
					"name": "NOME"
					"field": "nome",
					"type": "String"
					"annotations": [
						{
							"annotation": "Field"
						},
						{
							"annotation": "Unique"
						}
					]
				},
				{
					"name": "IDADE"
					"field": "idade",
					"type": "int"
					"annotations": [
						{
							"annotation": "Field"
						}
					]
				},
			]
		}
		*/

		db!!.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.i("TESTE", "CacheSmithOpenHelper.onUpgrade")
		
		/*
 		BEGIN TRANSACTION;
		CREATE TEMPORARY TABLE t1_backup(a,b);
		INSERT INTO t1_backup SELECT a,b FROM t1;
		DROP TABLE t1;
		CREATE TABLE t1(a,b);
		INSERT INTO t1 SELECT a,b FROM t1_backup;
		DROP TABLE t1_backup;
		COMMIT;
		 */
	}
	
	private fun getForeignKeyObject(annotation: Relationship, target: Class<*>): ForeignKeyObject {
		val foreignKey = ForeignKeyObject(annotation.targetTable, annotation.targetColumn)
		foreignKey.onDeleteAction = annotation.onDelete
		foreignKey.onUpdateAction = annotation.onUpdate
			
		if (!foreignKey.referenceTable.isBlank()) {
			foreignKey.referenceTable = getTableNameFromClass(target)
		}
		
		if (!foreignKey.referenceColumn.isBlank()) {
			getFieldsFromClass(target).forEach { field ->
				field.annotations.forEach { annot ->
					 when(annot) {
						 is PrimaryKey -> {
							 foreignKey.referenceColumn = getColumnNameFromField(field)
						 }
					 }
				}
			}
		}
		return foreignKey
	}

	private fun createRelationalTable(db: SQLiteDatabase?, target: Class<*>) {
		val queryBuilder = CreateQueryBuilder()
		
		queryBuilder.tableName = getRelationalTableName(entity.name, target.name)
		
		val entityColumnObj = getColumnsForRelationalTable(entity)
		queryBuilder.addColumn(entityColumnObj)
		
		val targetColumnObj = getColumnsForRelationalTable(target)
		queryBuilder.addColumn(targetColumnObj)
		
		db!!.execSQL(queryBuilder.build())
	}
	
	private fun getRelationalTableName(firstTableName: String, secondTableName: String): String {
		val prefxTable = "rel_"
		val firstName = firstTableName.substring(0, 4)
		val secondName = secondTableName.substring(0, 4)
		return prefxTable.plus(firstName).plus("_").plus(secondName)
	}
	
	private fun getColumnsForRelationalTable(parent: Class<*>): ColumnObject {
		val columnObj = ColumnObject()
		
		parent.declaredFields.forEach { field ->
			field.annotations.forEach {annotation ->
				when {
					annotation is PrimaryKey -> {
						columnObj.name = field.name
						columnObj.typeClass = field.type
					}
					annotation is Relationship -> {
						val foreignKeyObj = getForeignKeyObject(annotation, field.type)
						columnObj.foreignKey = foreignKeyObj
					}
				}
			}
		}
		
		return columnObj
	}

	private fun getFieldsFromClass(clazz: Class<*>): List<Field> {
		val fields = mutableListOf<Field>()
		clazz.declaredFields.forEach { field ->
			try {
				clazz.getMethod("get".plus(field.name.capitalize()))
			} catch (e: Exception) {
				return@forEach
			}
			fields.add(field)
		}
		return fields.toList()
	}

	private fun getTableNameFromClass(clazz: Class<*>): String {
		var tableName = clazz.name
        val tableAnnot = clazz.annotations.find { it is Table } as Table
        if (!tableAnnot.name.isBlank()) {
        	tableName = tableAnnot.name
        }
		return tableName
	}

	private fun getColumnNameFromField(field: Field): String {
		field.annotations.forEach { columnAnnot ->
			when(columnAnnot) {
				is Column -> {
					if (!columnAnnot.name.isBlank()) {
						return columnAnnot.name
					}
					return field.name
				} 
			}
		}
		return ""
	}
	
	fun checkEntityChanged(jsonTable: JSONTable): Boolean {
			
        val tableAnnot = entity.annotations.find { it is Table } as Table
        if ((!tableAnnot.name.isBlank() && !tableAnnot.name.equals(jsonTable.name))
			|| (!entity.name.equals(jsonTable.name))) {
			return true
        }
		
		return false
	}

    class Builder {

        companion object {

            fun buid(context: Context, entity: Class<*>): CacheSmithOpenHelper {

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