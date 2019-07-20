package com.cachesmith.library.util

import org.json.JSONArray
import org.json.JSONObject

class JSONAnnotation(): JSONObject() {
	
	companion object {
		const val NAME = "name"
	}
	
	var name: String
		get() = getString(NAME)
		set(value) {
			put(NAME, value)
		}
}