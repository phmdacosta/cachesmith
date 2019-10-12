package com.cachesmith.library.util.db.internal

import com.cachesmith.library.util.db.QueryBuilder
import com.cachesmith.library.util.db.SQLCommands

internal open class CopyTableDataBuilder(val fromTableName: String = "", val toTableName: String = "") : QueryBuilder() {

    companion object {
        const val SEPARATOR = ",".plus(SPACE)
    }

    val columns = mutableListOf<String>()

    fun addColumn(columnName: String) {
        columns.add(columnName)
    }

    override fun build(): String {
        val query = StringBuffer()
        query.append(SQLCommands.INSERT_INTO.value)
		query.append(SPACE)
        query.append(toTableName)
        query.append(SPACE)
        query.append(SQLCommands.SELECT.value)
        query.append(SPACE)

        if (columns.isEmpty()) {
            query.append(" * ")
        } else {
            // Adding column's names
            val columnsIter = columns.iterator()
            while (columnsIter.hasNext()) {
                val column = columnsIter.next()

                query.append(column)

                if (columnsIter.hasNext()) {
                    query.append(SEPARATOR)
                } else {
                    query.append(SPACE)
                }
            }
        }

        query.append(SQLCommands.FROM.value)
		query.append(SPACE)
        query.append(fromTableName)
		query.append(SPACE)
        return query.toString()
    }
}