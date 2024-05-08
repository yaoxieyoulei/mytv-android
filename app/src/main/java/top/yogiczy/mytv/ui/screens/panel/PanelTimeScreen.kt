package top.yogiczy.mytv.ui.screens.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.screens.panel.components.rememberTimestamp
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.SP
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PanelTimeScreen(
    modifier: Modifier = Modifier,
    showMode: SP.UiTimeShowMode = SP.UiTimeShowMode.HIDDEN,
) {
    val childPadding = rememberChildPadding()
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val timestamp = rememberTimestamp()

    var visible by remember { mutableStateOf(false) }
    when (showMode) {
        SP.UiTimeShowMode.HIDDEN -> visible = false
        SP.UiTimeShowMode.ALWAYS -> visible = true
        SP.UiTimeShowMode.EVERY_HOUR -> {
            LaunchedEffect(timestamp) {
                visible = timestamp % 3600000 <= Constants.UI_TIME_SHOW_RANGE
                        || timestamp % 3600000 >= 3600000 - Constants.UI_TIME_SHOW_RANGE
            }
        }

        SP.UiTimeShowMode.HALF_HOUR -> {
            LaunchedEffect(timestamp) {
                visible = timestamp % 1800000 <= Constants.UI_TIME_SHOW_RANGE
                        || timestamp % 1800000 >= 1800000 - Constants.UI_TIME_SHOW_RANGE
            }
        }
    }

    if (visible) {
        Box(modifier = modifier.fillMaxSize()) {
            Text(
                text = timeFormat.format(timestamp),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = childPadding.top, end = childPadding.end)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(0.5f),
                        shape = MaterialTheme.shapes.small,
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun PanelTimeScreenPreview() {
    MyTVTheme {
        PanelTimeScreen()
    }
}