package top.yogiczy.mytv.tv.ui.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.QrShapes
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.rememberQrCodePainter


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
