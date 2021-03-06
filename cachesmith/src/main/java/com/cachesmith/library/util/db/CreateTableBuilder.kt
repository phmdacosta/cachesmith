package com.cachesmith.library.util.db

import com.cachesmith.library.annotations.PrimaryKey
import com.cachesmith.library.util.db.QueryBuilder
import com.cachesmith.library.util.db.SQLCommands
import com.cachesmith.library.util.db.models.ColumnObject
import com.cachesmith.library.util.db.models.ForeignKeyObject

open class CreateTableBuilder(var tableName: String) : QueryBuilder() {

	companion object {
		const val SEPARATOR = ",".plus(SPACE)
	}
	
	private val columnsList = mutableListOf<ColumnObject>()
	private val foreignKeysList = mutableListOf<ColumnObject>()

	@JvmOverloads
	fun addColumn(name: String, type: String,
				  primaryKey: Boolean = false,
				  autoIncrement: Boolean = false,
				  notNull: Boolean = false,
				  unique: Boolean = false) {
		val column = ColumnObject()
		column.name = name
		column.typeName = type
		column.isPrimaryKey = primaryKey
		column.isAutoIncrement = autoIncrement
		column.isNotNull = notNull
		column.isUnique = unique
		columnsList.add(column)
	}

	fun addForeignKey(name: String, type: String,
					  referenceTable: String,
					  referenceColumn: String) {
		val column = ColumnObject()
		column.name = name
		column.foreignKey = ForeignKeyObject(referenceTable, referenceColumn, type)
		columnsList.add(column)
		foreignKeysList.add(column)
	}
	
	override fun build(): String {
		val query = StringBuffer()
		query.append(SQLCommands.CREATE_TABLE.value)
		query.append(SPACE)
		query.append(tableName)
		query.append(SPACE)
		query.append(START_PARAM)
		
		val iterColumns = columnsList.iterator()
		while (iterColumns.hasNext()) {
			val column = iterColumns.next()
			
			query.append(column.name)
			query.append(SPACE)

			if (column.isForeignKey) {
				query.append(column.foreignKey.referenceColumnType)
			} else {
				query.append(column.typeName)
			}
			query.append(SPACE)
			
			if (column.isPrimaryKey) {
				query.append(SQLCommands.PRIMARY_KEY.value)
				query.append(SPACE)
			}

			if (column.isAutoIncrement) {
				query.append(SQLCommands.AUTO_INCREMENT.value)
				query.append(SPACE)
			}

			if (column.isNotNull) {
				query.append(SQLCommands.NOT_NULL.value)
				query.append(SPACE)
			}
			
			if (column.isUnique) {
				query.append(SQLCommands.UNIQUE.value)
				query.append(SPACE)
			}
			
			if (iterColumns.hasNext()) {
				query.append(SEPARATOR)
			}
		}
		
		foreignKeysList.forEach { column ->
			if (!column.foreignKeyQuery.isBlank()) {
				query.append(column.foreignKeyQuery)
			} else {
				query.append(SEPARATOR)
				query.append(SQLCommands.FOREIGN_KEY.value)
				query.append(SPACE)
				query.append(START_PARAM)
				query.append(column.name)
				query.append(SPACE)
				query.append(END_PARAM)
				query.append(SQLCommands.REFERENCES.value)
				query.append(SPACE)
				query.append(column.foreignKey.referenceTable)
				query.append(SPACE)
				query.append(START_PARAM)
				query.append(column.foreignKey.referenceColumn)
				query.append(SPACE)
				query.append(END_PARAM)
			}
		}
		
		query.append(END_PARAM)
		return query.toString()
	}
}