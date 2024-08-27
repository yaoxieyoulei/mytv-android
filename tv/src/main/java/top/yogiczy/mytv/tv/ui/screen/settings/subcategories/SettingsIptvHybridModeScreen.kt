package top.yogiczy.mytv.tv.ui.screen.settings.subcategories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.tv.ui.rememberChildPadding
import top.yogiczy.mytv.tv.ui.screen.components.AppScreen
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme
import top.yogiczy.mytv.tv.ui.utils.Configs
import top.yogiczy.mytv.tv.ui.utils.handleKeyEventsOnFocused

@Composable
fun SettingsIptvHybridModeScreen(
    modifier: Modifier = Modifier,
    hybridModeProvider: () -> Configs.IptvHybridMode = { Configs.IptvHybridMode.DISABLE },
    onHybridModeChanged: (Configs.IptvHybridMode) -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    val childPadding = rememberChildPadding()

    AppScreen(
        modifier = modifier.padding(top = 20.dp),
        header = { Text("设置 / 直播源 / 混合模式") },
        canBack = true,
        onBackPressed = onBackPressed,
    ) {
        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Fixed(1),
            contentPadding = childPadding.copy(top = 4.dp).paddingValues,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(Configs.IptvHybridMode.entries) { mode ->
                ListItem(
                    modifier = Modifier.handleKeyEventsOnFocused(
                        onSelect = { onHybridModeChanged(mode) },
                    ),
                    headlineContent = {
                        Text(
                            when (mode) {
                                Configs.IptvHybridMode.DISABLE -> "禁用"
                                Configs.IptvHybridMode.IPTV_FIRST -> "直播源优先"
                                Configs.IptvHybridMode.HYBRID_FIRST -> "混合优先"
                            }
                        )
                    },
                    supportingContent = {
                        Text(
                            when (mode) {
                                Configs.IptvHybridMode.DISABLE -> ""
                                Configs.IptvHybridMode.IPTV_FIRST -> "优先尝试播放直播源中线路，若所有直播源线路不可用，则尝试混合线路"
                                Configs.IptvHybridMode.HYBRID_FIRST -> "优先尝试播放混合线路，若混合线路不可用，则播放直播源中线路"
                            }
                        )
                    },
                    trailingContent = {
                        if (hybridModeProvider() == mode) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                        }
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.onSurface.copy(0.1f),
                    ),
                    selected = false,
                    onClick = {},
                )
            }
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsIptvHybridModeScreenPreview() {
    MyTvTheme {
        SettingsIptvHybridModeScreen()
    }
}