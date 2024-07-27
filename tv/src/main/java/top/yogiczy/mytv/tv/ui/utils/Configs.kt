package top.yogiczy.mytv.tv.ui.utils

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.yogiczy.mytv.core.data.entities.epg.EpgProgrammeReserveList
import top.yogiczy.mytv.core.data.entities.epgsource.EpgSourceList
import top.yogiczy.mytv.core.data.entities.iptvsource.IptvSourceList
import top.yogiczy.mytv.core.data.utils.Constants
import top.yogiczy.mytv.core.data.utils.SP

/**
 * 应用配置
 */
object Configs {
    enum class KEY {
        /** ==================== 应用 ==================== */
        /** 开机自启 */
        APP_BOOT_LAUNCH,

        /** 上一次最新版本 */
        APP_LAST_LATEST_VERSION,

        /** ==================== 调式 ==================== */
        /** 显示fps */
        DEBUG_SHOW_FPS,

        /** 播放器详细信息 */
        DEBUG_SHOW_VIDEO_PLAYER_METADATA,

        /** 显示布局网格 */
        DEBUG_SHOW_LAYOUT_GRIDS,

        /** ==================== 直播源 ==================== */
        /** 上一次频道序号 */
        IPTV_LAST_CHANNEL_IDX,

        /** 换台反转 */
        IPTV_CHANNEL_CHANGE_FLIP,

        /** 直播源精简 */
        IPTV_SOURCE_SIMPLIFY,

        /** 直播源url */
        IPTV_SOURCE_URL,

        /** 直播源列表 */
        IPTV_SOURCE_LIST,

        /** 直播源缓存时间（毫秒） */
        IPTV_SOURCE_CACHE_TIME,

        /** 直播源可播放host列表 */
        IPTV_PLAYABLE_HOST_LIST,

        /** 是否启用数字选台 */
        IPTV_CHANNEL_NO_SELECT_ENABLE,

        /** 是否启用直播源频道收藏 */
        IPTV_CHANNEL_FAVORITE_ENABLE,

        /** 显示直播源频道收藏列表 */
        IPTV_CHANNEL_FAVORITE_LIST_VISIBLE,

        /** 直播源频道收藏列表 */
        IPTV_CHANNEL_FAVORITE_LIST,

        /** 直播源频道收藏换台边界跳出 */
        IPTV_CHANNEL_FAVORITE_CHANGE_BOUNDARY_JUMP_OUT,

        /** 混合模式 */
        IPTV_HYBRID_MODE,

        /** ==================== 节目单 ==================== */
        /** 启用节目单 */
        EPG_ENABLE,

        /** 节目单 xml url */
        EPG_XML_URL,

        /** 节目单来源列表 */
        EPG_SOURCE_LIST,

        /** 节目单刷新时间阈值（小时） */
        EPG_REFRESH_TIME_THRESHOLD,

        /** 节目预约列表 */
        EPG_CHANNEL_RESERVE_LIST,

        /** ==================== 界面 ==================== */
        /** 显示节目进度 */
        UI_SHOW_EPG_PROGRAMME_PROGRESS,

        /** 使用经典选台界面 */
        UI_USE_CLASSIC_PANEL_SCREEN,

        /** 界面密度缩放比例 */
        UI_DENSITY_SCALE_RATIO,

        /** 界面字体缩放比例 */
        UI_FONT_SCALE_RATIO,

        /** 时间显示模式 */
        UI_TIME_SHOW_MODE,

        /** ==================== 更新 ==================== */
        /** 更新强提醒（弹窗形式） */
        UPDATE_FORCE_REMIND,

        /** ==================== 播放器 ==================== */
        /** 播放器 自定义ua */
        VIDEO_PLAYER_USER_AGENT,

        /** 播放器 加载超时 */
        VIDEO_PLAYER_LOAD_TIMEOUT,

        /** 播放器 画面比例 */
        VIDEO_PLAYER_ASPECT_RATIO,
    }

