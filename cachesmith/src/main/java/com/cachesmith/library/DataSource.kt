package com.cachesmith.library

import android.database.sqlite.SQLiteOpenHelper

abstract class DataSource(dbHelper: SQLiteOpenHelper) {
    protected val database = dbHelper.writableDatabase
}
