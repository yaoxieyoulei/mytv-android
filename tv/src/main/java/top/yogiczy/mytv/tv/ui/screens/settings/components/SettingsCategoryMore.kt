package top.yogiczy.mytv.tv.ui.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import top.yogiczy.mytv.tv.ui.screens.components.Qrcode
import top.yogiczy.mytv.tv.utlis.HttpServer

@Composable
fun SettingsCategoryMore(
    modifier: Modifier = Modifier,
) {
    val serverUrl: String = HttpServer.serverUrl

    SettingsContentList(modifier) {
        item {
            SettingsListItem(
                modifier = Modifier.focusRequester(it),
                headlineContent = "设置页面",
                trailingContent = serverUrl,
            )
        }

        item {
            var show by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                delay(100)
                show = true
            }

            if (show) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Qrcode(
                        modifier = Modifier
                            .width(200.dp)
                            .height(200.dp),
                        textProvider = { serverUrl },
                    )
                }
            }
        }
    }
}