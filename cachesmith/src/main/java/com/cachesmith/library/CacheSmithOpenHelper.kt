package com.cachesmith.library

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.cachesmith.library.annotations.*
import com.cachesmith.library.exceptions.NoVersionException
import com.cachesmith.library.util.*
import com.cachesmith.library.util.db.internal.CloneTableBuilder
import com.cachesmith.library.util.db.internal.CreateTableBuilder
import com.cachesmith.library.util.db.DropTableBuilder
import com.cachesmith.library.util.db.models.ColumnObject
import com.cachesmith.library.util.db.internal.DatabaseUtils

internal class CacheSmithOpenHelper private constructor(val context: Context, val name: String, val version: Int, val entities: List<ObjectClass>) :
		SQLiteOpenHelper(context, name, null, version) {

	init {
		if (version > PreferencesManager.getVersion(context)) {
			this.writableDatabase
			PreferencesManager.saveVersion(context, version)
		}
	}

	companion object {
		private const val TAG = "CacheSmithHelper"
		private const val TABLE_SUFFIX = "_backup"

		private fun tableNotExists(context: Context, entity: ObjectClass): Boolean {
			val jsonTable = PreferencesManager.getTableJson(context, entity.qualifiedName)
			return jsonTable.isEmpty()
		}

		private fun entityChanged(context: Context, entity: ObjectClass): Boolean {
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
							if (columnAnnot.name.isBlank()) {
								if (field.name != jsonCollumn.name)
									return true
							} else if (columnAnnot.name != jsonCollumn.name) {
								return true
							}
						}

						if (field.type.name != jsonCollumn.type)
							return true

						if (annotationsChanged(field, jsonCollumn))
							return true
					}
				}
			}
			return false
		}
		
		private fun entitiesChanged(context: Context, entities: List<ObjectClass>): Boolean {
			var changed = false
			entities.forEach { entity ->
				entity.changed = entityChanged(context, entity)
				if(!changed) {
					changed = entity.changed
				}
			}
			return changed
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
		if (db == null) {
			Log.e(TAG, context.getString(R.string.error_database_null))
			return
		}

		entities.forEach { entity ->
			execCreateTable(db, entity)
		}
    }

	override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
		if (db == null) {
			Log.e(TAG, context.getString(R.string.error_database_null))
			return
		}

		entities.forEach { entity ->
			if (entity.changed) {
				if (tableNotExists(context, entity)) {
					execCreateTable(db, entity)

				} else {
					val jsonTable = PreferencesManager.getTableJson(context, entity.qualifiedName)

					db.beginTransaction()
					try {
						val cloneTableName = jsonTable.name.plus(TABLE_SUFFIX)

						execCloneTable(db, jsonTable.name, cloneTableName)
						execDropTable(db, jsonTable.name)

						execCloneTable(db, cloneTableName, jsonTable.name)
						execDropTable(db, cloneTableName)

						db.setTransactionSuccessful()
					} catch (e: Exception) {
						Log.e(TAG, context.getString(R.string.error_upgrade_database))
						e.printStackTrace()
					} finally {
					    db.endTransaction()
					}
				}
			}
		}
	}

	private fun execCreateTable(db: SQLiteDatabase?, entity: ObjectClass) {
		val jsonTable = JSONTable()
		val queryBuilder = CreateTableBuilder()

		jsonTable.name = entity.tableName
		queryBuilder.tableName = entity.tableName

		entity.fields.forEach field@{ field ->
			val columnObj = ColumnObject()
			val jsonColumn = JSONColumn()

			jsonColumn.field = field.name

			columnObj.name = field.columnName
			jsonColumn.name = columnObj.name

			columnObj.typeClass = field.type.clazz
			jsonColumn.type = field.type.name

			field.annotations.forEach annotation@{ annotation ->
				val jsonAnnotation = JSONAnnotation()

				jsonAnnotation.name = annotation.annotationClass.simpleName!!

				when(annotation) {
					is Column -> {
						if (annotation.type != DataType.NONE) {
							columnObj.typeName = annotation.type.value
						}
					}
					is Relationship -> {
						if (!annotation.query.isBlank()) {
							columnObj.foreignKeyQuery = annotation.query
						} else {
							if (annotation.type == RelationType.ONE_TO_MANY) {
								return@field
							}
							else if (annotation.type == RelationType.ONE_TO_ONE
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
		
		db!!.execSQL(sql)

		PreferencesManager.saveTableJson(context, entity.qualifiedName, jsonTable)
	}

	private fun execCloneTable(db: SQLiteDatabase?, tableName: String, newTableName: String) {
		val queryBuilder = CloneTableBuilder(tableName, newTableName)
		db!!.execSQL(queryBuilder.build())
	}

	private fun execDropTable(db: SQLiteDatabase?, tableName: String) {
		val queryBuilder = DropTableBuilder(tableName)
		db!!.execSQL(queryBuilder.build())
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
					}
					else if (entitiesChanged(context, entities)) {
						newVersion += INCREMENTAL
					}
					Log.i("TESTE", "New version: $newVersion")
				}
				else if (newVersion < 0) {
					throw NoVersionException()
				}

                return CacheSmithOpenHelper(context, dbName, newVersion, entities)
            }
        }
    }
}