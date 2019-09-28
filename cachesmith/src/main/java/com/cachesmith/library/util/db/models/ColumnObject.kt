package com.cachesmith.library.util.db.models

import com.cachesmith.library.util.DataType
import com.cachesmith.library.util.ObjectClass
import com.cachesmith.library.annotations.PrimaryKey
import com.cachesmith.library.util.ObjectField
import com.cachesmith.library.annotations.Column
import com.cachesmith.library.annotations.Unique
import com.cachesmith.library.annotations.AutoIncrement
import com.cachesmith.library.annotations.NotNullable
import com.cachesmith.library.annotations.Relationship
import com.cachesmith.library.util.RelationType
import com.cachesmith.library.util.db.internal.DatabaseUtils

internal class ColumnObject @JvmOverloads constructor (var name: String = "") {
	
	constructor (field: ObjectField) : this(field.columnName) {
		field.annotations.forEach { annotation ->
			when(annotation) {
				is Column -> {
					if (annotation.type != DataType.NONE) {
						typeName = annotation.type.value
					} else {
						typeClass = field.type.clazz
					}
				}
				is PrimaryKey -> isPrimaryKey =  true
				is Unique -> isUnique = true
				is AutoIncrement -> isAutoIncrement = true
				is NotNullable -> isNotNull = true
				is Relationship -> {
					if (!annotation.query.isBlank()) {
						foreignKeyQuery = annotation.query
					} else {
						if (annotation.type == RelationType.ONE_TO_ONE
								|| annotation.type == RelationType.MANY_TO_ONE) {
							foreignKey = DatabaseUtils.getForeignKeyObject(annotation, field.type.clazz)
						}
						else if (annotation.type == RelationType.MANY_TO_MANY) {
//							createRelationalTable(db, field.type.clazz)
						}
					}
				}
			}
		}
	}

	companion object {
		@JvmStatic
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
}