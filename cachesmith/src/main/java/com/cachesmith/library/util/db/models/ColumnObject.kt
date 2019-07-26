package com.cachesmith.library.util.db.models

import com.cachesmith.library.util.DataType
import com.cachesmith.library.util.ObjectClass

class ColumnObject @JvmOverloads constructor (var name: String = "") {
	
	var isUnique: Boolean = false
	var isPrimaryKey: Boolean = false
	var isAutoIncrement: Boolean = false
	var isNotNull: Boolean = false
	
	var typeClass: ObjectClass? = null
	var typeName: String = DataType.NONE.value
		get() {
			if (!DataType.NONE.value.equals(field)) {
				return field
			}
			return getTypeByClass(typeClass).value			
		}
	
	var foreignKey: ForeignKeyObject = ForeignKeyObject()
	val isForeignKey: Boolean
		get() {
			if (!foreignKey.isEmpty || !foreignKeyQuery.isBlank())
				return true
			return false
		}
	
	var foreignKeyQuery: String = ""
	
	fun getTypeByClass(typeClass: ObjectClass?): DataType {
		if (typeClass != null) {
			when {
                typeClass.simpleName.contains("int", true) -> return DataType.INTEGER
                typeClass.simpleName.contains("long", true) -> return DataType.INTEGER
                typeClass.simpleName.contains("double", true) -> return DataType.REAL
                typeClass.simpleName.contains("float", true) -> return DataType.REAL
                typeClass.simpleName.contains("string", true) -> return DataType.TEXT
                typeClass.simpleName.contains("char", true) -> return DataType.TEXT
                typeClass.simpleName.contains("byte", true) -> return DataType.BLOB
            }
		}
		return DataType.NONE
	}
}