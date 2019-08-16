package com.cachesmith.library.util.db

import com.cachesmith.library.exceptions.SQLiteQueryException

abstract class FilterBuilder : QueryBuilder() {
	
	companion object {
		const val EQUALS = "=".plus(SPACE)
		const val ERROR_MSG_FILTER = "Input a valid filter"
	}
	
	protected val filters = mutableListOf<Filter>()
	
	fun isFilterColumnValid(columnName: String): Boolean {
		if (columnName.contains("'"))
			return false
		
		val sqlCommands = enumValues<SQLCommands>();
		sqlCommands.forEach {
			if (columnName.contains(it.value))
				return false
		}
		
		return true
	}
	
	fun addAndFilter(fiter: String, args: Array<String>) {		
		addRawFilter(fiter, args, SQLCommands.AND.value)
	}
	
	fun addOrFilter(fiter: String, args: Array<String>) {
		addRawFilter(fiter, args, SQLCommands.OR.value)
	}
	
	fun addRawFilter(fiter: String, args: Array<String>, logicalOperator: String) {
		var query = fiter
		args.forEach { arg ->
			if (arg.contains("'"))
			throw SQLiteQueryException(ERROR_MSG_FILTER)
			
			enumValues<SQLCommands>().forEach {
				if (arg.contains(it.value))
					throw SQLiteQueryException(ERROR_MSG_FILTER)
			}
				
			query = query.replaceFirst("?", arg)
		}
		
		filters.add(Filter("", query, true, logicalOperator))
	}
	
	fun addFilterEquals(columnName: String, value: String) {
		if (isFilterColumnValid(columnName) || value.contains("'"))
			throw SQLiteQueryException(ERROR_MSG_FILTER)
		
		enumValues<SQLCommands>().forEach {
			if (value.contains(it.value))
				throw SQLiteQueryException(ERROR_MSG_FILTER)
		}
		
		val valueFilter = "'$value'"
		
		filters.add(Filter(columnName, valueFilter, false, SQLCommands.AND.value))
	}
	
	fun addFilterEquals(columnName: String, value: Char) {
		if (isFilterColumnValid(columnName))
			throw SQLiteQueryException(ERROR_MSG_FILTER)
		
		filters.add(Filter(columnName, Character.toString(value), false, SQLCommands.AND.value))
	}
	
	fun addFilterEquals(columnName: String, value: Int) {
		if (isFilterColumnValid(columnName))
			throw SQLiteQueryException(ERROR_MSG_FILTER)
		
		filters.add(Filter(columnName, value.toString(), false, SQLCommands.AND.value))
	}
	
	fun addFilterEquals(columnName: String, value: Double) {
		if (isFilterColumnValid(columnName))
			throw SQLiteQueryException(ERROR_MSG_FILTER)
		
		filters.add(Filter(columnName, value.toString(), false, SQLCommands.AND.value))
	}
	
	fun addFilterEquals(columnName: String, value: Float) {
		if (isFilterColumnValid(columnName))
			throw SQLiteQueryException(ERROR_MSG_FILTER)
		
		filters.add(Filter(columnName, value.toString(), false, SQLCommands.AND.value))
	}
	
	open fun buildFilters(query: StringBuffer) {
		if (filters.isNotEmpty()) {
			query.append(SQLCommands.WHERE.value)
			query.append(SPACE)			
			for (i in filters.indices) {
				val filter = filters.get(i)
				if (i > 0) {
					query.append(filter.logicalOperator)
					query.append(SPACE)
				}
				if (filter.rawQuery) {
					query.append(filter.filterValue)
					query.append(SPACE)
				} else {
					query.append(filter.columnName)
					query.append(SPACE)
					query.append(EQUALS)
					query.append(filter.filterValue)
					query.append(SPACE)
				}
			}
		}
	}
	
	protected class Filter(
		val columnName: String,
		val filterValue: String,
		val rawQuery: Boolean,
		val logicalOperator: String)
}