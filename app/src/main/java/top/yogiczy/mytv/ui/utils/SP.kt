package top.yogiczy.mytv.ui.utils

import android.content.Context
import android.content.SharedPreferences
import top.yogiczy.mytv.data.utils.Constants

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
        /** ==================== 应用 ==================== */
        /** 开机自启 */
        APP_BOOT_LAUNCH,

        /** ==================== 调式 ==================== */
        /** 显示fps */
        DEBUG_SHOW_FPS,

        /** ==================== 直播源 ==================== */
        /** 上一次直播源序号 */
        IPTV_LAST_IPTV_IDX,

        /** 换台反转 */
        IPTV_CHANNEL_CHANGE_FLIP,

        /** 直播源精简 */
        IPTV_SOURCE_SIMPLIFY,

        /** 直播源最近缓存时间 */
        IPTV_SOURCE_CACHED_AT,

        /** 直播源url */
        IPTV_SOURCE_URL,

        /** 直播源缓存时间（毫秒） */
        IPTV_SOURCE_CACHE_TIME,

        /** ==================== 节目单 ==================== */
        /** 启用epg */
        EPG_ENABLE,

        /** epg最近缓存时间 */
        EPG_XLM_CACHED_AT,

        /** epg解析最近缓存hash */
        EPG_CACHED_HASH,

        /** epg xml url */
        EPG_XML_URL,

        /** epg刷新时间阈值（小时） */
        EPG_REFRESH_TIME_THRESHOLD,
    }

    /** ==================== 应用 ==================== */
    /** 开机自启 */
    var appBootLaunch: Boolean
        get() = sp.getBoolean(KEY.APP_BOOT_LAUNCH.name, false)
        set(value) = sp.edit().putBoolean(KEY.APP_BOOT_LAUNCH.name, value).apply()

    /** ==================== 调式 ==================== */
    /** 显示fps */
    var debugShowFps: Boolean
        get() = sp.getBoolean(KEY.DEBUG_SHOW_FPS.name, false)
        set(value) = sp.edit().putBoolean(KEY.DEBUG_SHOW_FPS.name, value).apply()

    /** ==================== 直播源 ==================== */
    /** 上一次直播源序号 */
    var iptvLastIptvIdx: Int
        get() = sp.getInt(KEY.IPTV_LAST_IPTV_IDX.name, 0)
        set(value) = sp.edit().putInt(KEY.IPTV_LAST_IPTV_IDX.name, value).apply()

    /** 换台反转 */
    var iptvChannelChangeFlip: Boolean
        get() = sp.getBoolean(KEY.IPTV_CHANNEL_CHANGE_FLIP.name, false)
        set(value) = sp.edit().putBoolean(KEY.IPTV_CHANNEL_CHANGE_FLIP.name, value).apply()

    /** 直播源精简 */
    var iptvSourceSimplify: Boolean
        get() = sp.getBoolean(KEY.IPTV_SOURCE_SIMPLIFY.name, false)
        set(value) = sp.edit().putBoolean(KEY.IPTV_SOURCE_SIMPLIFY.name, value).apply()

    /** 直播源最近缓存时间 */
    var iptvSourceCachedAt: Long
        get() = sp.getLong(KEY.IPTV_SOURCE_CACHED_AT.name, 0)
        set(value) = sp.edit().putLong(KEY.IPTV_SOURCE_CACHED_AT.name, value).apply()

    /** 直播源 url */
    var iptvSourceUrl: String
        get() = (sp.getString(KEY.IPTV_SOURCE_URL.name, "")
            ?: "").ifBlank { Constants.IPTV_SOURCE_URL }
        set(value) = sp.edit().putString(KEY.IPTV_SOURCE_URL.name, value).apply()

    /** 直播源缓存时间（毫秒） */
    var iptvSourceCacheTime: Long
        get() = sp.getLong(KEY.IPTV_SOURCE_CACHE_TIME.name, Constants.IPTV_SOURCE_CACHE_TIME)
        set(value) = sp.edit().putLong(KEY.IPTV_SOURCE_CACHE_TIME.name, value).apply()

    /** ==================== 节目单 ==================== */
    /** 启用epg */
    var epgEnable: Boolean
        get() = sp.getBoolean(KEY.EPG_ENABLE.name, true)
        set(value) = sp.edit().putBoolean(KEY.EPG_ENABLE.name, value).apply()

    /** epg最近缓存时间 */
    var epgXmlCachedAt: Long
        get() = sp.getLong(KEY.EPG_XLM_CACHED_AT.name, 0)
        set(value) = sp.edit().putLong(KEY.EPG_XLM_CACHED_AT.name, value).apply()

    /** epg解析最近缓存hash */
    var epgCachedHash: Int
        get() = sp.getInt(KEY.EPG_CACHED_HASH.name, 0)
        set(value) = sp.edit().putInt(KEY.EPG_CACHED_HASH.name, value).apply()

    /** epg xml url */
    var epgXmlUrl: String
        get() = (sp.getString(KEY.EPG_XML_URL.name, "") ?: "").ifBlank { Constants.EPG_XML_URL }
        set(value) = sp.edit().putString(KEY.EPG_XML_URL.name, value).apply()

    /** epg刷新时间阈值（小时） */
    var epgRefreshTimeThreshold: Int
        get() = sp.getInt(KEY.EPG_REFRESH_TIME_THRESHOLD.name, Constants.EPG_REFRESH_TIME_THRESHOLD)
        set(value) = sp.edit().putInt(KEY.EPG_REFRESH_TIME_THRESHOLD.name, value).apply()
}