package com.cachesmith.library.kotlin

import com.cachesmith.library.util.db.CreateTableBuilder
import com.cachesmith.library.util.db.DeleteBuilder
import com.cachesmith.library.util.db.DropTableBuilder
import com.cachesmith.library.util.db.internal.CopyTableDataBuilder

import org.junit.Test

class QueryBuildersTest {

    @Test
    fun createTableSQLTest() {
        val expected = "CREATE TABLE my_table ( id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , name TEXT , some_unique_text TEXT UNIQUE , an_int INTEGER , foreign_id INTEGER , FOREIGN KEY ( foreign_id ) REFERENCES foreign_table ( id ) ) "
        val sql = CreateTableBuilder("my_table")
        sql.addColumn("id", "INTEGER", true, true, true, true)
        sql.addColumn("name", "TEXT")
        sql.addColumn("some_unique_text", "TEXT", false, false, false, true)
        sql.addColumn("an_int", "INTEGER")
        sql.addForeignKey("foreign_id", "INTEGER", "foreign_table", "id")
        assert(expected == sql.build())
    }

    @Test
    fun deleteDataSQLTest() {
        val expected = "DELETE FROM my_table WHERE name = 'xpto' "
        val sql = DeleteBuilder("my_table")
        sql.addFilterEquals("name", "xpto")
        assert(expected == sql.build())
    }

    @Test
    fun dropTableSQLTest() {
        val expected = "DROP TABLE my_table"
        val sql = DropTableBuilder("my_table")
        assert(expected == sql.build())
    }

    @Test
    fun copyTableDataSQLTest() {
        val expected = "INSERT INTO Teste_backup SELECT * FROM Teste "
        val sql = CopyTableDataBuilder("Teste", "Teste_backup")
        assert(expected == sql.build())
    }
}
