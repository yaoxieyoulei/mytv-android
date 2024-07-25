package top.yogiczy.mytv.tv.ui.screens.settings.components

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import top.yogiczy.mytv.core.data.utils.Logger
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SettingsCategoryLog(
    modifier: Modifier = Modifier,
    history: List<Logger.HistoryItem> = Logger.history,
) {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    val historySorted = remember(history) { history.sortedByDescending { it.time } }

    SettingsContentList(modifier) {
        items(historySorted) {
            SettingsListItem(
                headlineContent = "${it.level.toString()[0]} ${it.tag}",
                supportingContent = it.message,
                trailingContent = timeFormat.format(it.time),
            )
        }
    }
}
