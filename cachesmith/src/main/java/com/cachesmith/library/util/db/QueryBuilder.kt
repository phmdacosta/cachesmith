package com.cachesmith.library.util.db

/**
 * Query builders are designed to abstract query construction and make it easier  
 * for developers, reducing the need to write them. It was originally meant to 
 * just build SQLite queries, not to execute them.
 * Query builders are <b>not</b> final classes and it is free for developers 
 * customize it by hierarchy or kotlin extensions.
 */
abstract class QueryBuilder {
	
	companion object {
		const val SPACE = " "
		const val START_PARAM = "(".plus(SPACE)
		const val END_PARAM = ")".plus(SPACE)
	}

	/**
	 * Build the [SQLite] database query.
	 *
	 * @return SQLite database query
	 */
	abstract fun build(): String
}