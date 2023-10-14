package more.tech.app.core.data

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPrefsManager @Inject constructor(private val prefs: SharedPreferences) {

    companion object {
        const val LOGIN = "login"
        const val TOKEN = "token"
        const val LAST_FETCH_TIME = "last_fetch_time"

    }

    fun saveLogin(phone: String) {
        prefs.edit().apply {
            putString(LOGIN, phone)
        }.apply()
    }

    fun getLogin(): String? {
        return prefs.getString(LOGIN, "")
    }

    fun saveToken(token: String) {
        prefs.edit().apply {
            putString(TOKEN, token)
        }.apply()
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN, "")
    }
    fun saveLastFetchTime(value: String) {
        prefs.edit().apply {
            putString(LAST_FETCH_TIME, value)
        }.apply()
    }

    fun getLastFetchTime(): String {
        return prefs.getString(LAST_FETCH_TIME, "N/A") ?: "N/A"
    }

}