package top.yogiczy.mytv.ui.screens.monitor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.theme.MyTVTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MonitorScreen(
    modifier: Modifier = Modifier,
) {
    val childPadding = rememberChildPadding()
    val fpsState = rememberFpsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(start = childPadding.start, top = childPadding.top)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                    shape = MaterialTheme.shapes.extraSmall,
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            MonitorFps(fps = fpsState.current)
            Spacer(modifier = Modifier.height(4.dp))
            MonitorFpsBar(fpsList = fpsState.history)
        }
    }
}

data class FpsState(
    val current: Int = 0,
    val history: List<Int> = emptyList(),
)

@Composable
fun rememberFpsState(): FpsState {
    var state by remember { mutableStateOf(FpsState()) }

    var fpsCount by remember { mutableIntStateOf(0) }
    var lastUpdate by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { ms ->
                fpsCount++
                if (fpsCount == 5) {
                    val fps = (5_000 / (ms - lastUpdate)).toInt()
                    state = state.copy(current = fps, history = state.history.takeLast(30) + fps)
                    lastUpdate = ms
                    fpsCount = 0
                }
            }
        }
    }

    return state
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MonitorFps(modifier: Modifier = Modifier, fps: Int = 0) {
    Text(
        text = "FPS: $fps",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
fun MonitorFpsBar(
    modifier: Modifier = Modifier,
    fpsList: List<Int> = emptyList(),
) {
    Canvas(
        modifier = modifier
            .width(140.dp)
            .height(40.dp),
    ) {
        val barWidth = size.width / 60 // 每个柱状条的宽度
        val barSpacing = 2.dp.toPx() // 柱状条之间的间距
        val maxBarHeight = size.height // 柱状图的最大高度

        for (i in fpsList.indices) {
            val fps = fpsList[i]

            val barHeight =
                (fps.coerceAtMost(60) * maxBarHeight / 60) // 柱状条的高度，最大为最大高度的一半

            val x = i * (barWidth + barSpacing)
            val y = size.height - barHeight
            val rect = Rect(x, y, x + barWidth, size.height)

            val color = when (fps) {
                in 0..30 -> Color(0xfff44336)
                in 31..45 -> Color(0xffffeb3b)
                else -> Color(0xff00a2ff)
            }

            drawRect(color, topLeft = rect.topLeft, size = rect.size)
        }
    }
}

@Preview
@Composable
private fun MonitorFpsBarPreview() {
    MyTVTheme {
        MonitorFpsBar(fpsList = List(30) { it * 2 })
    }
}

@Preview
@Composable
private fun MonitorScreenPreview() {
    MyTVTheme {
        MonitorScreen()
    }
}