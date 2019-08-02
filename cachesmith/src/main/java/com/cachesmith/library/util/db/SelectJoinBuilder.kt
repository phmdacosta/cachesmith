package com.cachesmith.library.util.db

import com.cachesmith.library.util.ObjectClass
import com.cachesmith.library.util.JoinType

open class SelectJoinBuilder(val principal: ObjectClass, val joiTtype: JoinType) : SelectBuilder(principal) {
	
	companion object {
		const val AS = "AS".plus(SPACE)
		const val DOT = ".".plus(SPACE)
	}
	
	var joinTables = mutableListOf<ObjectClass>()
	val selectColumnsMap = mutableMapOf<String, String>()
	
	fun joinTable(table: ObjectClass) {
		joinTables.add(table)
	}
	
	fun addSelectColumn(tableName: String, columnName: String) {
		if (containsTable(tableName))
		selectColumnsMap.put(tableName, columnName)
		selectAll = false
	}
	
	fun buildTables(query: StringBuffer) {
		
	}
	
	fun containsTable(tableName: String): Boolean {
		joinTables.forEach { objClass ->
			if (objClass.tableName == tableName)
				return true
		}
		return false;
	}
	
	override fun buildSelectColumns(query: StringBuffer) {
		if (selectAll) {
			query.append(ALL)
		} else {
			val selectIter = selectColumnsMap.iterator()
			while(selectIter.hasNext()) {
				val selectColumn = selectIter.next()
				query.append(selectColumn.key)
				query.append(DOT)
				query.append(selectColumn.value)
				if (selectIter.hasNext()) {
					query.append(SEPARATOR)
				}
			}
		}
	}
	
	override fun buildFilters(query: StringBuffer) {
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