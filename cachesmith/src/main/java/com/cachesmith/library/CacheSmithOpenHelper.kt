package com.cachesmith.library

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.cachesmith.library.annotations.*
import java.lang.Exception
import com.cachesmith.library.exceptions.NoVersionException
import com.cachesmith.library.util.*
import com.cachesmith.library.util.db.CloneTableBuilder
import com.cachesmith.library.util.db.CreateTableBuilder
import com.cachesmith.library.util.db.DropTableBuilder
import com.cachesmith.library.util.db.models.ColumnObject
import com.cachesmith.library.util.db.models.ForeignKeyObject
import java.lang.reflect.Field
import kotlin.reflect.KMutableProperty
import kotlin.coroutines.Continuation
import com.cachesmith.library.util.db.DatabaseUtils

class CacheSmithOpenHelper private constructor(val context: Context, val name: String, val version: Int, private val entities: List<ObjectClass>):
        SQLiteOpenHelper(context, name, null, version) {

	companion object {
		private fun tableNotExists(context: Context, entity: ObjectClass): Boolean {
			val jsonTable = PreferencesManager.getTableJson(context, entity.qualifiedName)
			return jsonTable.isEmpty()
		}
		
		private fun anyEntityChanged(context: Context, entities: List<ObjectClass>): Boolean {
			entities.forEach { entity ->
				if (tableNotExists(context, entity))
					return true

				val jsonTable = PreferencesManager.getTableJson(context, entity.qualifiedName)
				
				if (entity.tableName != jsonTable.name)
					return true
		
				if (jsonTable.columnQuantity != entity.fields.size)
					return true
		
				entity.fields.forEach { field ->
					jsonTable.listJsonColumns().forEach { jsonCollumn ->
						if (field.name == jsonCollumn.field) {
							val columnAnnot = (field.annotations.find { it is Column })?.let { it as Column }
							if (columnAnnot == null) {
								if (field.name != jsonCollumn.name) return true
							}
							else {
								if (columnAnnot.name.isBlank() && field.name != jsonCollumn.name)
									return true
								else if (columnAnnot.name != jsonCollumn.name)
									return true
							}
		
							if (field.type.name != jsonCollumn.type)
								return true
		
							if (annotationsChanged(field, jsonCollumn))
								return true
						}
					}
				}
			}
			return false
		}
		
		private fun annotationsChanged(field: ObjectField, json: JSONColumn): Boolean {
			field.annotations.forEach { fieldAnnot ->
				val jsonAnnotation = JSONAnnotation()
				jsonAnnotation.name = fieldAnnot.annotationClass.simpleName!!
				if (!json.listAnnotationsJson().contains(jsonAnnotation))
					return true
			}
			return false
		}
	}
	
    override fun onCreate(db: SQLiteDatabase?) {
		entities.forEach { entity ->
			Log.i("TESTE", "CacheSmithOpenHelper.onCreate for " + entity.simpleName)
			execCreateTable(db, entity, true)
		}
    }

	override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
		entities.forEach { entity ->
			Log.i("TESTE", "CacheSmithOpenHelper.onUpgrade for " + entity.simpleName)
	
			val jsonTable = PreferencesManager.getTableJson(context, entity.qualifiedName)
	
			execCloneTable(db, jsonTable.name)
			// TODO insert data from origin table to clone table
			execDropTable(db, jsonTable.name)
	
			execCreateTable(db, entity, true)
	
			// TODO insert data from clone table to new table
			val cloneTableName = jsonTable.name.plus(CloneTableBuilder.TABLE_SUFIX)
			execDropTable(db, cloneTableName)
		}

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

	private fun execCreateTable(db: SQLiteDatabase?, entity: ObjectClass, saveRefBackup: Boolean) {
		val jsonTable = JSONTable()
		val queryBuilder = CreateTableBuilder()

		jsonTable.name = entity.tableName
		queryBuilder.tableName = entity.tableName

		entity.fields.forEach { field ->
			val columnObj = ColumnObject()
			val jsonColumn = JSONColumn()

			jsonColumn.field = field.name

			columnObj.typeClass = field.type.clazz

			val jsonAnnotation = JSONAnnotation()

			columnObj.name = field.columnName
			jsonColumn.name = columnObj.name

			field.annotations.forEach {annotation ->

				jsonAnnotation.name = annotation.annotationClass.simpleName!!

				when(annotation) {
					is Column -> {
						if (annotation.type != DataType.NONE) {
							columnObj.typeName = annotation.type.value
						}
						jsonColumn.type = field.type.name
					}
					is Relationship -> {
						if (!annotation.query.isBlank()) {
							columnObj.foreignKeyQuery = annotation.query
						} else {
							if (annotation.type == RelationType.ONE_TO_ONE
									|| annotation.type == RelationType.MANY_TO_ONE) {

								val foreignKey = DatabaseUtils.getForeignKeyObject(annotation, field.type.clazz)
								columnObj.foreignKey = foreignKey
							}
							else if (annotation.type == RelationType.MANY_TO_MANY) {
								createRelationalTable(db, entity, field.type.clazz)
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

			if ("" != jsonColumn.name) {
				jsonTable.addColumnJson(jsonColumn)
			}

			queryBuilder.addColumn(columnObj)
		}

		val sql = queryBuilder.build()

		if (saveRefBackup) {
			PreferencesManager.saveTableJson(context, entity.qualifiedName, jsonTable)
		}

		Log.i("TESTE", sql)
		Log.i("TESTE", jsonTable.toString())
	}

	private fun execCloneTable(db: SQLiteDatabase?, tableName: String) {
		val queryBuilder = CloneTableBuilder(tableName)
		Log.i("TESTE", queryBuilder.build())
	}

	private fun execDropTable(db: SQLiteDatabase?, tableName: String) {
		val queryBuilder = DropTableBuilder()
		queryBuilder.tableName = tableName
		Log.i("TESTE", queryBuilder.build())
	}

	private fun createRelationalTable(db: SQLiteDatabase?, principal: ObjectClass, target: ObjectClass) {
		val queryBuilder = CreateTableBuilder()
		
		queryBuilder.tableName = DatabaseUtils.getRelationalTableName(principal.simpleName, target.simpleName)
		
		val entityColumnObj = DatabaseUtils.getColumnsForRelationalTable(principal)
		queryBuilder.addColumn(entityColumnObj)
		
		val targetColumnObj = DatabaseUtils.getColumnsForRelationalTable(target)
		queryBuilder.addColumn(targetColumnObj)
		
		db!!.execSQL(queryBuilder.build())
	}

    class Builder {

        companion object {
			
			private const val BASE_VERSION = 1
			private const val INCREMENTAL = 1

			@Throws(NoVersionException::class)
            fun buid(context: Context, entities: List<ObjectClass>): CacheSmithOpenHelper {

                val dbName = PreferencesManager.getDatabaseName(context)
				var newVersion: Int = PreferencesManager.getVersion(context)
				val manualVersion = PreferencesManager.getManualVersionCheck(context)

				if (!manualVersion) {
					if (newVersion < 0) {
						newVersion = BASE_VERSION
						PreferencesManager.saveVersion(context, newVersion)
					}
					else if (anyEntityChanged(context, entities)) {
						newVersion += INCREMENTAL
					}
				}
				else if (newVersion < 0) {
					throw NoVersionException()
				}

                return CacheSmithOpenHelper(context, dbName, newVersion, entities)
            }
        }
    }
}