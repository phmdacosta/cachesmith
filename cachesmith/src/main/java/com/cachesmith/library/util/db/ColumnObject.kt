package com.cachesmith.library.util

class ColumnObject() {
	
	constructor (name: String) : this() {
		this.name = name
	}
	
	var name: String = ""
	var isUnique: Boolean = false
	var isPrimaryKey: Boolean = false
	var isAutoIncrement: Boolean = false
	var isNotNull: Boolean = false
	
	var typeClass: Class<*>? = null
	var typeName: String = DataType.NONE.value
		get() {
			if (!DataType.NONE.value.equals(field)) {
				return field
			}
			return getTypeByClass(typeClass).value			
		}
		set(value) {
			field = value
		}
	
	var foreignKey: ForeignKeyObject = ForeignKeyObject()
	val isForeignKey: Boolean
		get() {
			if (!foreignKey.isEmpty || !foreignKeyQuery.isBlank())
				return true
			return false
		}
	
	var foreignKeyQuery: String = ""
	
	fun getTypeByClass(typeClass: Class<*>?): DataType {
		if (typeClass != null) {
			when {
                typeClass.name.contains("int", true) -> return DataType.INTEGER
                typeClass.name.contains("long", true) -> return DataType.INTEGER
                typeClass.name.contains("double", true) -> return DataType.REAL
                typeClass.name.contains("float", true) -> return DataType.REAL
                typeClass.name.contains("string", true) -> return DataType.TEXT
                typeClass.name.contains("char", true) -> return DataType.TEXT
                typeClass.name.contains("byte", true) -> return DataType.BLOB
            }
		}
		return DataType.NONE
	}
}