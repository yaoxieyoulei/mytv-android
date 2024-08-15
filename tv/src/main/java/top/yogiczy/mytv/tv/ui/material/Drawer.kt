package top.yogiczy.mytv.tv.ui.material

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.LocalTextStyle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.theme.colors
import top.yogiczy.mytv.tv.ui.tooling.PreviewWithLayoutGrids

@Composable
fun Drawer(
    modifier: Modifier = Modifier,
    showProvider: () -> Boolean = { true },
    onDismissRequest: (() -> Unit)? = null,
    position: DrawerPosition = DrawerPosition.End,
    header: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit = {},
) {
    if (!showProvider()) return

    val alignment = when (position) {
        DrawerPosition.Start -> Alignment.TopStart
        DrawerPosition.End -> Alignment.TopEnd
        DrawerPosition.Top -> Alignment.TopStart
        DrawerPosition.Bottom -> Alignment.BottomStart
    }

    val positionModifier = when (position) {
        DrawerPosition.Start -> Modifier.fillMaxHeight()
        DrawerPosition.End -> Modifier.fillMaxHeight()
        DrawerPosition.Top -> Modifier.fillMaxWidth()
        DrawerPosition.Bottom -> Modifier.fillMaxWidth()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .pointerInput(Unit) { detectTapGestures { onDismissRequest?.invoke() } },
    ) {
        Box(
            modifier = Modifier
                .align(alignment)
                .then(positionModifier)
                .background(
                    MaterialTheme.colors.surfaceContainer,
                    MaterialTheme.shapes.large,
                )
                .padding(20.dp)
        ) {
            Column {
                header?.let { nnHeader ->
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.titleLarge
                    ) {
                        Box(
                            modifier = Modifier.padding(
                                top = 8.dp,
                                bottom = 16.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                        ) {
                            nnHeader()
                        }
                    }
                }

                content()
            }
        }
    }
}

enum class DrawerPosition {
    Start, End, Top, Bottom
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun DrawerPreviewStart() {
    MyTVTheme {
        PreviewWithLayoutGrids {
            Drawer(
                position = DrawerPosition.Start,
                header = { Text("Header") },
            )
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun DrawerPreviewEnd() {
    MyTVTheme {
        PreviewWithLayoutGrids {
            Drawer(
                position = DrawerPosition.End,
                header = { Text("Header") },
            )
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun DrawerPreviewTop() {
    MyTVTheme {
        PreviewWithLayoutGrids {
            Drawer(
                position = DrawerPosition.Top,
                header = { Text("Header") },
            )
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun DrawerPreviewBottom() {
    MyTVTheme {
        PreviewWithLayoutGrids {
            Drawer(
                position = DrawerPosition.Bottom,
                header = { Text("Header") },
            )
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun DrawerPreviewBottomNoHeader() {
    MyTVTheme {
        PreviewWithLayoutGrids {
            Drawer(
                position = DrawerPosition.Bottom,
            ) {
                Text("Content")
            }
        }
    }
}