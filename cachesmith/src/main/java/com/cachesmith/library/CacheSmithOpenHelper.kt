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

class CacheSmithOpenHelper private constructor(val context: Context, val name: String, val version: Int, private val entity: Class<*>):
        SQLiteOpenHelper(context, name, null, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        Log.i("TESTE", "CacheSmithOpenHelper.onCreate")

		val tableName = entity.getTableName()

		execCreateTable(db, tableName, true)
    }

	override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
		Log.i("TESTE", "CacheSmithOpenHelper.onUpgrade")

		if (!entityChanged()) {
			return
		}

		val jsonTable = PreferencesManager.getTableJson(context, entity.name)

		execCloneTable(db, jsonTable.name)
		// TODO insert data from origin table to clone table
		execDropTable(db, jsonTable.name)

		val newTableName = entity.getTableName()
		execCreateTable(db, newTableName, true)

		// TODO insert data from clone table to new table
		val cloneTableName = jsonTable.name.plus(CloneTableBuilder.TABLE_SUFIX)
		execDropTable(db, cloneTableName)

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

	private fun execCreateTable(db: SQLiteDatabase?, tableName: String, saveRefBackup: Boolean) {
		val jsonTable = JSONTable()
		val queryBuilder = CreateTableBuilder()

		jsonTable.name = tableName
		queryBuilder.tableName = tableName

		entity.getValidFields().forEach { field ->
			val columnObj = ColumnObject()
			val jsonColumn = JSONColumn()

			jsonColumn.field = field.name

			columnObj.typeClass = field.type

			val jsonAnnotation = JSONAnnotation()

			columnObj.name = field.getColumnName()
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

			if ("" != jsonColumn.name) {
				jsonTable.addColumnJson(jsonColumn)
			}

			queryBuilder.addColumn(columnObj)
		}

		val sql = queryBuilder.build()

		if (saveRefBackup) {
			PreferencesManager.saveTableJson(context, entity.name, jsonTable)
		}

		Log.i("TESTE", sql)
		Log.i("TESTE", jsonTable.toString())
	}

	private fun execCloneTable(db: SQLiteDatabase?, tableName: String) {
		val queryBuilder = CloneTableBuilder()
		queryBuilder.tableName = tableName
		Log.i("TESTE", queryBuilder.build())
	}

	private fun execDropTable(db: SQLiteDatabase?, tableName: String) {
		val queryBuilder = DropTableBuilder()
		queryBuilder.tableName = tableName
		Log.i("TESTE", queryBuilder.build())
	}

	private fun entityChanged(): Boolean {

		val jsonTable = PreferencesManager.getTableJson(context, entity.name)

		val entityTableName = entity.getTableName()
		if (entityTableName != jsonTable.name)
			return true

		if (jsonTable.columnQuantity != entity.getValidFields().size)
			return true

		entity.getValidFields().forEach { field->
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

					if (field.type.name != jsonCollumn.type) {
						return true
					}

					if (annotationsChanged(field, jsonCollumn)) {
						return true
					}
				}
			}
		}

		return false
	}

	private fun annotationsChanged(field: Field, json: JSONColumn): Boolean {
		field.annotations.forEach { fieldAnnot ->
			val jsonAnnotation = JSONAnnotation()
			jsonAnnotation.name = fieldAnnot.annotationClass.simpleName!!
			if (!json.listAnnotationsJson().contains(jsonAnnotation)) {
				return true
			}
		}
		return false
	}
	
	private fun getForeignKeyObject(annotation: Relationship, target: Class<*>): ForeignKeyObject {
		val foreignKey = ForeignKeyObject(annotation.targetTable, annotation.targetColumn)
		foreignKey.onDeleteAction = annotation.onDelete
		foreignKey.onUpdateAction = annotation.onUpdate
			
		if (!foreignKey.referenceTable.isBlank()) {
			foreignKey.referenceTable = target.getTableName()
		}
		
		if (!foreignKey.referenceColumn.isBlank()) {
			target.getValidFields().forEach { field ->
				field.annotations.forEach { annot ->
					 when(annot) {
						 is PrimaryKey -> {
							 foreignKey.referenceColumn = field.getColumnName()
						 }
					 }
				}
			}
		}
		return foreignKey
	}

	private fun createRelationalTable(db: SQLiteDatabase?, target: Class<*>) {
		val queryBuilder = CreateTableBuilder()
		
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
				when(annotation) {
					is PrimaryKey -> {
						columnObj.name = field.name
						columnObj.typeClass = field.type
					}
					is Relationship -> {
						val foreignKeyObj = getForeignKeyObject(annotation, field.type)
						columnObj.foreignKey = foreignKeyObj
					}
				}
			}
		}
		
		return columnObj
	}

    class Builder {

        companion object {

			@Throws(NoVersionException::class)
            fun buid(context: Context, entity: Class<*>): CacheSmithOpenHelper {

                val dbName = PreferencesManager.getDatabaseName(context)
				val newVersion: Int = PreferencesManager.getVersion(context)

				if (newVersion < 0) {
					throw NoVersionException("Could not get version for database.")
				}

                return CacheSmithOpenHelper(context, dbName, newVersion, entity)
            }
        }
    }
}

fun Class<*>.getValidFields(): Array<Field> {
	val fields = mutableListOf<Field>()
	this.declaredFields.forEach { field ->
		try {
			this.getMethod("get".plus(field.name.capitalize()))
		} catch (e: Exception) {
			return@forEach
		}

		fields.add(field)
	}
	return fields.toTypedArray()
}

fun Class<*>.getTableName(): String {
	var tableName = this.name
	val tableAnnot = (this.annotations.find { it is Table })?.let { it as Table }
	if (tableAnnot!= null && !tableAnnot.name.isBlank()) {
		tableName = tableAnnot.name
	}
	return tableName
}

fun Field.getColumnName(): String {
	var columnName = this.name
	this.annotations.forEach { columnAnnot ->
		when(columnAnnot) {
			is Column -> {
				if (!columnAnnot.name.isBlank()) {
					columnName = columnAnnot.name
				}
			}
		}
	}
	return columnName
}