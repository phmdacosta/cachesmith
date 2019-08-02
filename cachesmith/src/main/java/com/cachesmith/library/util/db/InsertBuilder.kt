package com.cachesmith.library.util.db

import com.cachesmith.library.util.ObjectClass
import kotlin.reflect.KClass

open class InsertBuilder(val tableName: String) : QueryBuilder {
	
	companion object {
		const val SPACE = " "
		const val SEPARATOR = ",".plus(SPACE)
		const val INSERT_INTO = "INSERT INTO".plus(SPACE)
		const val VALUES = "VALUES".plus(SPACE)
		const val START_QUOTE = "(".plus(SPACE)
		const val END_QUOTE = ")".plus(SPACE)
	}
	
	val columns = mutableMapOf<String, Any>()
	
	fun addParameter(columnName: String, value: Any) {
		columns.put(columnName, value);
	}
	
	override fun build(): String {
		
		val query = StringBuffer()
		query.append(INSERT_INTO)
		query.append(tableName)
		query.append(SPACE)
		
		// Adding column's names and values
		val columnNames = StringBuffer()
		val columnValues = StringBuffer()
		val columnsIter = columns.iterator()
		while(columnsIter.hasNext()) {
			val column = columnsIter.next()
			
			columnNames.append(column.key)
			columnValues.append(column.value)
			
			if (columnsIter.hasNext()) {
				columnNames.append(SEPARATOR)
				columnValues.append(SEPARATOR)
			}
			
			columnNames.append(SPACE)
			columnValues.append(SPACE)
		}
		
		query.append(START_QUOTE)
		query.append(columnNames.toString())
		query.append(END_QUOTE)
		query.append(VALUES)
		query.append(START_QUOTE)
		query.append(columnValues.toString())	
		query.append(END_QUOTE)
				
		return query.toString()
	}
}