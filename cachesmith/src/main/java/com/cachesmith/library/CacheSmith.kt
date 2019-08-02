package com.cachesmith.library

import android.content.Context
import kotlin.reflect.KClass

/**
 * CacheSmith is a ORM library that abstracts and adapts Android SQLite implementation
 * by using annotations to mapping classes to SQLite relation database.
 * To start to use the library create a instance by calling the method/function [create].
 * By the instance just call [load] passing your DataSource class. This will return a
 * DataSource's instance with table created and updated in database, ready to execute
 * commands.
 * <p>
 * <pre><code>
 * <b><i>Kotlin</i></b>
 * val dataSource = CacheSmith.create().load(DataSource::class)
 * 
 * <b><i>Java</i></b>
 * DataSource dataSource = CacheSmith.create().load(DataSource.class)
 * </code></pre>
 *
 * @author Pedro da Costa
 */
interface CacheSmith {

	/**
	 * Loads a DataSource object to access database.
	 *
	 * @param T type of a data source, it's a DataSource subclass
	 * @property Class<T> java class of data source
	 * @return the datasource object
	 */
    fun <T : DataSource> load(dataSource: Class<T>): T
	
	/**
	 * Loads a DataSource object to access database.
	 *
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
	 *
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
	 * @return version number
	 */
    fun getVersion(): Int
	
	/**
	 * Defines the database name.
	 * <p>If no name is setting, library will create database with a default name.
	 *
	 * @property String database name
	 */
    fun setDatabaseName(name: String)
	
	/**
	 * Returns the database name that was defined by user.
	 * <p>It does not return default name defined by library.
	 *
	 * @return database name
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