    /** ==================== 应用 ==================== */
    /** 开机自启 */
    var appBootLaunch: Boolean
        get() = SP.getBoolean(KEY.APP_BOOT_LAUNCH.name, false)
        set(value) = SP.putBoolean(KEY.APP_BOOT_LAUNCH.name, value)

    /** 上一次最新版本 */
    var appLastLatestVersion: String
        get() = SP.getString(KEY.APP_LAST_LATEST_VERSION.name, "")
        set(value) = SP.putString(KEY.APP_LAST_LATEST_VERSION.name, value)

    /** ==================== 调式 ==================== */
    /** 显示fps */
    var debugShowFps: Boolean
        get() = SP.getBoolean(KEY.DEBUG_SHOW_FPS.name, false)
        set(value) = SP.putBoolean(KEY.DEBUG_SHOW_FPS.name, value)

    /** 播放器详细信息 */
    var debugShowVideoPlayerMetadata: Boolean
        get() = SP.getBoolean(KEY.DEBUG_SHOW_VIDEO_PLAYER_METADATA.name, false)
        set(value) = SP.putBoolean(KEY.DEBUG_SHOW_VIDEO_PLAYER_METADATA.name, value)

    /** 显示布局网格 */
    var debugShowLayoutGrids: Boolean
        get() = SP.getBoolean(KEY.DEBUG_SHOW_LAYOUT_GRIDS.name, false)
        set(value) = SP.putBoolean(KEY.DEBUG_SHOW_LAYOUT_GRIDS.name, value)

    /** ==================== 直播源 ==================== */
    /** 上一次直播源序号 */
    var iptvLastChannelIdx: Int
        get() = SP.getInt(KEY.IPTV_LAST_CHANNEL_IDX.name, 0)
        set(value) = SP.putInt(KEY.IPTV_LAST_CHANNEL_IDX.name, value)

    /** 换台反转 */
    var iptvChannelChangeFlip: Boolean
        get() = SP.getBoolean(KEY.IPTV_CHANNEL_CHANGE_FLIP.name, false)
        set(value) = SP.putBoolean(KEY.IPTV_CHANNEL_CHANGE_FLIP.name, value)

    /** 直播源精简 */
    var iptvSourceSimplify: Boolean
        get() = SP.getBoolean(KEY.IPTV_SOURCE_SIMPLIFY.name, false)
        set(value) = SP.putBoolean(KEY.IPTV_SOURCE_SIMPLIFY.name, value)

    /** 直播源url */
    var iptvSourceUrl: String
        get() = SP.getString(KEY.IPTV_SOURCE_URL.name, "")
            .ifBlank { Constants.IPTV_SOURCE_LIST.first().url }
        set(value) = SP.putString(KEY.IPTV_SOURCE_URL.name, value)

    /** 直播源列表 */
    var iptvSourceList: IptvSourceList
        get() = Json.decodeFromString(
            SP.getString(KEY.IPTV_SOURCE_LIST.name, Json.encodeToString(IptvSourceList()))
        )
        set(value) = SP.putString(KEY.IPTV_SOURCE_LIST.name, Json.encodeToString(value))

    /** 直播源缓存时间（毫秒） */
    var iptvSourceCacheTime: Long
        get() = SP.getLong(KEY.IPTV_SOURCE_CACHE_TIME.name, Constants.IPTV_SOURCE_CACHE_TIME)
        set(value) = SP.putLong(KEY.IPTV_SOURCE_CACHE_TIME.name, value)

    /** 直播源可播放host列表 */
    var iptvPlayableHostList: Set<String>
        get() = SP.getStringSet(KEY.IPTV_PLAYABLE_HOST_LIST.name, emptySet())
        set(value) = SP.putStringSet(KEY.IPTV_PLAYABLE_HOST_LIST.name, value)

    /** 是否启用数字选台 */
    var iptvChannelNoSelectEnable: Boolean
        get() = SP.getBoolean(KEY.IPTV_CHANNEL_NO_SELECT_ENABLE.name, true)
        set(value) = SP.putBoolean(KEY.IPTV_CHANNEL_NO_SELECT_ENABLE.name, value)

