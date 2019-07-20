package com.cachesmith.library.util

import org.json.JSONArray
import org.json.JSONObject

class JSONColumn(): JSONObject() {
	
	var annotJsonArray = JSONArray()
	
	companion object {
		const val NAME = "name"
		const val FIELD = "field"
		const val TYPE = "type"
		const val ANNOTATIONS = "name"
		const val ANNOTATION = "name"
	}
	
	var name: String
		get() = getString(NAME)
		set(value) {
			put(NAME, value)
		}
	
	var classFieldName: String
		get() = getString(FIELD)
		set(value) {
			put(FIELD, value)
		}
	
	var type: String
		get() = getString(TYPE)
		set(value) {
			put(TYPE, value)
		}
	
	fun addAnnotationJson(jsonAnnot: JSONObject) {
		annotJsonArray.put(jsonAnnot)
		putColumnsJsonArray(annotJsonArray)
	}
	
	fun addAnnotationJson(jsonAnnot: JSONAnnotation) {
		annotJsonArray.put(jsonAnnot)
		putColumnsJsonArray(annotJsonArray)
	}
	
	fun putColumnsJsonArray(annotJsonArray: JSONArray) {
		put(ANNOTATIONS, annotJsonArray)
	}
}