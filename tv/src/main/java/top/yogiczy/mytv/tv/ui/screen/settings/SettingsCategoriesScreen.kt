package top.yogiczy.mytv.tv.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.ControlCamera
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.DisplaySettings
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.SmartDisplay
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import top.yogiczy.mytv.tv.ui.rememberChildPadding
import top.yogiczy.mytv.tv.ui.screen.components.AppScreen
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme
import top.yogiczy.mytv.tv.ui.tooling.PreviewWithLayoutGrids
import top.yogiczy.mytv.tv.ui.utils.gridColumns
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents

@Composable
fun SettingsCategoriesScreen(
    modifier: Modifier = Modifier,
    toSettingsCategoryScreen: (category: SettingsCategories) -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    val childPadding = rememberChildPadding()

    AppScreen(
        modifier = modifier.padding(top = 10.dp),
        header = { Text("设置") },
        canBack = true,
        onBackPressed = onBackPressed,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            contentPadding = childPadding.copy(top = 10.dp).paddingValues,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            items(SettingsCategories.entries) {
                SettingsCategoryItem(
                    title = it.title,
                    imageVector = it.icon,
                    onSelected = { toSettingsCategoryScreen(it) },
                )
            }
        }
    }
}

@Composable
private fun SettingsCategoryItem(
    modifier: Modifier = Modifier,
    title: String,
    imageVector: ImageVector,
    onSelected: () -> Unit = {},
) {
    Surface(
        modifier = modifier
            .size(2.gridColumns())
            .handleKeyEvents(onSelect = onSelected),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.onSurface.copy(0.1f),
        ),
        onClick = {},
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(imageVector, contentDescription = null, modifier = Modifier.size(34.dp))
            Spacer(Modifier.height(10.dp))
            Text(title, style = MaterialTheme.typography.titleLarge)
        }
    }
}

enum class SettingsCategories(
    val icon: ImageVector,
    val title: String,
) {
    SYSTEM(Icons.Outlined.Devices, "系统"),
    IPTV(Icons.Outlined.LiveTv, "直播源"),
    EPG(Icons.AutoMirrored.Outlined.LibraryBooks, "节目单"),
    UI(Icons.Outlined.DisplaySettings, "界面"),
    CONTROL(Icons.Outlined.ControlCamera, "控制"),
    VIDEO_PLAYER(Icons.Outlined.SmartDisplay, "播放器"),
    UPDATE(Icons.Outlined.Update, "更新"),

    // FAVORITE(Icons.Outlined.FavoriteBorder, "收藏"),
    // EPG_RESERVE(Icons.Default.BookmarkBorder, "预约"),
    NETWORK(Icons.Outlined.Wifi, "网络"),
    THEME(Icons.Outlined.ColorLens, "主题"),
    DEBUG(Icons.Outlined.BugReport, "调试"),
    LOG(Icons.AutoMirrored.Outlined.FormatListBulleted, "日志"),
}

enum class SettingsSubCategories {
    IPTV_SOURCE,
    IPTV_SOURCE_CACHE_TIME,
    CHANNEL_GROUP_VISIBILITY,
    IPTV_HYBRID_MODE,
    EPG_SOURCE,
    EPG_REFRESH_TIME_THRESHOLD,
    UI_TIME_SHOW_MODE,
    UI_SCREEN_AUTO_CLOSE_DELAY,
    UI_DENSITY_SCALE_RATIO,
    UI_FONT_SCALE_RATIO,
    VIDEO_PLAYER_DISPLAY_MODE,
    VIDEO_PLAYER_LOAD_TIMEOUT,
    UPDATE_CHANNEL,
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsCategoriesScreenPreview() {
    MyTvTheme {
        SettingsCategoriesScreen()
        PreviewWithLayoutGrids { }
    }
}