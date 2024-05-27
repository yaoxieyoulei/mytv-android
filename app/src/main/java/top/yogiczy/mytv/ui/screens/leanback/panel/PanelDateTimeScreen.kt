package top.yogiczy.mytv.ui.screens.leanback.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.ui.rememberLeanbackChildPadding
import top.yogiczy.mytv.ui.theme.LeanbackTheme
import top.yogiczy.mytv.ui.utils.SP
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun LeanbackPanelDateTimeScreen(
    modifier: Modifier = Modifier,
    showModeProvider: () -> SP.UiTimeShowMode = { SP.UiTimeShowMode.HIDDEN },
) {
    val childPadding = rememberLeanbackChildPadding()

    var timeText by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            val timestamp = System.currentTimeMillis()

            visible = when (showModeProvider()) {
                SP.UiTimeShowMode.HIDDEN -> false
                SP.UiTimeShowMode.ALWAYS -> true

                SP.UiTimeShowMode.EVERY_HOUR -> {
                    timestamp % 3600000 <= (Constants.UI_TIME_SHOW_RANGE + 1000) || timestamp % 3600000 >= 3600000 - Constants.UI_TIME_SHOW_RANGE
                }

                SP.UiTimeShowMode.HALF_HOUR -> {
                    timestamp % 1800000 <= (Constants.UI_TIME_SHOW_RANGE + 1000) || timestamp % 1800000 >= 1800000 - Constants.UI_TIME_SHOW_RANGE
                }
            }

            if (visible) {
                timeText = when (showModeProvider()) {
                    SP.UiTimeShowMode.ALWAYS -> SimpleDateFormat("HH:mm", Locale.getDefault())
                    else -> SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                }.format(timestamp)
            }

            delay(1000)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (visible) {
            Text(
                text = timeText,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = childPadding.top, end = childPadding.end)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(0.8f),
                        shape = MaterialTheme.shapes.small,
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun LeanbackPanelDateTimeScreenPreview() {
    LeanbackTheme {
        LeanbackPanelDateTimeScreen(showModeProvider = { SP.UiTimeShowMode.ALWAYS })
    }
}