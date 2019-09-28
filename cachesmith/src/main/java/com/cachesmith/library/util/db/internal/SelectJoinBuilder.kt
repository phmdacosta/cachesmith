package com.cachesmith.library.util.db.internal

import com.cachesmith.library.util.ObjectClass
import com.cachesmith.library.util.JoinType
import com.cachesmith.library.exceptions.SQLiteQueryException
import com.cachesmith.library.util.db.SQLCommands
import com.cachesmith.library.util.db.SelectBuilder
import com.cachesmith.library.util.db.models.ColumnObject
import com.cachesmith.library.util.db.models.ForeignKeyObject

internal open class SelectJoinBuilder(val principal: ObjectClass, val joiTtype: JoinType) : SelectBuilder(principal.tableName) {
	
	companion object {
		const val DOT = ".".plus(SPACE)
	}
	
	private var joinTables = mutableListOf<ObjectClass>()
	private val joinFilters = mutableListOf<Join>()
	private val selectColumnsMap = mutableMapOf<String, String>()
	
	fun joinTable(table: ObjectClass) {
		if (!containsTable(table.tableName))
			joinTables.add(table)
	}
	
	fun addSelectColumn(tableName: String, columnName: String) {
		if (!containsTable(tableName))
			throw SQLiteQueryException("Table $tableName not added.")
		selectColumnsMap.put(tableName, columnName)
		selectAll = false
	}
	
	fun addJoinFilter(joinType: JoinType, firstTable: ObjectClass, secondTable: ObjectClass) {
		if (firstTable.tableName != principal.tableName && !containsTable(firstTable.tableName))
			joinTable(firstTable)
		
		if (!containsTable(secondTable.tableName))
			joinTable(secondTable)
		
		joinFilters.add(Join(firstTable, secondTable, joinType))
	}
	
	fun containsTable(tableName: String): Boolean {
		joinTables.forEach { objClass ->
			if (objClass.tableName == tableName)
				return true
		}
		return false;
	}
	
	fun buildTables(query: StringBuffer) {
		query.append(principal.tableName)
		query.append(SEPARATOR)
		query.append(SPACE)
		
		val tablesIter = joinTables.iterator()
		while(tablesIter.hasNext()) {
			val tableObject = tablesIter.next()
			query.append(tableObject.tableName)
			if (tablesIter.hasNext()) {
				query.append(SEPARATOR)
				query.append(SPACE)
			}
		}
	}
	
	fun buildJoinFilters(query: StringBuffer) {
		val joinsIter = joinFilters.iterator()
		while(joinsIter.hasNext()) {
			val joinObject = joinsIter.next()
			query.append(joinObject.build())
			if (joinsIter.hasNext()) {
				query.append(SEPARATOR)
				query.append(SPACE)
			}
		}
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
					query.append(SPACE)
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
		query.append(principal.tableName)
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
	
	private class Join(val first: ObjectClass, val second: ObjectClass, val type: JoinType) {
		fun build(): String {
			val filter = StringBuffer()
			when(type) {
				JoinType.CROSS_JOIN -> filter.append(SQLCommands.CROSS_JOIN.value)
				JoinType.INNER_JOIN -> filter.append(SQLCommands.INNER_JOIN.value)
				JoinType.OUTER_JOIN -> filter.append(SQLCommands.OUTER_JOIN.value)
			}
			filter.append(SPACE)
			filter.append(SQLCommands.ON.value)
			filter.append(SPACE)
			filter.append(first.tableName)
			filter.append(DOT)
			
			var foreignKey: ForeignKeyObject? = null
			first.fields.forEach {
				val column = ColumnObject(it)
				if (column.isForeignKey && column.foreignKey.referenceTable.equals(second.tableName)) {
					foreignKey = column.foreignKey
					filter.append(column.name)
				}				
			}
			
			if (foreignKey == null) {
				throw SQLiteQueryException("Could not find foreign key of ${second.tableName} in ${first.tableName}")
			}
			
			filter.append(EQUALS)
			filter.append(second.tableName)
			filter.append(DOT)
			
			second.fields.forEach {
				val column = ColumnObject(it)
				if (column.isPrimaryKey && foreignKey!!.referenceColumn.equals(column.name))
					filter.append(column.name)
			}
			return filter.toString()
		}
	}
}