    /** 是否启用直播源频道收藏 */
    var iptvChannelFavoriteEnable: Boolean
        get() = SP.getBoolean(KEY.IPTV_CHANNEL_FAVORITE_ENABLE.name, true)
        set(value) = SP.putBoolean(KEY.IPTV_CHANNEL_FAVORITE_ENABLE.name, value)

    /** 显示直播源频道收藏列表 */
    var iptvChannelFavoriteListVisible: Boolean
        get() = SP.getBoolean(KEY.IPTV_CHANNEL_FAVORITE_LIST_VISIBLE.name, false)
        set(value) = SP.putBoolean(KEY.IPTV_CHANNEL_FAVORITE_LIST_VISIBLE.name, value)

    /** 直播源频道收藏列表 */
    var iptvChannelFavoriteList: Set<String>
        get() = SP.getStringSet(KEY.IPTV_CHANNEL_FAVORITE_LIST.name, emptySet())
        set(value) = SP.putStringSet(KEY.IPTV_CHANNEL_FAVORITE_LIST.name, value)

    /** 直播源频道收藏换台边界跳出 */
    var iptvChannelFavoriteChangeBoundaryJumpOut: Boolean
        get() = SP.getBoolean(KEY.IPTV_CHANNEL_FAVORITE_CHANGE_BOUNDARY_JUMP_OUT.name, true)
        set(value) = SP.putBoolean(KEY.IPTV_CHANNEL_FAVORITE_CHANGE_BOUNDARY_JUMP_OUT.name, value)

    /** 混合模式 */
    var iptvHybridMode: IptvHybridMode
        get() = IptvHybridMode.fromValue(
            SP.getInt(KEY.IPTV_HYBRID_MODE.name, IptvHybridMode.DISABLE.value)
        )
        set(value) = SP.putInt(KEY.IPTV_HYBRID_MODE.name, value.value)

    /** ==================== 节目单 ==================== */
    /** 启用节目单 */
    var epgEnable: Boolean
        get() = SP.getBoolean(KEY.EPG_ENABLE.name, true)
        set(value) = SP.putBoolean(KEY.EPG_ENABLE.name, value)

    /** 节目单 xml url */
    var epgXmlUrl: String
        get() = SP.getString(KEY.EPG_XML_URL.name, "")
            .ifBlank { Constants.EPG_SOURCE_LIST.first().url }
        set(value) = SP.putString(KEY.EPG_XML_URL.name, value)

    /** 节目单来源列表 */
    var epgSourceList: EpgSourceList
        get() = Json.decodeFromString(
            SP.getString(KEY.EPG_SOURCE_LIST.name, Json.encodeToString(EpgSourceList()))
        )
        set(value) = SP.putString(KEY.EPG_SOURCE_LIST.name, Json.encodeToString(value))

    /** 节目单刷新时间阈值（小时） */
    var epgRefreshTimeThreshold: Int
        get() = SP.getInt(KEY.EPG_REFRESH_TIME_THRESHOLD.name, Constants.EPG_REFRESH_TIME_THRESHOLD)
        set(value) = SP.putInt(KEY.EPG_REFRESH_TIME_THRESHOLD.name, value)

    /** 节目预约列表 */
    var epgChannelReserveList: EpgProgrammeReserveList
        get() = Json.decodeFromString(
            SP.getString(
                KEY.EPG_CHANNEL_RESERVE_LIST.name, Json.encodeToString(EpgProgrammeReserveList())
            )
        )
        set(value) = SP.putString(KEY.EPG_CHANNEL_RESERVE_LIST.name, Json.encodeToString(value))

    /** ==================== 界面 ==================== */
    /** 显示节目进度 */
    var uiShowEpgProgrammeProgress: Boolean
        get() = SP.getBoolean(KEY.UI_SHOW_EPG_PROGRAMME_PROGRESS.name, true)
        set(value) = SP.putBoolean(KEY.UI_SHOW_EPG_PROGRAMME_PROGRESS.name, value)

