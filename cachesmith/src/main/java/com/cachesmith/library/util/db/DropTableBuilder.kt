package com.cachesmith.library.util.db

class DropTableBuilder : QueryBuilder {

	companion object {
		const val SPACE = " "
		const val DROP_TABLE = "DROP TABLE".plus(SPACE)
	}

	var tableName = ""
	
	override fun build(): String {
		val query = StringBuffer()
		query.append(DROP_TABLE)
		query.append(tableName)
		return query.toString()
	}
}