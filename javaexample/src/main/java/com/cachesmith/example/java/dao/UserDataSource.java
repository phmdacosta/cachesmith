package com.cachesmith.example.java.dao;

import android.database.sqlite.SQLiteOpenHelper;

import com.cachesmith.library.DataSource;
import com.cachesmith.library.annotations.Entity;

@Entity("com.cachesmith.example.java.models.User")
public class UserDataSource extends DataSource {
    UserDataSource(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }
}
