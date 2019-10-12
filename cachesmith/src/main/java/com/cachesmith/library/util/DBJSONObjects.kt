package com.cachesmith.library.util

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class JSONTable() {

	companion object {
		const val NAME = "name"
		const val QUANTITY = "quantity"
		const val COLUMNS = "columns"
	}

	private var rawJson = JSONObject()
	private var columnsJsonArray = JSONArray()

	constructor(json: JSONObject) : this() {
		rawJson = json
	}
	
	constructor(json: String) : this() {
		if (json.isBlank())
			rawJson = JSONObject()
		else
			rawJson = JSONObject(json)
	}
	
	var name: String
		get() {
			var result = ""
			try {
				result = rawJson.getString(NAME)
			} catch (e: JSONException) {}
			return result
		}
		set(value) {
			rawJson.put(NAME, value)
		}
	
	var columnQuantity: Int
		get() {
			var result = 0
			try {
				result = rawJson.getInt(QUANTITY)
			} catch (e: JSONException) {}
			return result
		}
		set(value) {
			rawJson.put(QUANTITY, value)
		}

	fun isEmpty(): Boolean {
		return rawJson.isNull(NAME)
	}
	
	fun addColumnJson(jsonColumn: JSONColumn) {
		columnsJsonArray.put(jsonColumn.toJSONObject())
		columnQuantity++
		putColumnsJsonArray(columnsJsonArray)
	}
	
	fun putColumnsJsonArray(columnsJsonArray: JSONArray) {
		rawJson.put(COLUMNS, columnsJsonArray)
	}

	fun listJsonColumns(): Array<JSONColumn> {
		val listJsonColumns = mutableListOf<JSONColumn>()
		if (columnsJsonArray.length() <= 0)
			columnsJsonArray = rawJson.getJSONArray(COLUMNS)

		for (i in 0 until columnsJsonArray.length()) {
			listJsonColumns.add(JSONColumn(columnsJsonArray.getJSONObject(i)))
		}
		return listJsonColumns.toTypedArray()
	}

	fun containsColumn(columnName: String): Boolean {
		if (columnsJsonArray.length() <= 0)
			columnsJsonArray = rawJson.getJSONArray(COLUMNS)
		for (i in 0 until columnsJsonArray.length()) {
			val jsonColumn = JSONColumn(columnsJsonArray.getJSONObject(i))
			if (jsonColumn.name == columnName) {
				return true
			}
		}
		return false
	}

	override fun toString(): String {
		return rawJson.toString()
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) {
			return true
		}

		if (other !is JSONTable) {
			return false
		}

		if (this.name != other.name
				|| this.columnQuantity != other.columnQuantity) {
			return false
		}

		val listColumns = listJsonColumns()
		val otherListColumns = other.listJsonColumns()
		listColumns.forEach {
			if (!otherListColumns.contains(it)) {
				return false
			}
		}

		return true
	}
}

class JSONColumn() {

	companion object {
		const val NAME = "name"
		const val FIELD = "field"
		const val TYPE = "type"
		const val ANNOTATIONS = "annotations"
	}

	constructor(json: JSONObject) : this() {
		rawJson = json
	}
	
	constructor(json: String) : this(JSONObject(json))

	private var rawJson = JSONObject()
	private var annotJsonArray = JSONArray()

	var name: String
		get() {
			var result = ""
			try {
				result = rawJson.getString(NAME)
			} catch (e: JSONException) {}
			return result
		}
		set(value) {
			rawJson.put(NAME, value)
		}

	var field: String
		get() {
			var result = ""
			try {
				result = rawJson.getString(FIELD)
			} catch (e: JSONException) {}
			return result
		}
		set(value) {
			rawJson.put(FIELD, value)
		}

	var type: String
		get() {
			var result = ""
			try {
				result = rawJson.getString(TYPE)
			} catch (e: JSONException) {}
			return result
		}
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

	fun listAnnotationsJson(): Array<JSONAnnotation> {
		val listJsonAnnots = mutableListOf<JSONAnnotation>()
		if (annotJsonArray.length() <= 0)
			annotJsonArray = rawJson.getJSONArray(ANNOTATIONS)

		for (i in 0 until annotJsonArray.length()) {
			listJsonAnnots.add(JSONAnnotation(annotJsonArray.getJSONObject(i)))
		}
		return listJsonAnnots.toTypedArray()
	}

	fun toJSONObject(): JSONObject {
		return rawJson
	}

	override fun toString(): String {
		return rawJson.toString()
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) {
			return true
		}

		if (other !is JSONColumn) {
			return false
		}

		if (this.name != other.name
				|| this.field != other.field
				|| this.type != other.type) {
			return false
		}

		val listAnnotations = listAnnotationsJson()
		val otherListAnnots = other.listAnnotationsJson()
		listAnnotations.forEach {
			if (!otherListAnnots.contains(it)) {
				return false
			}
		}

		return true
	}
}

class JSONAnnotation() {

	companion object {
		const val NAME = "name"
	}

	constructor(json: JSONObject) : this() {
		rawJson = json
	}
	
	constructor(json: String) : this(JSONObject(json))

	private var rawJson = JSONObject()

	var name: String
		get() {
			var result = ""
			try {
				result = rawJson.getString(NAME)
			} catch (e: JSONException) {}
			return result
		}
		set(value) {
			rawJson.put(NAME, value)
		}

	fun toJSONObject(): JSONObject {
		return rawJson
	}

	override fun toString(): String {
		return rawJson.toString()
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) {
			return true
		}

		if (other !is JSONAnnotation) {
			return false
		}

		if (this.name != other.name) {
			return false
		}

		return true
	}
}