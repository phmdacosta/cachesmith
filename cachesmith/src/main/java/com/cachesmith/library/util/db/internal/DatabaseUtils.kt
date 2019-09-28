package com.cachesmith.library.util.db.internal

import com.cachesmith.library.annotations.Relationship
import com.cachesmith.library.util.ObjectClass
import com.cachesmith.library.util.db.models.ForeignKeyObject
import com.cachesmith.library.annotations.PrimaryKey
import com.cachesmith.library.util.db.models.ColumnObject
import com.cachesmith.library.annotations.Column
import com.cachesmith.library.util.DataType

internal object DatabaseUtils {
	fun getRelationalTableName(firstTableName: String, secondTableName: String): String {
		val prefxTable = "rel_"
		val firstName = firstTableName.substring(0, 4)
		val secondName = secondTableName.substring(0, 4)
		return prefxTable.plus(firstName).plus("_").plus(secondName)
	}
	
	fun getColumnsForRelationalTable(parent: ObjectClass): ColumnObject {
		val columnObj = ColumnObject()
		
		parent.fields.forEach { field ->
			field.annotations.forEach {annotation ->
				when(annotation) {
					is PrimaryKey -> {
						columnObj.isNotNull = true
						columnObj.name = field.columnName
						columnObj.typeClass = field.type.clazz
						val columnAnnot = (field.annotations.find { it is Column })?.let { it as Column }
						if (columnAnnot != null && columnAnnot.type != DataType.NONE) {
							columnObj.typeName = columnAnnot.type.value
						}
					}
					is Relationship -> {
						val typeClass = field.type.clazz
						val foreignKeyObj = getForeignKeyObject(annotation, typeClass)
						columnObj.foreignKey = foreignKeyObj
					}
				}
			}
		}
		
		return columnObj
	}
	
	fun getForeignKeyObject(annotation: Relationship, target: ObjectClass): ForeignKeyObject {
		val foreignKey = ForeignKeyObject(annotation.targetTable, annotation.targetColumn, annotation.targetColumnType)
		foreignKey.onDeleteAction = annotation.onDelete
		foreignKey.onUpdateAction = annotation.onUpdate
			
		if (foreignKey.referenceTable.isBlank()) {
			foreignKey.referenceTable = target.tableName
		}
		
		if (foreignKey.referenceColumn.isBlank()) {
			target.fields.forEach { field ->
				field.annotations.forEach { annot ->
					 when(annot) {
						 is PrimaryKey -> {
							 foreignKey.referenceColumn = field.columnName
							 foreignKey.referenceColumnType = ColumnObject.getTypeByClass(field.type.clazz).value
						 }
					 }
				}
			}
		}
		return foreignKey
	}
}