package top.yogiczy.mytv.tv.ui.screen.multiview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme
import top.yogiczy.mytv.tv.ui.utils.ifElse
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun MultiViewLayout(
    modifier: Modifier = Modifier,
    count: Int,
    keyList: List<Any> = emptyList(),
    zoomInIndex: Int? = null,
    content: @Composable BoxScope.(Int) -> Unit = { PreviewMultiViewLayoutItem(index = it) },
) {
    val gridSize = ceil(sqrt(count.toFloat())).toInt()

    Box(modifier = modifier.fillMaxSize()) {
        Layout(
            modifier = Modifier.align(Alignment.Center),
            content = {
                (0..<count).forEach { index ->
                    key(keyList.getOrElse(index) { index }) {
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .ifElse(zoomInIndex == index, Modifier.layoutId("zoomInItem")),
                        ) {
                            val scaleRatio =
                                calculateScaleRatio(count, zoomInIndex, gridSize, index)

                            CompositionLocalProvider(
                                LocalDensity provides Density(
                                    density = LocalDensity.current.density * scaleRatio,
                                    fontScale = LocalDensity.current.fontScale,
                                ),
                            ) {
                                content(index)
                            }
                        }
                    }
                }
            },
        ) { measurables, constraints ->
            val zoomInItem = measurables.find { it.layoutId == "zoomInItem" }
            if (zoomInItem == null) {
                handleGridLayout(measurables, constraints, gridSize, count)
            } else {
                handleZoomLayout(measurables, constraints, zoomInItem, count)
            }
        }
    }
}

private fun calculateScaleRatio(
    count: Int,
    zoomInIndex: Int?,
    gridSize: Int,
    index: Int
): Float {
    return if (zoomInIndex == null) {
        min(2 / 3f, 1 / gridSize.toFloat())
    } else {
        when {
            count == 9 -> if (index == zoomInIndex) 3 / 5f else 1 / 5f
            else -> if (index == zoomInIndex) 3 / 4f else 1 / 4f
        }
    }
}

private fun MeasureScope.handleGridLayout(
    measurables: List<Measurable>,
    constraints: Constraints,
    gridSize: Int,
    count: Int
): MeasureResult {
    val scale = calculateScaleRatio(count, null, gridSize, 0)
    val itemWidth = (constraints.maxWidth * scale).roundToInt()
    val itemHeight = (constraints.maxHeight * scale).roundToInt()

    val width = itemWidth * ceil(count / gridSize.toFloat()).toInt()
    val height = itemHeight * gridSize

    val placeables = measurables.map { measurable ->
        measurable.measure(
            Constraints(maxWidth = itemWidth, maxHeight = itemHeight)
        )
    }

    return layout(width, height) {
        placeables.forEachIndexed { index, placeable ->
            placeable.placeRelative(
                x = (index / gridSize) * itemWidth,
                y = (index % gridSize) * itemHeight,
            )
        }
    }
}

private fun MeasureScope.handleZoomLayout(
    measurables: List<Measurable>,
    constraints: Constraints,
    zoomInItem: Measurable,
    count: Int
): MeasureResult {
    val zoomInItemScale = calculateScaleRatio(count, 0, 0, 0)
    val zoomInItemPlaceable = zoomInItem.measure(
        Constraints(
            maxWidth = (constraints.maxWidth * zoomInItemScale).roundToInt(),
            maxHeight = (constraints.maxHeight * zoomInItemScale).roundToInt(),
        )
    )

    val itemScale = calculateScaleRatio(count, -1, 0, 0)
    val itemWidth = (constraints.maxWidth * itemScale).roundToInt()
    val itemHeight = (constraints.maxHeight * itemScale).roundToInt()

    val placeables = measurables
        .filter { it.layoutId != "zoomInItem" }
        .map { measurable ->
            measurable.measure(Constraints(maxWidth = itemWidth, maxHeight = itemHeight))
        }

    return layout(constraints.maxWidth, constraints.maxHeight) {
        if (count <= 5) {
            zoomInItemPlaceable.placeRelative(
                x = (constraints.maxWidth - zoomInItemPlaceable.width) / 2,
                y = 0,
            )
        } else if (count <= 8) {
            zoomInItemPlaceable.placeRelative(x = 0, y = 0)
        } else {
            zoomInItemPlaceable.placeRelative(
                x = (constraints.maxWidth - zoomInItemPlaceable.width) / 2,
                y = itemHeight,
            )
        }

        placeables.forEachIndexed { index, placeable ->
            if (count <= 8) {
                if (index < 4) {
                    val startX = (constraints.maxWidth - itemWidth * (min(4, count - 1))) / 2
                    placeable.placeRelative(
                        x = startX + index * itemWidth,
                        y = itemHeight * 3,
                    )
                } else {
                    placeable.placeRelative(
                        x = itemWidth * 3,
                        y = (index - 4) * itemHeight,
                    )
                }
            } else {
                if (index < 4) {
                    val startX = (constraints.maxWidth - itemWidth * 4) / 2
                    placeable.placeRelative(
                        x = startX + index * itemWidth,
                        y = 0,
                    )
                } else {
                    val startX = (constraints.maxWidth - itemWidth * 4) / 2
                    placeable.placeRelative(
                        x = startX + (index - 4) * itemWidth,
                        y = itemHeight * 4,
                    )
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
private fun MultiViewLayout1Preview() {
    MyTvTheme {
        MultiViewLayout(count = 1)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun MultiViewLayout2Preview() {
    MyTvTheme {
        MultiViewLayout(count = 2)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun MultiViewLayout3Preview() {
    MyTvTheme {
        MultiViewLayout(count = 3)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun MultiViewLayout4Preview() {
    MyTvTheme {
        MultiViewLayout(count = 4)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun MultiViewLayout5Preview() {
    MyTvTheme {
        MultiViewLayout(count = 5)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun MultiViewLayout6Preview() {
    MyTvTheme {
        MultiViewLayout(count = 6)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun MultiViewLayout7Preview() {
    MyTvTheme {
        MultiViewLayout(count = 7)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun MultiViewLayout8Preview() {
    MyTvTheme {
        MultiViewLayout(count = 8)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun MultiViewLayout9Preview() {
    MyTvTheme {
        MultiViewLayout(count = 9)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun MultiViewLayout2ScalePreview() {
    MyTvTheme {
        MultiViewLayout(count = 2, zoomInIndex = 0)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun MultiViewLayout3ScalePreview() {
    MyTvTheme {
        MultiViewLayout(count = 3, zoomInIndex = 0)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun MultiViewLayout4ScalePreview() {
    MyTvTheme {
        MultiViewLayout(count = 4, zoomInIndex = 0)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun MultiViewLayout5ScalePreview() {
    MyTvTheme {
        MultiViewLayout(count = 5, zoomInIndex = 0)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun MultiViewLayout6ScalePreview() {
    MyTvTheme {
        MultiViewLayout(count = 6, zoomInIndex = 0)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun MultiViewLayout7ScalePreview() {
    MyTvTheme {
        MultiViewLayout(count = 7, zoomInIndex = 0)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun MultiViewLayout8ScalePreview() {
    MyTvTheme {
        MultiViewLayout(count = 8, zoomInIndex = 0)
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun MultiViewLayout9ScalePreview() {
    MyTvTheme {
        MultiViewLayout(count = 9, zoomInIndex = 0)
    }
}