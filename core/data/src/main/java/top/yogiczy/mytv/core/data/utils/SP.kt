package top.yogiczy.mytv.core.data.utils

import android.content.Context
import android.content.SharedPreferences

object SP {
    private val log = Logger.create(javaClass.simpleName)
    private const val SP_NAME = "mytv-android"
    private const val SP_MODE = Context.MODE_PRIVATE
    private lateinit var sp: SharedPreferences

    fun getInstance(context: Context): SharedPreferences =
        context.getSharedPreferences(SP_NAME, SP_MODE)

    fun init(context: Context) {
        sp = getInstance(context)
    }

    private fun <T> safeGet(key: String, defValue: T, op: (key: String, defValue: T) -> T): T {
        try {
            return op(key, defValue)
        } catch (ex: Exception) {
            log.e("SP", ex)
            sp.edit().remove(key).apply()
            return defValue
        }
    }

    fun getString(key: String, defValue: String) = safeGet(key, defValue, sp::getString)!!
    fun putString(key: String, value: String) = sp.edit().putString(key, value).apply()

    fun getStringSet(key: String, defValue: Set<String>): Set<String> =
        safeGet(key, defValue, sp::getStringSet)!!

    fun putStringSet(key: String, value: Set<String>) = sp.edit().putStringSet(key, value).apply()

    fun getInt(key: String, defValue: Int) = safeGet(key, defValue, sp::getInt)
    fun putInt(key: String, value: Int) = sp.edit().putInt(key, value).apply()

    fun getLong(key: String, defValue: Long) = safeGet(key, defValue, sp::getLong)
    fun putLong(key: String, value: Long) = sp.edit().putLong(key, value).apply()

    fun getFloat(key: String, defValue: Float) = safeGet(key, defValue, sp::getFloat)
    fun putFloat(key: String, value: Float) = sp.edit().putFloat(key, value).apply()

    fun getBoolean(key: String, defValue: Boolean) = safeGet(key, defValue, sp::getBoolean)
    fun putBoolean(key: String, value: Boolean) = sp.edit().putBoolean(key, value).apply()

    fun clear() = sp.edit().clear().apply()
}