package top.yogiczy.mytv.tv.ui.screens.settings.components

import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import top.yogiczy.mytv.core.data.utils.Logger
import top.yogiczy.mytv.tv.ui.utils.ifElse
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
        itemsIndexed(historySorted) { index, log ->
            SettingsListItem(
                modifier = Modifier.ifElse(index == 0, Modifier.focusRequester(it)),
                headlineContent = "${log.level.toString()[0]} ${log.tag}",
                supportingContent = log.message,
                trailingContent = timeFormat.format(log.time),
            )
        }
    }
}
