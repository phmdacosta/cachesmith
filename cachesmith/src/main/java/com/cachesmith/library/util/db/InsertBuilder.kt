package com.cachesmith.library.util.db

import com.cachesmith.library.util.ObjectClass

class InsertBuilder<T>(val entityClass: ObjectClass) : QueryBuilder {
	
	companion object {
		const val SPACE = " "
		const val SEPARATOR = ",".plus(SPACE)
		const val INSERT_INTO = "INSERT INTO".plus(SPACE)
		const val VALUES = "VALUES".plus(SPACE)
		const val START_QUOTE = "(".plus(SPACE)
		const val END_QUOTE = ")".plus(SPACE)
	}
	
	val entity: T? = null
	
	override fun build(): String {
		if (entity == null)
			throw IllegalArgumentException("Entity can not be null")
		
		val query = StringBuffer()
		query.append(INSERT_INTO)
		query.append(entityClass.tableName)
		query.append(SPACE)
		query.append(START_QUOTE)
		
		val fieldsIter = entityClass.fields.iterator()
		while(fieldsIter.hasNext()) {
			val field = fieldsIter.next()
			
			if (fieldsIter.hasNext())
				query.append(SEPARATOR)
		}
		
		return ""
	}
}