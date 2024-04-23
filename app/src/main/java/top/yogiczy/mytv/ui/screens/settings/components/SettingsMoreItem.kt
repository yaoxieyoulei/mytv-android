package top.yogiczy.mytv.ui.screens.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.QrShapes
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.HttpServer

@Composable
fun SettingsMoreItem(modifier: Modifier = Modifier) {
    var showServerQrcode by remember { mutableStateOf(false) }
    val serverUrl = "http://${HttpServer.getLocalIpAddress()}:${HttpServer.SERVER_PORT}"

    SettingsItem(
        modifier = modifier,
        title = "更多设置",
        value = "",
        description = "访问以下网址进行配置：$serverUrl",
        onClick = { showServerQrcode = true },
    )

    if (showServerQrcode) {
        SettingsQrcodeDialog(
            onDismissRequest = { showServerQrcode = false },
            data = serverUrl,
        )
    }
}


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SettingsQrcodeDialog(
    modifier: Modifier = Modifier,
    data: String = "",
    onDismissRequest: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        content = {
            Box(
                modifier = modifier
                    .width(200.dp)
                    .height(200.dp)
                    .background(
                        MaterialTheme.colorScheme.onBackground,
                        MaterialTheme.shapes.medium,
                    )
                    .padding(16.dp),
            ) {
                Image(
                    modifier = modifier.fillMaxSize(),
                    painter = rememberQrCodePainter(
                        data = data,
                        shapes = QrShapes(
                            ball = QrBallShape.circle(),
                            darkPixel = QrPixelShape.roundCorners(),
                            frame = QrFrameShape.roundCorners(.25f),
                        ),
                    ),
                    contentDescription = data,
                )
            }
        },
    )
}

@Preview
@Composable
private fun SettingsQrcodeDialogPreview() {
    MyTVTheme {
        SettingsQrcodeDialog(
            data = "data",
        )
    }
}