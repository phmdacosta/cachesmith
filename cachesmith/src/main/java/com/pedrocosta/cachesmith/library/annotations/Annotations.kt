package com.pedrocosta.cachesmith.library.annotations

enum class RelationTypes {
    ONE_TO_ONE, ONE_TO_MANY, MANY_TO_MANY
}

/**
 * Defines the entity model of a data source.
 *
 * @property value class name of model for reflection. Ex: <package-name>.Model
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Entity(val value: String)

/**
 * Defines the name of a table in data base.
 *
 * @property value the table name
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Table(val name: String)

/**
 * Defines the name of a field of a table in data base.
 *
 * @property value the field name
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Field(val name: String = "")

/**
 * Defines if field is a primary key of the entity.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class PrimaryKey

/**
 * Defines if field is a foreign key of the entity.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class ForeignKey

/**
 * Defines if field has a auto incremented value. If field is not numeric it throws a exception.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class AutoIncrement

/**
 * Defines if field value is unique.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Unique

/**
 * Defines if field can not be null.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class NotNullable

/**
 * Defines the relationship between entities.
 *
 * To defines the type of relationship, use the following elements: [ONE_TO_ONE], [ONE_TO_MANY], [MANY_TO_MANY]
 *
 * @property type the type of relationship.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
@MustBeDocumented
annotation class Relationship(val type: RelationTypes)