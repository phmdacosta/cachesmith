package com.cachesmith.library.util.db

import com.cachesmith.library.util.ObjectClass

open class DeleteBuilder(val tableName: String) : FilterBuilder() {
	
	override fun build(): String {
		val query = StringBuffer()
		
		query.append(SQLCommands.DELETE)
		query.append(SPACE)
		query.append(SQLCommands.FROM)
		query.append(SPACE)
		query.append(tableName)
		query.append(SPACE)
		
		buildFilters(query)
		
		return query.toString()
	}
}