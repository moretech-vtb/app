package more.tech.app.core.util

import android.content.SharedPreferences

inline fun <reified T> SharedPreferences.get(key: String): T? {
    return when(T::class) {
        String::class -> getString(key, null) as T
        Integer::class -> getInt(key, 0) as T
        java.lang.Boolean::class -> getBoolean(key, false) as T
        java.lang.Float::class -> getFloat(key, 0.0F) as T
        else -> null
    }
}

inline fun <reified T> SharedPreferences.put(key: String, value: T?) {
    val editor = edit()
    when(T::class) {
        String::class -> editor.putString(key, value as String)
        Integer::class -> editor.putInt(key, value as Int)
        java.lang.Boolean::class -> editor.putBoolean(key, value as Boolean)
        java.lang.Float::class -> editor.putFloat(key, value as Float)
        else -> return
    }
    editor.apply()
}

fun SharedPreferences.clear() {
    edit().clear().apply()
}