package com.cachesmith.library.util.db

import com.cachesmith.library.util.ObjectClass
import kotlin.reflect.KClass
import com.cachesmith.library.exceptions.SQLiteQueryException

open class SelectBuilder(val tableName: String) : FilterBuilder()  {
	
	companion object {
		const val ALL = "*".plus(SPACE)
		const val SEPARATOR = ",".plus(SPACE)
		const val EQUALS = "=".plus(SPACE)
		const val ERROR_MSG_FILTER = "Input a valid filter"
	}
	
	val selectColumns = mutableListOf<String>()
	var orderBy: String = ""
	var selectAll = true	
	
	fun addSelectColumn(columnName: String) {
		selectColumns.add(columnName)
		selectAll = false
	}
	
	fun orderBy(columnName: String) {
		orderBy = columnName
	}
	
	open fun buildSelectColumns(query: StringBuffer) {
		if (selectAll) {
			query.append(ALL)
		} else {
			val selectIter = selectColumns.iterator()
			while(selectIter.hasNext()) {
				query.append(selectIter.next())
				if (selectIter.hasNext()) {
					query.append(SEPARATOR)
				}
			}
		}
	}
	
	override fun build(): String {
		val query = StringBuffer()
		query.append(SQLCommands.SELECT.value)
		query.append(SPACE)
		
		buildSelectColumns(query)
		
		query.append(SQLCommands.FROM.value)
		query.append(SPACE)
		query.append(tableName)
		query.append(SPACE)
		
		// Where statement
		buildFilters(query)
		
		// Order by statement
		if (!orderBy.isBlank()) {
			query.append(SQLCommands.ORDER_BY.value)
			query.append(orderBy)
		}
		
		return query.toString()
	}
}