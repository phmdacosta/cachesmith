package com.cachesmith.library

import android.content.Context
import kotlin.reflect.KClass

/**
 * CacheSmith is a ORM library that abstracts and adapts Android SQLite implementation
 * by using annotations to mapping classes to SQLite relation database.
 * To start to use the library create a instance by calling the method/function [build]
 * passing the Android [Context] object.
 * <p>By the instance just call [load] passing your DataSource class. This will return a
 * DataSource's instance with table created and updated in database, ready to execute
 * commands.
 * <p>
 * <pre><code>
 * <p><b><i>Kotlin</i></b>
 * <p>val dataSource = CacheSmith.Builder.build(context).load(DataSource::class)
 * <p>
 * <p><b><i>Java</i></b>
 * <p>DataSource dataSource = CacheSmith.build(context).load(DataSource.class)
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
	 * @property KClass<T> kotlin class of data source
	 * @return the datasource object
	 */
	@JvmSynthetic
    fun <T : DataSource> load(dataSource: KClass<T>): T

	/**
	 * Defines if manual version will be used.
	 * <p>
	 * By default it is disabled and <b>CacheSmith</b> uses a automated version control,
	 * to have more control of database version active this flag and use functions
	 * [setVersion] and [getVersion] to define and access your own version.
	 * <p>
	 * The use of manual version requires to set mapped models in library using function 
	 * [addModel] or [addAllModels] passing de model class.
	 * <p>
	 * The list of mapped models can be used with automated version control solution
	 * and it isn't limited to manual version.
	 *
	 * @property Boolean boolean value that defines if manual version are activated or not.
	 */
	fun setManualVersion(value: Boolean)
	
	/**
	 * Returns if manual version defition is activated.
	 * <p>
	 * By default it is disabled and <b>CacheSmith</b> uses a automated version control,
	 * to have more control of database version active this flag and use functions
	 * [setVersion] and [getVersion] to define and access your own version.
	 * <p>
	 * The use of manual version requires to set mapped models in library using function 
	 * [addModel] or [addAllModels] passing de model class.
	 * <p>
	 * The list of mapped models can be used with automated version control solution
	 * and it isn't limited to manual version.
	 *
	 * @return Boolean boolean value that defines if manual version are activated or not.
	 */
	fun isManualVersionDefined(): Boolean
	
	/**
	 * Defines the version of database. SQLite needs a version to control database modifications,
	 * every time a new version is set, it runs a update function that updates that applies
	 * changes made in table's structs.
	 * <p>
	 * If the defined version is the same as old one, nothing will be done.
	 *
	 * @property Int new version
	 */
    fun setVersion(version: Int)
	
	/**
	 * Returns the version defined for database.
	 * <p>
	 * SQLite needs a version to control database modifications,
	 * every time a new version is set, it runs a update function that updates that applies
	 * changes made in table's structs.
	 * <p>
	 * If the defined version is the same as old one, nothing will be done.
	 *
	 * @return version number
	 */
    fun getVersion(): Int
	
	/**
	 * Defines the database name.
	 * <p>
	 * If no name is setting, library will create database with a default name.
	 *
	 * @property String database name
	 */
    fun setDatabaseName(name: String)
	
	/**
	 * Returns the database name that was defined by user.
	 *
	 * @return database name
	 */
    fun getDatabaseName(): String

	/**
	 * Add a mapped model to a list that will be used to create tables on call
	 * of [initDatabase] function.
	 * <p>
	 * If no model is setted, the [initDatabase] function will not create a
	 * single table. On the other hand, the table of the model will be created  
	 * when it loads its [DataSource] object.
	 * <p>
	 * In manual verison mode mapped models definition is required to create
	 * their tables in advance.
	 * <p>
	 * The automated version control process manages the version to enable
	 * new creations possibility during library usage. 
	 *
	 * @property Class java class of model
	 */
	fun addModel(model: Class<*>)
	
	/**
	 * Add a mapped model to a list that will be used to create tables on call
	 * of [initDatabase] function.
	 * <p>
	 * If no model is setted, the [initDatabase] function will not create a
	 * single table. On the other hand, the table of the model will be created  
	 * when it loads its [DataSource] object.
	 * <p>
	 * In manual verison mode mapped models definition is required to create
	 * their tables in advance.
	 * <p>
	 * The automated version control process manages the version to enable
	 * new creations possibility during library usage
	 *
	 * @property KClass kotlin class of model
	 */
	@JvmSynthetic
	fun addModel(model: KClass<*>)
	
	/**
	 * Defines the list of mapped models that will be used to create tables on
	 * call of [initDatabase] function.
	 * <p>
	 * If no model is setted, the [initDatabase] function will not create a
	 * single table. On the other hand, the table of the model will be created  
	 * when it loads its [DataSource] object.
	 * <p>
	 * In manual verison mode mapped models definition is required to create
	 * their tables in advance.
	 * <p>
	 * The automated version control process manages the version to enable
	 * new creations possibility during library usage
	 *
	 * @property List<Class> list of java classes of models
	 */
	fun addAllModels(models: List<Class<*>>)
	
	/**
	 * Initiate tables creation in Database.
	 * <p>
	 * If no model is setted, the [initDatabase] function will not create a
	 * single table. On the other hand, the table of the model will be created  
	 * when it loads its [DataSource] object.
	 * <p>
	 * In manual verison mode mapped models definition is required to create
	 * their tables in advance.
	 * <p>
	 * The automated version control process manages the version to enable
	 * new creations possibility during library usage
	 */
    fun initDatabase()

    companion object Builder {
        @Volatile private var instance: CacheSmith? = null

		/**
		 * Builds the class instance.
		 * @property Context context of the application 
		 * @return class instance. 
		 */
        fun build(context: Context): CacheSmith {
            instance ?: synchronized(this) {
                return CacheSmithBuilder.build(context)
            }
            return instance!!
        }
    }
}