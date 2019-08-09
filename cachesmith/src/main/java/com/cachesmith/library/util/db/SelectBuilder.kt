package com.cachesmith.library.util.db

import com.cachesmith.library.util.ObjectClass
import kotlin.reflect.KClass
import com.cachesmith.library.exceptions.SQLiteQueryException

open class SelectBuilder(val entity: ObjectClass) : QueryBuilder()  {
	
	companion object {
		const val ALL = "*".plus(SPACE)
		const val SEPARATOR = ",".plus(SPACE)
		const val EQUALS = "=".plus(SPACE)
		const val ERROR_MSG_FILTER = "Input a valid filter"
	}
	
	constructor(entity: Class<*>) : this(ObjectClass(entity))
	constructor(entity: KClass<*>) : this(ObjectClass(entity))
	
	val filters = mutableMapOf<String, Any>()
	val selectColumns = mutableListOf<String>()
	var orderBy: String = ""
	var selectAll = true
	
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
	
	fun addFilter(columnName: String, value: String) {
		if (isFilterColumnValid(columnName) || value.contains("'"))
			throw SQLiteQueryException(ERROR_MSG_FILTER)
		
		enumValues<SQLCommands>().forEach {
			if (value.contains(it.value))
				throw SQLiteQueryException(ERROR_MSG_FILTER)
		}
		
		val valueFilter = "'$value'"
		
		filters.put(columnName, valueFilter)
	}
	
	fun addFilter(columnName: String, value: Char) {
		if (isFilterColumnValid(columnName))
			throw SQLiteQueryException(ERROR_MSG_FILTER)
		
		filters.put(columnName, value)
	}
	
	fun addFilter(columnName: String, value: Int) {
		if (isFilterColumnValid(columnName))
			throw SQLiteQueryException(ERROR_MSG_FILTER)
		
		filters.put(columnName, value)
	}
	
	fun addFilter(columnName: String, value: Double) {
		if (isFilterColumnValid(columnName))
			throw SQLiteQueryException(ERROR_MSG_FILTER)
		
		filters.put(columnName, value)
	}
	
	fun addFilter(columnName: String, value: Float) {
		if (isFilterColumnValid(columnName))
			throw SQLiteQueryException(ERROR_MSG_FILTER)
		
		filters.put(columnName, value)
	}
	
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
	
	open fun buildFilters(query: StringBuffer) {
		if (filters.isNotEmpty()) {
			query.append(SQLCommands.WHERE.value)
			val filtersIter = filters.iterator()
			while(filtersIter.hasNext()) {
				val filter = filtersIter.next()
				
				query.append(filter.key)
				query.append(SPACE)
				query.append(EQUALS)
				query.append(filter.value)
				query.append(SPACE)
				
				if (filtersIter.hasNext()) {
					query.append(SQLCommands.AND.value)
				}
			}
		}
	}
	
	override fun build(): String {
		val query = StringBuffer()
		query.append(SQLCommands.SELECT.value)
		
		buildSelectColumns(query)
		
		query.append(SQLCommands.FROM.value)
		query.append(entity.tableName)
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