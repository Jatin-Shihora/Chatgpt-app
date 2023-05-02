package com.jatin.template.common.utils.persistence

import android.os.Parcelable
import com.tencent.mmkv.MMKV
import java.util.*

class MMKVPersistence : BasePersistence {
    val mmkv: MMKV? by lazy { MMKV.defaultMMKV() }

    fun put(key: String, value: Any?): Boolean {
        return when (value) {
            is String -> mmkv?.encode(key, value)!!
            is Float -> mmkv?.encode(key, value)!!
            is Boolean -> mmkv?.encode(key, value)!!
            is Int -> mmkv?.encode(key, value)!!
            is Long -> mmkv?.encode(key, value)!!
            is Double -> mmkv?.encode(key, value)!!
            is ByteArray -> mmkv?.encode(key, value)!!
            else -> false
        }
    }

    /**
     * Find data and return a specific object to the calling method
     * If the type cannot be found, use the deserialization method to return the type
     * default is the default object to prevent exceptions that return empty objects
     * That is, if the name does not find the value,
     * the default serialized object will be returned, and then returned after deserialization
     */
    override fun <A> findPreference(name: String, default: A): A = with(mmkv!!) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> getString(name, serialize(default))?.let(this@MMKVPersistence::deSerialization)
        }!!
        res as A
    }

    override fun <A> putPreference(name: String, value: A) = with(mmkv!!) {
        var a = when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> putString(name, serialize(value))
        }.commit()
    }


    override fun clearPreference() {
        mmkv?.edit()?.clear()?.commit()

    }

    override fun clearPreference(key: String) {
        mmkv?.edit()?.remove(key)?.commit()
    }

    /**
     * The Parcelable serialization that comes with Android is used here, which has better serialization performance than the Serializer supported by java.
     */
    fun <T : Parcelable> put(key: String, t: T?): Boolean {
        if (t == null) {
            return false
        }
        return mmkv?.encode(key, t)!!
    }

    fun put(key: String, sets: Set<String>?): Boolean {
        if (sets == null) {
            return false
        }
        return mmkv?.encode(key, sets)!!
    }


    fun getInt(key: String): Int? {
        return mmkv?.decodeInt(key, 0)
    }

    fun getDouble(key: String): Double? {
        return mmkv?.decodeDouble(key, 0.00)
    }

    fun getLong(key: String): Long? {
        return mmkv?.decodeLong(key, 0L)
    }

    fun getBoolean(key: String): Boolean? {
        return mmkv?.decodeBool(key, false)
    }

    fun getFloat(key: String): Float? {
        return mmkv?.decodeFloat(key, 0F)
    }

    fun getByteArray(key: String): ByteArray? {
        return mmkv?.decodeBytes(key)
    }

    fun getString(key: String): String? {
        return mmkv?.decodeString(key, "")
    }

    inline fun <reified T : Parcelable> getParcelable(key: String): T? {
        return mmkv?.decodeParcelable(key, T::class.java)
    }

    fun getStringSet(key: String): Set<String>? {
        return mmkv?.decodeStringSet(key, Collections.emptySet())
    }

    fun removeKey(key: String) {
        mmkv?.removeValueForKey(key)
    }

    fun clearAll() {
        mmkv?.clearAll()
    }

}