package top.yogiczy.mytv.ui.screens.leanback.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.delay
import top.yogiczy.mytv.ui.rememberLeanbackChildPadding
import top.yogiczy.mytv.ui.theme.LeanbackTheme

@Composable
fun LeanbackToastScreen(
    modifier: Modifier = Modifier,
    state: LeanbackToastState = rememberLeanbackToastState(),
) {
    val childPadding = rememberLeanbackChildPadding()

    Box(modifier = modifier.fillMaxSize()) {
        Popup(
            offset = IntOffset(
                x = with(LocalDensity.current) { childPadding.start.toPx().toInt() },
                y = with(LocalDensity.current) { childPadding.top.toPx().toInt() },
            ),
        ) {
            AnimatedVisibility(visible = state.visible) {
                LeanbackToastItem(property = state.current)
            }
        }
    }
}

@Composable
fun LeanbackToastItem(
    modifier: Modifier = Modifier,
    property: LeanbackToastProperty = LeanbackToastProperty(),
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                shape = MaterialTheme.shapes.small,
            )
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(text = property.message)
    }
}

@Preview(device = "id:Android TV (720p)", showBackground = true)
@Composable
private fun LeanbackToastScreenAnimationPreview() {
    LeanbackTheme {
        val state = rememberLeanbackToastState()

        LaunchedEffect(Unit) {
            while (true) {
                state.showToast("新版本: v1.2.2")
                delay(1000)
                state.showToast("新版本: v9.9.9")
                delay(5000)
            }
        }

        LeanbackToastScreen(state = state)
    }
}

@Preview(showBackground = true)
@Composable
private fun LeanbackToastScreenPreview() {
    LeanbackTheme {
        LeanbackToastItem(
            modifier = Modifier.padding(16.dp),
            property = LeanbackToastProperty(message = "新版本: v1.2.2"),
        )
    }
}
