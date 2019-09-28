package com.cachesmith.library.util

import kotlin.reflect.KClass
import java.lang.reflect.Field
import kotlin.reflect.KProperty
import com.cachesmith.library.annotations.Table

internal class ObjectClass private constructor() {
	
	constructor(clazz: Class<*>) : this() {
		javaClass = clazz
	}
	
	constructor(clazz: KClass<*>) : this() {
		kotlinClass = clazz
		if (kotlinClass!!.simpleName == null) {
			throw ClassNotFoundException("Wrong type of kotlin class")
		}
	}
	
	var javaClass: Class<*>? = null
	var kotlinClass: KClass<*>? = null
	var changed: Boolean = false
	
	val isJava: Boolean
		get() = javaClass != null
	
	val isKotlin: Boolean
		get() = kotlinClass != null
	
	val simpleName: String
		get() {
			if (isJava) {
				return javaClass!!.simpleName
			} else {
				return kotlinClass!!.simpleName!!
			}
		}
	
	val qualifiedName: String
		get() {
			if (isJava) {
				return javaClass!!.name
			} else {
				return kotlinClass!!.qualifiedName!!
			}
		}
	
	val fields: Array<ObjectField>
		get() {
			val listFields = mutableListOf<ObjectField>()
			if (isJava) {
				javaClass!!.declaredFields.forEach { field ->
					try {
						javaClass!!.getMethod("get".plus(field.name.capitalize()))
					} catch (e: Exception) {
						return@forEach
					}
					val generalField = ObjectField(field)
					listFields.add(generalField)
				}
			} else {
				kotlinClass!!.members.forEach { member ->
					if (member is KProperty) {
						val generalField = ObjectField(member)
						listFields.add(generalField)
					}
				}
			}
			return listFields.toTypedArray()
		}
	
	val tableName: String
		get() {
			var tableName: String
			if (isJava) {
				tableName = javaClass!!.simpleName
				val tableAnnot = (javaClass!!.annotations.find { it is Table })?.let { it as Table }
				if (tableAnnot!= null && !tableAnnot.name.isBlank()) {
					tableName = tableAnnot.name
				}
			} else {
				tableName = kotlinClass!!.simpleName!!
				val tableAnnot = (kotlinClass!!.annotations.find { it is Table })?.let { it as Table }
				if (tableAnnot!= null && !tableAnnot.name.isBlank()) {
					tableName = tableAnnot.name
				}
			}
			return tableName
		}
}