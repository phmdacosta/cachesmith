package com.cachesmith.example.java.dao;

import android.database.sqlite.SQLiteOpenHelper;

import com.cachesmith.library.DataSource;
import com.cachesmith.library.annotations.Entity;

@Entity("com.cachesmith.example.models.Teste")
public class TesteDataSource extends DataSource {
    public TesteDataSource(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }
}
