package com.cachesmith.library.util

import org.json.JSONArray
import org.json.JSONObject

class JSONTable() {

	companion object {
		const val NAME = "name"
		const val QUANTITY = "quantity"
		const val COLUMNS = "columns"
	}

	private var rawJson = JSONObject()
	private var columnsJsonArray = JSONArray()

	constructor(json: String) : this() {
		rawJson = JSONObject(json)
	}
	
	var name: String
		get() = rawJson.getString(NAME)
		set(value) {
			rawJson.put(NAME, value)
		}
	
	var columnQuantity: Int
		get() = rawJson.getInt(QUANTITY)
		set(value) {
			rawJson.put(QUANTITY, value)
		}
	
	fun addColumnJson(jsonColumn: JSONColumn) {
		columnsJsonArray.put(jsonColumn.toJSONObject())
		columnQuantity++
		putColumnsJsonArray(columnsJsonArray)
	}
	
	fun putColumnsJsonArray(columnsJsonArray: JSONArray) {
		rawJson.put(COLUMNS, columnsJsonArray)
	}

	override fun toString(): String {
		return rawJson.toString()
	}
}

class JSONColumn {

	companion object {
		const val NAME = "name"
		const val TYPE = "type"
		const val ANNOTATIONS = "name"
	}

	private var rawJson = JSONObject()
	private var annotJsonArray = JSONArray()

	var name: String
		get() = rawJson.getString(NAME)
		set(value) {
			rawJson.put(NAME, value)
		}

	var type: String
		get() = rawJson.getString(TYPE)
		set(value) {
			rawJson.put(TYPE, value)
		}

	fun addAnnotationJson(jsonAnnot: JSONAnnotation) {
		annotJsonArray.put(jsonAnnot.toJSONObject())
		putColumnsJsonArray(annotJsonArray)
	}

	fun putColumnsJsonArray(annotJsonArray: JSONArray) {
		rawJson.put(ANNOTATIONS, annotJsonArray)
	}

	fun toJSONObject(): JSONObject {
		return rawJson
	}

	override fun toString(): String {
		return rawJson.toString()
	}
}

class JSONAnnotation {

	companion object {
		const val NAME = "name"
	}

	private var rawJson = JSONObject()

	var name: String
		get() = rawJson.getString(NAME)
		set(value) {
			rawJson.put(NAME, value)
		}

	fun toJSONObject(): JSONObject {
		return rawJson
	}

	override fun toString(): String {
		return rawJson.toString()
	}
}