package com.cachesmith.library

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.cachesmith.library.annotations.*
import com.cachesmith.library.exceptions.NoVersionException
import com.cachesmith.library.util.*
import com.cachesmith.library.util.db.DropTableBuilder
import com.cachesmith.library.util.db.internal.*
import com.cachesmith.library.util.db.internal.CopyTableDataBuilder
import com.cachesmith.library.util.db.internal.CreateTableBuilder
import com.cachesmith.library.util.db.internal.DatabaseUtils
import com.cachesmith.library.util.db.internal.RenameTableBuilder
import com.cachesmith.library.util.db.models.ColumnObject
import java.lang.UnsupportedOperationException

internal class CacheSmithOpenHelper private constructor(val context: Context, val name: String, val version: Int, val entities: List<ObjectClass>) :
		SQLiteOpenHelper(context, name, null, version) {

	init {
		/*
		 * Check if the version was changed. If it's the case,
		 * load the database to execute onCreate and onUpgrade functions. 
		 */
		if (version > PreferencesManager.getVersion(context)) {
			this.writableDatabase
			PreferencesManager.saveVersion(context, version)
		}
	}

	companion object {
		private const val TAG = "CacheSmithHelper"
		private const val TABLE_SUFFIX = "_backup"

		/**
		 * Check if table of model was already created.
		 * <p>
		 * It verifies the existence of a JSON object that is generated when create the table.
		 * <p>
		 * @property Context context of the application
		 * @property ObjectClass a object of custom class to manage class data.		 
		 * @return true if JSON exists, false otherwise 
		 */
		private fun tableNotExists(context: Context, entity: ObjectClass): Boolean {
			val jsonTable = PreferencesManager.getTableJson(context, entity.qualifiedName)
			return jsonTable.isEmpty()
		}

		/**
		 * Check if class model was changed.
		 * <p>
		 * It compare the structure of the model saved in JSON file, when it creates the table,
		 * with the new structure received from application.
		 * <p>
		 * @property Context context of the application
		 * @property ObjectClass a object of custom class to manage class data.		 
		 * @return true if the new structure of the model is different from the saved one,
		 *		   false otherwise. 
		 */
		private fun entityChanged(context: Context, entity: ObjectClass): Boolean {
			// Check table existence
			if (tableNotExists(context, entity))
				return true

			val jsonTable = PreferencesManager.getTableJson(context, entity.qualifiedName)

			// Check table name changed
			if (entity.tableName != jsonTable.name)
				return true

			/*
			 * Check if number of columns changed.
			 * Column dropping are not supported by SQLite.
			 */
			if (jsonTable.columnQuantity < entity.fields.size)
				return true
			else if (jsonTable.columnQuantity > entity.fields.size)
				throw UnsupportedOperationException(context.getString(R.string.error_drop_column_not_supported))

			// Check changes in each field
			entity.fields.forEach { field ->
				jsonTable.listJsonColumns().forEach { jsonCollumn ->
					if (field.name == jsonCollumn.field) {
						val columnAnnot = (field.annotations.find { it is Column })?.let { it as Column }
						// Check name of column
						if (columnAnnot == null) {
							/*
 							 * If there is no annotation for this field, check if the name
 							 * of the column is different from the name of the field
							 */
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

						// Check if any bannotation was added or removed.
						if (annotationsChanged(context, field, jsonCollumn))
							return true

						// Type changes are not supported by SQLite
						if (field.type.name != jsonCollumn.type)
							throw UnsupportedOperationException(context.getString(R.string.error_type_change_not_supported))
					}
				}
			}
			return false
		}
		
		/**
		 * Check if classes of all models were changed.
		 * <p>
		 * It compare the structure of the model saved in JSON file, when it creates the table,
		 * with the new structure received from application.
		 * <p>
		 * @property Context context of the application
		 * @property List<ObjectClass> List of objects of a manager of model's class data.		 
		 * @return true if the new structure of the any model is different from the saved one,
		 *		   false otherwise. 
		 */
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
		
		/**
		 * Check if a annotation was added or removed from class model.
		 * <p>
		 * It compare the structure of the model saved in JSON file, when it creates the table,
		 * with the new structure received from application.
		 * <p>
		 * @property ObjectField a object of custom class to manage field/property data
		 * @property JSONColumn JSON object of a column.		 
		 * @return true if the annotations in a model are different from earlier,
		 *		   false otherwise. 
		 */
		private fun annotationsChanged(context: Context, field: ObjectField, json: JSONColumn): Boolean {
			field.annotations.forEach { fieldAnnot ->
				// Type changes are not supported by SQLite
				if (fieldAnnot is Column
						&& fieldAnnot.type != DataType.NONE
						&& fieldAnnot.type.value != json.type) {
					throw UnsupportedOperationException(context.getString(R.string.error_type_change_not_supported))
				}

				val jsonAnnotation = JSONAnnotation()
				jsonAnnotation.name = fieldAnnot.annotationClass.simpleName!!
				if (!json.listAnnotationsJson().contains(jsonAnnotation))
					return true
			}
			return false
		}
	}
	
	/**
	 * Function to create database.
	 */
    override fun onCreate(db: SQLiteDatabase?) {
		if (db == null) {
			Log.e(TAG, context.getString(R.string.error_database_null))
			return
		}

		entities.forEach { entity ->
			val jsonTable = execCreateTable(db, entity)
			// Saving model's structure in a JSON file to check changes on future.
            PreferencesManager.saveTableJson(context, entity.qualifiedName, jsonTable)
		}
    }

	/**
	 * Function to upgrade database. It runs if the new version is different from the old version.
	 */
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
					val oldJsonTable = PreferencesManager.getTableJson(context, entity.qualifiedName)

					db.beginTransaction()
					try {
						val oldTableName = oldJsonTable.name.plus(TABLE_SUFFIX)

						// Rename the old table
                        execRenameTable(db, oldJsonTable.name, oldTableName)

						// Create the new table
						val newJsonTable = execCreateTable(db, entity)

						// Coping data to the new table and drop the old one
						execCopyTable(db, oldJsonTable, entity)
						execDropTable(db, oldTableName)

						db.setTransactionSuccessful()

						// Saving new model's structure in a JSON file to check changes on future.
						PreferencesManager.saveTableJson(context, entity.qualifiedName, newJsonTable)
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

	/**
	 * Create the table of a model.
	 * <p>
	 * It also saves a JSON result of model's structure to verify future changes in class.
	 * <p>
	 * @property SQLiteDatabase database object.
	 * @property ObjectClass a object of custom class to manage class data.
	 * @return JSON object of the created table.
	 */
	private fun execCreateTable(db: SQLiteDatabase?, entity: ObjectClass): JSONTable {
		val jsonTable = JSONTable()
		val queryBuilder = CreateTableBuilder()

		var tableName = entity.tableName

        // Debug log
		Log.d(TAG, context.getString(R.string.debug_log_create_table, tableName))

		/*
 		 * Getting table name.
 		 * If the annotation Table is setted on this model with a table name,
 		 * uses this defined name. Otherwise, it uses the class own name.
		 */
		jsonTable.name = tableName
		queryBuilder.tableName = tableName

		// Working on model's fields (table's columns)
		entity.fields.forEach field@{ field ->
			val columnObj = ColumnObject()
			val jsonColumn = JSONColumn()

			jsonColumn.field = field.name

			columnObj.name = field.columnName
			jsonColumn.name = columnObj.name

			columnObj.typeClass = field.type.clazz
			jsonColumn.type = field.type.name

			// Check annotations on fields
			field.annotations.forEach annotation@{ annotation ->
				val jsonAnnotation = JSONAnnotation()

				jsonAnnotation.name = annotation.annotationClass.simpleName!!

				when(annotation) {
					is Column -> { // Column annotation
						if (annotation.type != DataType.NONE) {
							columnObj.typeName = annotation.type.value
						}
					}
					is Relationship -> { // Relationship annotation
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
					is PrimaryKey -> columnObj.isPrimaryKey = true // PrimaryKey annotation
					is Unique -> columnObj.isUnique = true // Unique annotation
					is AutoIncrement -> columnObj.isAutoIncrement = true // AutoIncrement annotation
					is NotNullable -> columnObj.isNotNull = true // NotNullable annotation
				}

				jsonColumn.addAnnotationJson(jsonAnnotation)
			}

			if ("" != jsonColumn.name) {
				jsonTable.addColumnJson(jsonColumn)
			}

			queryBuilder.addColumn(columnObj)
		}

		// Executing query to create table.
		val sql = queryBuilder.build()
        // Debug log
		Log.d(TAG, sql)
		db!!.execSQL(sql)

        // Return the table's JSON with the structure of the new table.
        return jsonTable
	}

	/**
	 * Copy data between tables.
	 * @property SQLiteDatabase database object.
	 * @property JSONTable JSON of the old table.
	 * @property ObjectClass class of the new model's version.
	 */
	private fun execCopyTable(db: SQLiteDatabase?, oldJsonTable: JSONTable, entity: ObjectClass) {
		val queryBuilder = CopyTableDataBuilder(oldJsonTable.name, entity.tableName)

        // Copy data only from columns that exist in old table
		oldJsonTable.listJsonColumns().forEach { oldJsonColumn ->
			queryBuilder.addColumn(oldJsonColumn.name)
        }

        // Debug log
        Log.d(TAG, context.getString(R.string.debug_log_copy_table, oldJsonTable.name, entity.tableName))
        Log.d(TAG, queryBuilder.build())

        db!!.execSQL(queryBuilder.build())
	}

	/**
	 * Rename the table.
	 * @property SQLiteDatabase database object.
	 * @property String old name.
	 * @property String new name.
	 */
    private fun execRenameTable(db: SQLiteDatabase?, tableName: String, toTableName: String) {
        val queryBuilder = RenameTableBuilder(tableName, toTableName)

        // Debug log
        Log.d(TAG, context.getString(R.string.debug_log_rename_table, tableName, toTableName))
        Log.d(TAG, queryBuilder.build())

        db!!.execSQL(queryBuilder.build())
    }

	/**
	 * Delete a table in database.
	 * @property SQLiteDatabase database object.
	 * @property String name of the table that will be removed.
	 */
	private fun execDropTable(db: SQLiteDatabase?, tableName: String) {
		val queryBuilder = DropTableBuilder(tableName)
		db!!.execSQL(queryBuilder.build())
	}

	/**
	 * Create a relational table for two models when its relationship is [MANY_TO_MANY].
	 * @property SQLiteDatabase database object.
	 * @property ObjectClass a object of the current model to manage its class data.
	 * @property ObjectClass a object of other model to manage its class data.
	 */
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

			/**
			 * Build the class instance and loads database if any changes are found in models.
			 * @property Context context of the application
			 * @property List<ObjectClass> List of objects of a manager of model's class data.		 
			 * @return class instance. 
			 */
			@Throws(NoVersionException::class)
            fun buid(context: Context, entities: List<ObjectClass>): CacheSmithOpenHelper {

                val dbName = PreferencesManager.getDatabaseName(context)
				var newVersion: Int = PreferencesManager.getVersion(context)
				val manualVersion = PreferencesManager.getManualVersionCheck(context)

				if (!manualVersion) {
					// If the flag of a manual version control is false, the library will manage it
					if (newVersion < 0) {
						newVersion = BASE_VERSION
					}
					else if (entitiesChanged(context, entities)) {
						newVersion += INCREMENTAL
					}
				}
				else if (newVersion < 0) {
					// If a manual control is setted and no version number defined, throws a exception
					throw NoVersionException()
				}

                return CacheSmithOpenHelper(context, dbName, newVersion, entities)
            }
        }
    }
}