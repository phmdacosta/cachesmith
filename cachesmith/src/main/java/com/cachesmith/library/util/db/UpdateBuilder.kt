package com.cachesmith.library.util.db

open class UpdateBuilder(val tableName: String) : QueryBuilder() {
	
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
	
	override fun build(): String {
		return ""
	}
}