    /** 使用经典选台界面 */
    var uiUseClassicPanelScreen: Boolean
        get() = SP.getBoolean(KEY.UI_USE_CLASSIC_PANEL_SCREEN.name, false)
        set(value) = SP.putBoolean(KEY.UI_USE_CLASSIC_PANEL_SCREEN.name, value)

    /** 界面密度缩放比例 */
    var uiDensityScaleRatio: Float
        get() = SP.getFloat(KEY.UI_DENSITY_SCALE_RATIO.name, 1f)
        set(value) = SP.putFloat(KEY.UI_DENSITY_SCALE_RATIO.name, value)

    /** 界面字体缩放比例 */
    var uiFontScaleRatio: Float
        get() = SP.getFloat(KEY.UI_FONT_SCALE_RATIO.name, 1f)
        set(value) = SP.putFloat(KEY.UI_FONT_SCALE_RATIO.name, value)

    /** 时间显示模式 */
    var uiTimeShowMode: UiTimeShowMode
        get() = UiTimeShowMode.fromValue(
            SP.getInt(KEY.UI_TIME_SHOW_MODE.name, UiTimeShowMode.HIDDEN.value)
        )
        set(value) = SP.putInt(KEY.UI_TIME_SHOW_MODE.name, value.value)

    /** ==================== 更新 ==================== */
    /** 更新强提醒（弹窗形式） */
    var updateForceRemind: Boolean
        get() = SP.getBoolean(KEY.UPDATE_FORCE_REMIND.name, false)
        set(value) = SP.putBoolean(KEY.UPDATE_FORCE_REMIND.name, value)

    /** ==================== 播放器 ==================== */
    /** 播放器 自定义ua */
    var videoPlayerUserAgent: String
        get() = SP.getString(KEY.VIDEO_PLAYER_USER_AGENT.name, "").ifBlank {
            Constants.VIDEO_PLAYER_USER_AGENT
        }
        set(value) = SP.putString(KEY.VIDEO_PLAYER_USER_AGENT.name, value)

    /** 播放器 加载超时 */
    var videoPlayerLoadTimeout: Long
        get() = SP.getLong(KEY.VIDEO_PLAYER_LOAD_TIMEOUT.name, Constants.VIDEO_PLAYER_LOAD_TIMEOUT)
        set(value) = SP.putLong(KEY.VIDEO_PLAYER_LOAD_TIMEOUT.name, value)

    /** 播放器 画面比例 */
    var videoPlayerAspectRatio: VideoPlayerAspectRatio
        get() = VideoPlayerAspectRatio.fromValue(
            SP.getInt(KEY.VIDEO_PLAYER_ASPECT_RATIO.name, VideoPlayerAspectRatio.ORIGINAL.value)
        )
        set(value) = SP.putInt(KEY.VIDEO_PLAYER_ASPECT_RATIO.name, value.value)

    enum class UiTimeShowMode(val value: Int) {
        /** 隐藏 */
        HIDDEN(0),

        /** 常显 */
        ALWAYS(1),

        /** 整点 */
        EVERY_HOUR(2),

        /** 半点 */
        HALF_HOUR(3);

        companion object {
            fun fromValue(value: Int): UiTimeShowMode {
                return entries.firstOrNull { it.value == value } ?: ALWAYS
            }
        }
    }

    enum class IptvHybridMode(val value: Int) {
        /** 禁用 */
        DISABLE(0),

        /** 直播源优先 */
        IPTV_FIRST(1),

        /** 混合优先 */
        HYBRID_FIRST(2);

        companion object {
            fun fromValue(value: Int): IptvHybridMode {
                return entries.firstOrNull { it.value == value } ?: DISABLE
            }
        }
    }

    enum class VideoPlayerAspectRatio(val value: Int) {
        /** 原始 */
        ORIGINAL(0),

        /** 16:9 */
        SIXTEEN_NINE(1),

        /** 4:3 */
        FOUR_THREE(2),

        /** 自动拉伸 */
        AUTO(3);

        companion object {
            fun fromValue(value: Int): VideoPlayerAspectRatio {
                return entries.firstOrNull { it.value == value } ?: ORIGINAL
            }
        }
    }
}