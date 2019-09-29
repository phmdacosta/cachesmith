package com.cachesmith.library.java;

import com.cachesmith.library.util.db.internal.CloneTableBuilder;
import com.cachesmith.library.util.db.CreateTableBuilder;
import com.cachesmith.library.util.db.DeleteBuilder;
import com.cachesmith.library.util.db.DropTableBuilder;

import org.junit.Test;

public class QueryBuildersTest {

    @Test
    public void cloneTableSQLTest() {
        String expected = "CREATE TABLE my_table_backup AS SELECT * FROM my_table WHERE 0";
        CloneTableBuilder sql = new CloneTableBuilder("my_table");
        assert expected.equals(sql.build());
    }

    @Test
    public void createTableSQLTest() {
        String expected = "CREATE TABLE my_table ( id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , name TEXT , some_unique_text TEXT UNIQUE , an_int INTEGER , foreign_id INTEGER , FOREIGN KEY ( foreign_id ) REFERENCES foreign_table ( id ) ) ";
        CreateTableBuilder sql = new CreateTableBuilder("my_table");
        sql.addColumn("id", "INTEGER", true, true, true, true);
        sql.addColumn("name", "TEXT");
        sql.addColumn("some_unique_text", "TEXT", false, false, false, true);
        sql.addColumn("an_int", "INTEGER");
        sql.addForeignKey("foreign_id", "INTEGER", "foreign_table", "id");
        assert expected.equals(sql.build());
    }

    @Test
    public void deleteDataSQLTest() {
        String expected = "DELETE FROM my_table WHERE name = 'xpto' ";
        DeleteBuilder sql = new DeleteBuilder("my_table");
        sql.addFilterEquals("name", "xpto");
        assert expected.equals(sql.build());
    }

    @Test
    public void dropTableSQLTest() {
        String expected = "DROP TABLE my_table";
        DropTableBuilder sql = new DropTableBuilder("my_table");
        assert expected.equals(sql.build());
    }
}
