package com.cachesmith.library.util.db.internal

import com.cachesmith.library.util.db.QueryBuilder
import com.cachesmith.library.util.db.SQLCommands

internal class RenameTableBuilder(val oldName: String, val newName: String) : QueryBuilder() {
    override fun build(): String {
        val query = StringBuffer()
        query.append(SQLCommands.ALTER_TABLE.value)
        query.append(SPACE)
        query.append(oldName)
        query.append(SPACE)
        query.append(SQLCommands.RENAME_TO.value)
        query.append(SPACE)
        query.append(newName)
        query.append(SPACE)
        return query.toString()
    }
}