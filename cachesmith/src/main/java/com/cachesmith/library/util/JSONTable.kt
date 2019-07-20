package com.cachesmith.library.util

import org.json.JSONArray
import org.json.JSONObject

class JSONTable(): JSONObject() {
	
	var columnsJsonArray = JSONArray()
	
	companion object {
		const val NAME = "name"
		const val QUANTITY = "quantity"
		const val COLUMNS = "columns"
	}
	
	var name: String
		get() = getString(NAME)
		set(value) {
			put(NAME, value)
		}
	
	var columnQuantity: Int
		get() = getInt(QUANTITY)
		set(value) {
			put(QUANTITY, value)
		}
	
	fun addColumnJson(jsonColumn: JSONObject) {
		columnsJsonArray.put(jsonColumn)
		putColumnsJsonArray(columnsJsonArray)
	}
	
	fun addColumnJson(jsonColumn: JSONColumn) {
		columnsJsonArray.put(jsonColumn)
		columnQuantity++
		putColumnsJsonArray(columnsJsonArray)
	}
	
	fun putColumnsJsonArray(columnsJsonArray: JSONArray) {
		put(COLUMNS, columnsJsonArray)
	}
}