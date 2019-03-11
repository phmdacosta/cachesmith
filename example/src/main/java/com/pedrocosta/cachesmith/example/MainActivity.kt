package com.pedrocosta.cachesmith.example

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import cachesmith.com.br.cachesmith.R

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

//        val k = Teste::class.java
//
//        Log.i("TESTE", "DeclaredFields:")
//        k.declaredFields.forEach {field ->
//            try {
//                k.getMethod("get".plus(field.name.capitalize()))
//            } catch (e: Exception) {
//                return
//            }
//
//            Log.i("TESTE", field.name.plus(": ".plus(field.type.name)))
//
//            if (field.annotations != null) {
//                field.annotations.forEach {
//                    Log.i("TESTE", "annotation: ".plus(it.annotationClass.simpleName))
//                }
//            }
//        }
//
//        Log.i("TESTE", "Fields:")
//        k.fields.forEach {
//            Log.i("TESTE", it.name.plus(": ".plus(it.type.name)))
//            if (it.annotations != null) {
//                it.annotations.forEach {
//                    Log.i("TESTE", "annotation: ".plus(it.annotationClass.simpleName))
//                }
//            }
//        }
    }

}
