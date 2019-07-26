package com.cachesmith.library

import android.content.Context
import kotlin.reflect.KClass

interface CacheSmith {

	/**
	 * Loads a DataSource object to access database.
	 * @param T type of a data source, it's a DataSource subclass
	 * @property Class<T> java class of data source
	 * @return the datasource object
	 */
    fun <T : DataSource> load(dataSource: Class<T>): T
	
	/**
	 * Loads a DataSource object to access database.
	 * @param T type of a data source, it's a DataSource subclass
	 * @property Class<T> kotlin class of data source
	 * @return the datasource object
	 */
    fun <T : DataSource> load(dataSource: KClass<T>): T
	
	/**
	 * Defines the version of database. SQLite needs a version to control database modifications,
	 * every time a new version is set, it runs a update function that updates that applies
	 * changes made in table's structs.
	 * <p>If the defined version is the same as old one, nothing will be done.
	 * @property Int new version
	 */
    fun setVersion(version: Int)
	
	/**
	 * Returns the version defined for database.
	 *
	 * <p>SQLite needs a version to control database modifications,
	 * every time a new version is set, it runs a update function that updates that applies
	 * changes made in table's structs.
	 * <p>If the defined version is the same as old one, nothing will be done.`
	 *
	 * @param T type of a data source, it's a DataSource subclass
	 * @property Class<T> kotlin class of data source
	 * @return the datasource object
	 */
    fun getVersion(): Int
	
	/**
	 * Defines the database name.
	 * <p>If no name is setting, library will create database with a default name.
	 * @param T type of a data source, it's a DataSource subclass
	 * @property Class<T> kotlin class of data source
	 * @return the datasource object
	 */
    fun setDatabaseName(name: String)
	
	/**
	 * Returns the database name that was defined by user.
	 * <p>It does not return default name defined by library.
	 * @param T type of a data source, it's a DataSource subclass
	 * @property Class<T> kotlin class of data source
	 * @return the datasource object
	 */
    fun getDatabaseName(): String

    companion object {
        @Volatile private var instance: CacheSmith? = null

        fun create(context: Context): CacheSmith {
            instance ?: synchronized(this) {
                return CacheSmithBuilder.build(context)
            }
            return instance!!
        }
    }
}