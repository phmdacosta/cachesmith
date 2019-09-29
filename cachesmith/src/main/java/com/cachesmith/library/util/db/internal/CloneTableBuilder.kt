package com.cachesmith.library.util.db.internal

import com.cachesmith.library.util.db.QueryBuilder
import com.cachesmith.library.util.db.SQLCommands

/**
 * A QueryBuilder that makes a query for clone a table in {@link SQLite} database.
 * 
 * Query builders are designed to abstract query construction and make it easier  
 * for developers, reducing the need to write them. It was originally meant to 
 * just build SQLite queries, not to execute them.
 * Query builders are <b>not</b> final classes and it is free for developers 
 * customize it by hierarchy or kotlin extensions.
 *
 * This class already contains query commands to construct a query to execute a
 * copy of the table.
 */
internal open class CloneTableBuilder(val fromTableName: String = "", val newTableName: String = "") : QueryBuilder() {

    override fun build(): String {
        val query = StringBuffer()
        query.append(SQLCommands.CREATE_TABLE.value)
		query.append(SPACE)
        query.append(newTableName)
        query.append(SPACE)
        query.append(SQLCommands.AS.value)
		query.append(SPACE)
        query.append(SQLCommands.SELECT.value)
        query.append(" * ")
        query.append(SQLCommands.FROM.value)
		query.append(SPACE)
        query.append(fromTableName)
		query.append(SPACE)
        query.append(SQLCommands.WHERE.value)
		query.append(SPACE)
        query.append("0")
        return query.toString()
    }
}