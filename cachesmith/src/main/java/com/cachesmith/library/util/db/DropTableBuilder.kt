package com.cachesmith.library.util.db

open class DropTableBuilder : QueryBuilder() {
	
	var tableName = ""
	
	override fun build(): String {
		val query = StringBuffer()
		query.append(SQLCommands.DROP_TABLE.value)
		query.append(SPACE)
		query.append(tableName)
		return query.toString()
	}
}