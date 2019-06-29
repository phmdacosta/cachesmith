package com.cachesmith.library

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

class ChacheSmithProvider : ContentProvider() {

//    companion object {
//        @SuppressLint("StaticFieldLeak")
//        var cacheContext: Context? = null
//            private set
//    }

    override fun onCreate(): Boolean {
//        cacheContext = context
        return true
    }

    override fun insert(uri: Uri?, values: ContentValues?): Uri? {
        return null
    }

    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        return null
    }

    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun getType(uri: Uri?): String? {
        return null
    }

}