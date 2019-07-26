package com.cachesmith.library.util

import kotlin.reflect.KProperty
import java.lang.reflect.Field
import com.cachesmith.library.annotations.Column

class ObjectField private constructor() {
	
	constructor(field: Field) : this() {
		javaField = field
	}
	
	constructor(property: KProperty<*>) : this() {
		kotlinProperty = property
	}
	
	var javaField: Field? = null
	var kotlinProperty: KProperty<*>? = null
	
	val isJava: Boolean
		get() = javaField != null
	
	val isKotlin
		get() = kotlinProperty != null
	
	val name: String
		get() {
			if (isJava) {
				return javaField!!.name
			} else {
				return kotlinProperty!!.name
			}
		}
	
	val type: ObjectType
		get() {
			if (isJava) {
				return ObjectType(javaField!!.type)
			} else {
				return ObjectType(kotlinProperty!!.returnType)
			}
		}
	
	val annotations: Array<Annotation>
		get() {
			if (isJava) {
				return javaField!!.annotations
			} else {
				return kotlinProperty!!.annotations.toTypedArray()
			}
		}
	
	val columnName: String
		get() {
			var columnName = this.name
			val columnAnnot = (this.annotations.find { it is Column })?.let { it as Column }
			if (columnAnnot != null && !columnAnnot.name.isBlank()) {
				columnName = columnAnnot.name
			}
			return columnName
		}
}