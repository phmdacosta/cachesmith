package com.cachesmith.library.util.db

open class UpdateBuilder(val tableName: String) : FilterBuilder() {
	
	companion object {
		const val SEPARATOR = ",".plus(SPACE)
		const val EQUALS = "=".plus(SPACE)
	}
	
	val columnsToChange = mutableMapOf<String, Any>()
	
	fun addChangeColumn(column: String, value: Any) {
		var newValue = value
		if (value is String) {
			newValue = "'$value'"
		}
		columnsToChange.put(column, newValue)
	}
	
	fun buildColumnChanges(query: StringBuffer) {
		val columnToChangeIter = columnsToChange.iterator()
		while(columnToChangeIter.hasNext()) {
			val newChange = columnToChangeIter.next()
			query.append(newChange.key)
			query.append(SPACE)
			query.append(newChange.value)
			if (columnToChangeIter.hasNext()) {
				query.append(SEPARATOR)
			}
		}
	}
	
	override fun build(): String {
		val query = StringBuffer()
		
		query.append(SQLCommands.UPDATE.value)
		query.append(SPACE)
		query.append(tableName)
		query.append(SQLCommands.SET.value)
		query.append(SPACE)
		
		buildColumnChanges(query)
		
		buildFilters(query)
		
		return query.toString()
	}
}