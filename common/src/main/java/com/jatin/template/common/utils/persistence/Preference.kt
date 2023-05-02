package com.jatin.template.common.utils.persistence

import android.content.Context
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Preference<T>(val name: String, val default: T) : ReadWriteProperty<Any?, T> {

    companion object {
        private lateinit var context: Context
        private lateinit var persistence: BasePersistence

        private var fileName = "default"

        fun init(context: Context, fileName: String) {
            persistence = PersistenceUtils.initialize(context)
            Companion.context = context.applicationContext
            Companion.fileName = fileName
        }
    }


    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(name, default!!)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(name, value)
    }

    /**
     * Find data and return a specific object to the calling method
     * If the type cannot be found, use the deserialization method to return the type
     * default is the default object to prevent exceptions that return empty objects
     * That is, if the name does not find the value,
     * the default serialized object will be returned, and then returned after deserialization
     */
    private fun <A> findPreference(name: String, default: A): A {
        return persistence.findPreference(name, default)
    }

    private fun <A> putPreference(name: String, value: A) {
        return persistence.putPreference(name, value)
    }

    /**
     * delete all data
     */
    fun clearPreference() {
        persistence.clearPreference()
    }

    /**
     * Delete stored data according to key
     */
    fun clearPreference(key: String) {
        persistence.clearPreference(key)
    }

}
