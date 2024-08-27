package top.yogiczy.mytv.tv.ui.screen.settings.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.Text
import top.yogiczy.mytv.core.data.utils.Logger
import top.yogiczy.mytv.tv.ui.rememberChildPadding
import top.yogiczy.mytv.tv.ui.screen.components.AppScreen
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SettingsLogScreen(
    modifier: Modifier = Modifier,
    historyItemList: List<Logger.HistoryItem> = Logger.history,
    onBackPressed: () -> Unit = {},
) {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val childPadding = rememberChildPadding()

    AppScreen(
        modifier = modifier.padding(top = 10.dp),
        header = { Text("设置 / 日志") },
        canBack = true,
        onBackPressed = onBackPressed,
    ) {
        LazyColumn(
            contentPadding = childPadding.copy(top = 10.dp).paddingValues,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(historyItemList.reversed()) { log ->
                ListItem(
                    leadingContent = { Text(log.level.toString()[0].toString()) },
                    headlineContent = { Text(log.tag) },
                    supportingContent = { Text(log.message) },
                    trailingContent = { Text(timeFormat.format(log.time)) },
                    selected = false,
                    onClick = {},
                )
            }
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun Preview() {
    MyTvTheme {
        SettingsLogScreen(
            historyItemList = listOf(
                Logger.HistoryItem(
                    level = Logger.LevelType.DEBUG,
                    tag = "SettingsLogScreen",
                    message = "message level = Logger.LevelType.INFO,",
                    time = System.currentTimeMillis(),
                ),
                Logger.HistoryItem(
                    level = Logger.LevelType.INFO,
                    tag = "SettingsLogScreen",
                    message = "message level = Logger.LevelType.INFO,",
                    time = System.currentTimeMillis(),
                ),
                Logger.HistoryItem(
                    level = Logger.LevelType.WARN,
                    tag = "SettingsLogScreen",
                    message = "message level = Logger.LevelType.INFO,",
                    time = System.currentTimeMillis(),
                ),
                Logger.HistoryItem(
                    level = Logger.LevelType.ERROR,
                    tag = "SettingsLogScreen",
                    message = "message level = Logger.LevelType.INFO,",
                    time = System.currentTimeMillis(),
                ),
            )
        )
    }
}