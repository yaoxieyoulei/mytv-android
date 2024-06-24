package top.yogiczy.mytv.ui.screens.leanback.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Http
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Update
import androidx.compose.ui.graphics.vector.ImageVector

enum class LeanbackSettingsCategories(
    val icon: ImageVector,
    val title: String
) {
    ABOUT(Icons.Default.Info, "关于"),
    APP(Icons.Default.Settings, "应用"),
    IPTV(Icons.Default.LiveTv, "直播源"),
    EPG(Icons.Default.Menu, "节目单"),
    UI(Icons.Default.DisplaySettings, "界面"),
    FAVORITE(Icons.Default.Star, "收藏"),
    UPDATE(Icons.Default.Update, "更新"),
    VIDEO_PLAYER(Icons.Default.SmartDisplay, "播放器"),
    HTTP(Icons.Default.Http, "网络"),
    DEBUG(Icons.Default.BugReport, "调试"),
    LOG(Icons.Default.FormatListNumbered, "日志"),
    MORE(Icons.Default.MoreHoriz, "更多设置"),
}