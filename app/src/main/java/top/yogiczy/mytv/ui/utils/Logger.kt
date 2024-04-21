package top.yogiczy.mytv.ui.utils

import android.util.Log


class Logger private constructor(
    private val tag: String? = null,
) {

    fun d(message: String, throwable: Throwable? = null) {
        Log.d(tag, message, throwable)
    }

    fun i(message: String, throwable: Throwable? = null) {
        Log.i(tag, message, throwable)
    }

    fun w(message: String, throwable: Throwable? = null) {
        Log.w(tag, message, throwable)
    }

    fun e(message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
    }

    fun wtf(message: String, throwable: Throwable? = null) {
        Log.wtf(tag, message, throwable)
    }

    companion object {
        fun create(tag: String?) = Logger(tag)
    }
}

abstract class Loggable(private val tag: String? = null) {
    protected val log: Logger
        get() = Logger.create("[L]${tag ?: javaClass.simpleName}")
}