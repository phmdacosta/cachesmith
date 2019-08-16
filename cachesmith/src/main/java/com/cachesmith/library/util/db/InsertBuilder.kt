package com.cachesmith.library.util.db

import com.cachesmith.library.util.ObjectClass
import kotlin.reflect.KClass

open class InsertBuilder(val tableName: String) : QueryBuilder() {
	
	companion object {
		const val SEPARATOR = ",".plus(SPACE)
	}
	
	val columns = mutableMapOf<String, Any>()
	
	fun addParameter(columnName: String, value: Any) {
		columns.put(columnName, value);
	}
	
	override fun build(): String {
		
		val query = StringBuffer()
		query.append(SQLCommands.INSERT_INTO.value)
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
			} else {
				columnNames.append(SPACE)
				columnValues.append(SPACE)
			}
		}
		
		query.append(START_PARAM)
		query.append(columnNames.toString())
		query.append(END_PARAM)
		query.append(SQLCommands.VALUES.value)
		query.append(START_PARAM)
		query.append(columnValues.toString())	
		query.append(END_PARAM)
				
		return query.toString()
	}
}