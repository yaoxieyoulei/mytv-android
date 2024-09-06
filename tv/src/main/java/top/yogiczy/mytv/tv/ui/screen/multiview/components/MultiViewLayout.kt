package top.yogiczy.mytv.tv.ui.screen.multiview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.tv.ui.theme.DESIGN_HEIGHT
import top.yogiczy.mytv.tv.ui.theme.DESIGN_WIDTH
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme
import kotlin.math.ceil
import kotlin.math.sqrt

@Composable
fun MultiViewLayout(
    modifier: Modifier = Modifier,
    count: Int,
    keyList: List<Any> = emptyList(),
    content: @Composable BoxScope.(Int) -> Unit = { PreviewMultiViewLayoutItem(index = it) },
) {
    val size = ceil(sqrt(count.toFloat())).toInt()

    Box(modifier = modifier.fillMaxSize()) {
        LazyHorizontalGrid(
            modifier = Modifier.align(Alignment.Center),
            rows = GridCells.Fixed(size),
        ) {
            items(
                count,
                key = { index -> keyList.getOrElse(index) { index } },
            ) { index ->
                Box(
                    modifier = Modifier
                        .width(DESIGN_WIDTH.dp / size)
                        .height(DESIGN_HEIGHT.dp / size)
                        .padding(4.dp),
                ) {
                    CompositionLocalProvider(
                        LocalDensity provides Density(
                            density = LocalDensity.current.density / size,
                            fontScale = LocalDensity.current.fontScale,
                        ),
                    ) {
                        content(index)
                    }
                }
            }
        }
    }
}

@Composable
fun PreviewMultiViewLayoutItem(modifier: Modifier = Modifier, index: Int) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onSurface.copy(0.2f))
    ) {
        Text(
            "Screen${index + 1}",
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SplitContent1Preview() {
    MyTvTheme {
        MultiViewLayout(count = 1)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SplitContent2Preview() {
    MyTvTheme {
        MultiViewLayout(count = 2)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SplitContent3Preview() {
    MyTvTheme {
        MultiViewLayout(count = 3)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SplitContent4Preview() {
    MyTvTheme {
        MultiViewLayout(count = 4)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SplitContent5Preview() {
    MyTvTheme {
        MultiViewLayout(count = 5)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SplitContent6Preview() {
    MyTvTheme {
        MultiViewLayout(count = 6)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SplitContent7Preview() {
    MyTvTheme {
        MultiViewLayout(count = 7)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SplitContent8Preview() {
    MyTvTheme {
        MultiViewLayout(count = 8)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SplitContent9Preview() {
    MyTvTheme {
        MultiViewLayout(count = 9)
    }
}