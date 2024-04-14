package top.yogiczy.mytv.ui.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * 应用配置存储
 */
object SP {
    const val SP_NAME = "mytv"
    const val SP_MODE = Context.MODE_PRIVATE
    private lateinit var sp: SharedPreferences
    fun init(context: Context) {
        sp = context.getSharedPreferences(SP_NAME, SP_MODE)
    }

    enum class KEY {
        /** 开机自启 */
        APP_BOOT_LAUNCH,

        /** 显示fps */
        DEBUG_SHOW_FPS,

        /** 上一次直播源序号 */
        IPTV_LAST_IPTV_IDX,

        /** 换台反转 */
        IPTV_CHANNEL_CHANGE_FLIP,

        /** 直播源精简 */
        IPTV_SOURCE_SIMPLIFY,

        /** 直播源缓存时间 */
        IPTV_SOURCE_CACHE_TIME,

        /** 自定义直播源 */
        IPTV_CUSTOM_SOURCE,

        /** 启用epg */
        EPG_ENABLE,

        /** epg缓存时间 */
        EPG_XLM_CACHE_TIME,

        /** epg解析缓存hash */
        EPG_CACHE_HASH,
    }

    var appBootLaunch: Boolean
        get() = sp.getBoolean(KEY.APP_BOOT_LAUNCH.name, false)
        set(value) = sp.edit().putBoolean(KEY.APP_BOOT_LAUNCH.name, value).apply()

    var debugShowFps: Boolean
        get() = sp.getBoolean(KEY.DEBUG_SHOW_FPS.name, false)
        set(value) = sp.edit().putBoolean(KEY.DEBUG_SHOW_FPS.name, value).apply()

    var iptvLastIptvIdx: Int
        get() = sp.getInt(KEY.IPTV_LAST_IPTV_IDX.name, 0)
        set(value) = sp.edit().putInt(KEY.IPTV_LAST_IPTV_IDX.name, value).apply()

    var iptvChannelChangeFlip: Boolean
        get() = sp.getBoolean(KEY.IPTV_CHANNEL_CHANGE_FLIP.name, false)
        set(value) = sp.edit().putBoolean(KEY.IPTV_CHANNEL_CHANGE_FLIP.name, value).apply()

    var iptvSourceSimplify: Boolean
        get() = sp.getBoolean(KEY.IPTV_SOURCE_SIMPLIFY.name, false)
        set(value) = sp.edit().putBoolean(KEY.IPTV_SOURCE_SIMPLIFY.name, value).apply()

    var iptvSourceCacheTime: Long
        get() = sp.getLong(KEY.IPTV_SOURCE_CACHE_TIME.name, 0)
        set(value) = sp.edit().putLong(KEY.IPTV_SOURCE_CACHE_TIME.name, value).apply()

    var iptvCustomSource: String
        get() = sp.getString(KEY.IPTV_CUSTOM_SOURCE.name, "") ?: ""
        set(value) = sp.edit().putString(KEY.IPTV_CUSTOM_SOURCE.name, value).apply()

    var epgEnable: Boolean
        get() = sp.getBoolean(KEY.EPG_ENABLE.name, true)
        set(value) = sp.edit().putBoolean(KEY.EPG_ENABLE.name, value).apply()

    var epgXmlCacheTime: Long
        get() = sp.getLong(KEY.EPG_XLM_CACHE_TIME.name, 0)
        set(value) = sp.edit().putLong(KEY.EPG_XLM_CACHE_TIME.name, value).apply()

    var epgCacheHash: Int
        get() = sp.getInt(KEY.EPG_CACHE_HASH.name, 0)
        set(value) = sp.edit().putInt(KEY.EPG_CACHE_HASH.name, value).apply()
}