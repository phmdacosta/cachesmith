package com.cachesmith.example.dao;

import android.database.sqlite.SQLiteOpenHelper;

import com.cachesmith.library.DataSource;
import com.cachesmith.library.annotations.Entity;

@Entity("com.cachesmith.example.models.TesteJava")
public class TesteJavaDataSource extends DataSource {
    public TesteJavaDataSource(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }
}
