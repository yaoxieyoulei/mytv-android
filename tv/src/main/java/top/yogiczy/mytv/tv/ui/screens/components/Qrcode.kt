package top.yogiczy.mytv.tv.ui.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.surfaceColorAtElevation
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.QrShapes
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import top.yogiczy.mytv.tv.ui.material.PopupHandleableApplication
import top.yogiczy.mytv.tv.ui.material.SimplePopup
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme


@Composable
fun Qrcode(
    modifier: Modifier = Modifier,
    textProvider: () -> String = { "" },
) {
    val text = textProvider()

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium)
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .padding(10.dp),
            painter = rememberQrCodePainter(
                data = text,
                shapes = QrShapes(
                    ball = QrBallShape.circle(),
                    darkPixel = QrPixelShape.roundCorners(),
                    frame = QrFrameShape.roundCorners(.25f),
                ),
            ),
            contentDescription = text,
        )
    }
}

@Composable
fun QrcodeDialog(
    modifier: Modifier = Modifier,
    textProvider: () -> String,
    descriptionProvider: () -> String? = { null },
    showDialogProvider: () -> Boolean = { false },
    onDismissRequest: () -> Unit = {},
) {
    SimplePopup(
        visibleProvider = showDialogProvider,
        onDismissRequest = onDismissRequest,
        withBackground = true,
    ) {
        Column(
            modifier = modifier
                .align(Alignment.Center)
                .background(
                    MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                    MaterialTheme.shapes.large,
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            descriptionProvider()?.let {
                Text(it, style = MaterialTheme.typography.headlineMedium)
            }

            Qrcode(
                textProvider = textProvider,
                modifier = Modifier.size(240.dp),
            )
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun QrcodeDialogPreview() {
    MyTVTheme {
        PopupHandleableApplication {
            QrcodeDialog(
                textProvider = { "Hello, World!" },
                descriptionProvider = { "This is a QR code" },
                showDialogProvider = { true },
            )
        }
    }
}