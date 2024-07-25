package top.yogiczy.mytv.tv.ui.screens.videoplayer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme

@Composable
fun VideoPlayerError(
    modifier: Modifier = Modifier,
    errorProvider: () -> String? = { null },
) {
    val error = errorProvider() ?: return

    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = MaterialTheme.shapes.medium,
            )
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "播放失败",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error,
        )

        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = LocalContentColor.current.copy(alpha = 0.8f),
        )
    }
}

@Preview
@Composable
private fun VideoPlayerErrorPreview() {
    MyTVTheme {
        VideoPlayerError(
            errorProvider = { "ERROR_CODE_BEHIND_LIVE_WINDOW" }
        )
    }
}