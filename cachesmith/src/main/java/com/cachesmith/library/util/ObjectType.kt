package com.cachesmith.library.util

import java.lang.reflect.Type
import kotlin.reflect.KType
import kotlin.reflect.KClass

class ObjectType private constructor() {
	
	constructor(type: Type) : this() {
		java = type
	}
	
	constructor(type: KType) : this() {
		kotlin = type
	}
	
	var java: Type? = null
	var kotlin: KType? = null
	
	val isJava: Boolean
		get() = java != null
	
	val isKotlin: Boolean
		get() = kotlin != null
	
	val name: String
		get() {
			if (isJava) {
				return java!!.getTypeName()
			} else {
				if (kotlin!!.classifier!! is KClass<*>) {
					return (kotlin!!.classifier!! as KClass<*>).simpleName!!
				}
				return ""
			}
		}
	
	val clazz: ObjectClass
		get() {
			if (isJava) {
				return ObjectClass(java!!.javaClass)
			} else {
				if (kotlin!!.classifier!! is KClass<*>) {
					return ObjectClass(kotlin!!.classifier!! as KClass<*>)
				} else {
					throw ClassNotFoundException(
						String.format("Could not get type class of %s property",
							this.name))
				}
			}
		}
}