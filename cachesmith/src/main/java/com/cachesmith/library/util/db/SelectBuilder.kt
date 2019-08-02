package com.cachesmith.library.util.db

import com.cachesmith.library.util.ObjectClass
import kotlin.reflect.KClass

open class SelectBuilder(val entity: ObjectClass) : QueryBuilder  {
	
	companion object {
		const val SPACE = " "
		const val ALL = "*".plus(SPACE)
		const val SEPARATOR = ",".plus(SPACE)
		const val SELECT = "SELECT".plus(SPACE)
		const val FROM = "FROM".plus(SPACE)
		const val WHERE = "WHERE".plus(SPACE)
		const val AND = "AND".plus(SPACE)
		const val ORDER_BY = "ORDER BY".plus(SPACE)
		const val EQUALS = "=".plus(SPACE)
		const val START_QUOTE = "(".plus(SPACE)
		const val END_QUOTE = ")".plus(SPACE)
	}
	
	constructor(entity: Class<*>) : this(ObjectClass(entity))
	constructor(entity: KClass<*>) : this(ObjectClass(entity))
	
	val filters = mutableMapOf<String, Any>()
	val selectColumns = mutableListOf<String>()
	var orderBy: String = ""
	var selectAll = true
	
	fun addFilter(columnName: String, value: Any) {
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
			query.append(WHERE)
			val filtersIter = filters.iterator()
			while(filtersIter.hasNext()) {
				val filter = filtersIter.next()
				
				query.append(filter.key)
				query.append(SPACE)
				query.append(EQUALS)
				query.append(filter.value)
				query.append(SPACE)
				
				if (filtersIter.hasNext()) {
					query.append(AND)
				}
			}
		}
	}
	
	override fun build(): String {
		val query = StringBuffer()
		query.append(SELECT)
		
		buildSelectColumns(query)
		
		query.append(FROM)
		query.append(entity.tableName)
		query.append(SPACE)
		
		// Where statement
		buildFilters(query)
		
		// Order by statement
		if (!orderBy.isBlank()) {
			query.append(ORDER_BY)
			query.append(orderBy)
		}
		
		return query.toString()
	}
}