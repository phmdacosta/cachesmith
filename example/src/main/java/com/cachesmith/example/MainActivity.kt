package com.cachesmith.example

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import cachesmith.com.br.cachesmith.R
import com.cachesmith.example.dao.TesteDataSource
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

        val cacheSimth = CacheSmith.Builder.build(this)
        val testeDataSource = cacheSimth.load(TesteDataSource::class.java)
    }

}
