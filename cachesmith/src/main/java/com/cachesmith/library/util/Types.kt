package com.cachesmith.library.util

enum class RelationType {
    ONE_TO_ONE, ONE_TO_MANY, MANY_TO_ONE, MANY_TO_MANY;

	override fun toString(): String {
		return this.name
	}
}

enum class ActionType(val value: String) {
	NO_ACTION("NO ACTION"),
	RESTRICT("RESTRICT"),
	SET_NULL("SET NULL"),
	SET_DEFAULT("SET DEFAULT"),
	CASCADE("CASCADE");

	override fun toString(): String {
		return this.value
	}
}

enum class DataType(val value: String) {
	NONE(""),
	INT("INT"),
	INTEGER("INTEGER"),
	TINYINT("INT"),
	SMALLINT("INT"),
	MEDIUMINT("INT"),
	BIGINT("BIGINT"),
	UNSIGNED_BIG_INT("UNSIGNED BIG INT"),
	INT2("INT2"),
	INT8("INT8"),
	CHARACTER("CHARACTER(20)"),
	VARCHAR("VARCHAR(255)"),
	VARYING_CHARACTER("VARYING CHARACTER(255)"),
	NCHAR("NCHAR(55)"),
	NATIVE_CHARACTER("NATIVE CHARACTER(70)"),
	NVARCHAR("NVARCHAR(100)"),
	TEXT("TEXT"),
	CLOB("CLOB"),
	BLOB("BLOB"),
	REAL("REAL"),
	DOUBLE("DOUBLE"),
	DOUBLE_PRECISION("DOUBLE PRECISION"),
	FLOAT("FLOAT"),
	NUMERIC("NUMERIC"),
	DECIMAL("DECIMAL(10,5)"),
	BOOLEAN("BOOLEAN"),
	DATE("DATE"),
	DATETIME("DATETIME");

	override fun toString(): String {
		return this.value
	}
}

enum class JoinType {
	CROSS_JOIN,
	INNER_JOIN,
	OUTER_JOIN
}