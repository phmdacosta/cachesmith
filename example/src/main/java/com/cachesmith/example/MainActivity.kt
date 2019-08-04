package com.cachesmith.example

import android.os.Bundle
import android.support.constraint.solver.Cache
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import cachesmith.com.br.cachesmith.R
import com.cachesmith.example.dao.TesteDataSource
import com.cachesmith.example.dao.TesteJavaDataSource
import com.cachesmith.library.CacheSmith

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val cacheSmith = CacheSmith.create(this)
        cacheSmith.setDatabaseName("TesteCacheSmith")
        cacheSmith.setVersion(1)
        cacheSmith.initDatabase()
        val testeDataSource = cacheSmith.load(TesteDataSource::class)
        assert(testeDataSource is TesteDataSource)
        val testeJavaDataSource = cacheSmith.load(TesteJavaDataSource::class.java)
        assert(testeJavaDataSource is TesteJavaDataSource)
    }

}
