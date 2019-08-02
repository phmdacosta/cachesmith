package com.cachesmith.library.util.db

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
open class CloneTableBuilder(val tableName: String = "") : QueryBuilder {
    /*CREATE TABLE copied AS SELECT * FROM mytable WHERE 0*/
    /*CREATE TABLE copied AS SELECT sql FROM sqlite_master WHERE type='table' AND name='mytable'*/

    companion object {
        const val SPACE = " "
        const val CREATE_TABLE = "CREATE TABLE".plus(SPACE)
        const val AS = "AS".plus(SPACE)
        const val SELECT = "SELECT".plus(SPACE)
        const val FROM = "FROM".plus(SPACE)
        const val WHERE = "WHERE".plus(SPACE)
        const val TABLE_SUFIX = "_backup"
    }

    override fun build(): String {
        val query = StringBuffer()
        query.append(CREATE_TABLE)
        query.append(tableName.plus(TABLE_SUFIX))
        query.append(SPACE)
        query.append(AS)
        query.append(SELECT)
        query.append("* ")
        query.append(FROM)
        query.append(tableName.plus(SPACE))
        query.append(WHERE)
        query.append("0")
        return query.toString()
    }
}