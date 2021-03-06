package com.cachesmith.library

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.cachesmith.library.annotations.Entity
import com.cachesmith.library.util.PreferencesManager
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import com.cachesmith.library.util.ObjectClass
import kotlin.reflect.jvm.internal.impl.resolve.jvm.JvmClassName

internal class CacheSmithBuilder private constructor(val context: Context) : CacheSmith {

    companion object {
        fun build(context: Context): CacheSmith {
            return CacheSmithBuilder(context)
        }
    }

    override fun <T : DataSource> load(dataSource: Class<T>): T {
		val models = mutableListOf<ObjectClass>()
        dataSource.annotations.forEach {
            if (it is Entity) {
                try {
                    val model = ObjectClass(Class.forName(it.value))
					models.add(model)
                } catch (e: ClassNotFoundException) {
                    models.addAll(getModelList())
                    handleClassNotFoundException(e, it.value, models)
                }
            }
        }
        val helper = CacheSmithHelper.create(context, models)
        return dataSource.getConstructor(SQLiteOpenHelper::class.java).newInstance(helper) as T
    }

    @JvmSynthetic
    override fun <T : DataSource> load(dataSource: KClass<T>): T {
		val models = mutableListOf<ObjectClass>()
        dataSource.annotations.forEach {
            if (it is Entity) {
                try {
                    val model = ObjectClass(Class.forName(it.value).kotlin)
					models.add(model)
                } catch (e: ClassNotFoundException) {
                    models.addAll(getModelList())
                    handleClassNotFoundException(e, it.value, models)
                }
            }
        }
        val helper = CacheSmithHelper.create(context, models)
        return dataSource.primaryConstructor?.call(helper) as T
    }
	
	override fun initDatabase() {
		CacheSmithHelper.create(context, getModelList())
    }

    override fun setManualVersion(value: Boolean) {
        PreferencesManager.saveManualVersionCheck(context, value)
    }

    override fun isManualVersionDefined(): Boolean {
        return PreferencesManager.getManualVersionCheck(context)
    }
    
    override fun setVersion(version: Int) {
    	PreferencesManager.saveVersion(context, version)
    }

    override fun getVersion(): Int {
        return PreferencesManager.getVersion(context)
    }

    override fun setDatabaseName(name: String) {
        PreferencesManager.saveDatabaseName(context, name)
    }

    override fun getDatabaseName(): String {
        return PreferencesManager.getDatabaseName(context)
    }

    override fun addModel(model: Class<*>) {
        val savedModels = PreferencesManager.getModels(context)
        if (!savedModels.contains(model.name)) {
            savedModels.add(model.name)
            PreferencesManager.saveModels(context, savedModels)
        }
    }

    override fun addModel(model: KClass<*>) {
        val savedModels = PreferencesManager.getModels(context)
        if (!savedModels.contains(model.qualifiedName)) {
            savedModels.add(model.qualifiedName!!)
            PreferencesManager.saveModels(context, savedModels)
        }
    }

    override fun addAllModels(models: List<Class<*>>) {
        val listNames = mutableListOf<String>()
        models.forEach {
            listNames.add(it.name)
        }
        PreferencesManager.saveModels(context, listNames)
    }
	
	private fun getModelList(): List<ObjectClass> {
		val result = mutableListOf<ObjectClass>()
		val listModels = PreferencesManager.getModels(context)
		try {
			listModels.forEach {
				val model = Class.forName(it)
				result.add(ObjectClass(model))
			}
		} catch(e: ClassNotFoundException) {
			throw e
		}
		return result
	}

    private fun handleClassNotFoundException(e: ClassNotFoundException, className: String,
                                             models: MutableList<*>) {
        Log.e(context.getString(R.string.app_name),
                context.getString(R.string.error_load_model, className))
        // If there aren't any model, throws the exception
        if (models.isEmpty()) {
            throw e
        }
    }
}