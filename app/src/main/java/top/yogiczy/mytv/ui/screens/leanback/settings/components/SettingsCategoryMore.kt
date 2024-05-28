package top.yogiczy.mytv.ui.screens.leanback.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.material3.MaterialTheme
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.QrShapes
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.coroutines.delay
import top.yogiczy.mytv.ui.theme.LeanbackTheme
import top.yogiczy.mytv.ui.utils.HttpServer

@Composable
fun LeanbackSettingsCategoryMore(
    modifier: Modifier = Modifier,
    serverUrl: String = HttpServer.serverUrl,
) {
    TvLazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 10.dp),
    ) {
        item {
            LeanbackSettingsCategoryListItem(
                headlineContent = "设置网址",
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
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .height(200.dp)
                            .background(
                                MaterialTheme.colorScheme.onBackground,
                                MaterialTheme.shapes.medium,
                            )
                            .padding(10.dp),
                    ) {
                        Image(
                            modifier = modifier.fillMaxSize(),
                            painter = rememberQrCodePainter(
                                data = serverUrl,
                                shapes = QrShapes(
                                    ball = QrBallShape.circle(),
                                    darkPixel = QrPixelShape.roundCorners(),
                                    frame = QrFrameShape.roundCorners(.25f),
                                ),
                            ),
                            contentDescription = serverUrl,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LeanbackSettingsMorePreview() {
    LeanbackTheme {
        LeanbackSettingsCategoryMore(
            serverUrl = "http://127.0.0.1:10481",
        )
    }
}
