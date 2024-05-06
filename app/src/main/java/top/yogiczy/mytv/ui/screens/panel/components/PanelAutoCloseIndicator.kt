package top.yogiczy.mytv.ui.screens.panel.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import kotlinx.coroutines.launch
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.screens.panel.PanelAutoCloseState
import top.yogiczy.mytv.ui.screens.panel.rememberPanelAutoCloseState
import top.yogiczy.mytv.ui.theme.MyTVTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PanelAutoCloseIndicator(
    modifier: Modifier = Modifier,
    panelAutoCloseState: PanelAutoCloseState = rememberPanelAutoCloseState(),
) {
    val childPadding = rememberChildPadding()
    val coroutineScope = rememberCoroutineScope()

    val animatedProgress = remember { Animatable(initialValue = 1f) }
    panelAutoCloseState.onActive {
        coroutineScope.launch {
            animatedProgress.animateTo(1f)
            animatedProgress.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 10_000, easing = LinearEasing)
            )
        }
    }

    val color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)

    Canvas(
        modifier
            .size(20.dp)
            .padding(4.dp),
    ) {
        val sweepAngle = 360 * animatedProgress.value
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = 2f),
        )
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun PanelAutoCloseIndicatorPreview() {
    MyTVTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            PanelAutoCloseIndicator()
        }
    }
}