package com.cachesmith.library.util

class CreateQueryBuilder : QueryBuilder {
	
	companion object {
		const val SPACE = " "
		const val SEPARATOR = ",".plus(SPACE)
		const val CREATE_TABLE = "CREATE TABLE".plus(SPACE)
		const val START_QUOTE = "(".plus(SPACE)
		const val END_QUOTE = ")".plus(SPACE)
		const val PRIMARY_KEY = "PRIMARY KEY".plus(SPACE)
		const val UNIQUE = "UNIQUE".plus(SPACE)
		const val AUTO_INCREMENT = "AUTO_INCREMENT".plus(SPACE)
		const val NOT_NULL = "NOT NULL".plus(SPACE)
		const val FOREIGN_KEY = "FOREIGN KEY"
		const val REFERENCES = "REFERENCES".plus(SPACE)
	}
	
	var tableName = ""
		set(value) {
			field = value.plus(" ")
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
		query.append(CREATE_TABLE)
		query.append(START_QUOTE)
		
		val iterColumns = columnsList.iterator()
		while (iterColumns.hasNext()) {
			val column = iterColumns.next()
			
			query.append(column.name.plus(SPACE))
			query.append(column.typeName.plus(SPACE))
			
			if (column.isPrimaryKey) {
				query.append(PRIMARY_KEY)
			}
			
			if (column.isUnique) {
				query.append(UNIQUE)
			}
			
			if (column.isAutoIncrement) {
				query.append(AUTO_INCREMENT)
			}
			
			if (column.isNotNull) {
				query.append(NOT_NULL)
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
				query.append(FOREIGN_KEY)
				query.append(START_QUOTE)
				query.append(column.name.plus(SPACE))
				query.append(END_QUOTE)
				query.append(REFERENCES)
				query.append(column.foreignKey.referenceTable.plus(SPACE))
				query.append(START_QUOTE)
				query.append(column.foreignKey.referenceColumn.plus(SPACE))
				query.append(END_QUOTE)
			}
		}
		
		query.append(END_QUOTE)
		return query.toString()
	}
}