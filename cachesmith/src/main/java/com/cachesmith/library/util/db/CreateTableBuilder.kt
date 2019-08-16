package com.cachesmith.library.util.db

import com.cachesmith.library.util.db.models.ColumnObject

open class CreateTableBuilder : QueryBuilder() {

	companion object {
		const val SEPARATOR = ",".plus(SPACE)
	}
	
	var tableName = ""
		set(value) {
			field = value.plus(SPACE)
		}
	
	val columnsList = mutableListOf<ColumnObject>()
	val foreignKeysList = mutableListOf<ColumnObject>()
	
	fun addColumn(column: ColumnObject) {
		if (column.isForeignKey) {
			foreignKeysList.add(column)
		}
		columnsList.add(column)
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
			
			if (column.isUnique) {
				query.append(SQLCommands.UNIQUE.value)
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