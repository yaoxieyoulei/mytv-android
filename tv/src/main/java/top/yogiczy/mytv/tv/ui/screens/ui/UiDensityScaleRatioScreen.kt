package top.yogiczy.mytv.tv.ui.screens.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.tv.ui.material.Drawer
import top.yogiczy.mytv.tv.ui.material.DrawerPosition
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.theme.colors
import top.yogiczy.mytv.tv.ui.tooling.PreviewWithLayoutGrids
import top.yogiczy.mytv.tv.ui.utils.focusOnLaunchedSaveable
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents
import top.yogiczy.mytv.tv.ui.utils.ifElse
import java.text.DecimalFormat
import kotlin.math.max

@Composable
fun UiDensityScaleRatioScreen(
    modifier: Modifier = Modifier,
    currentScaleRatioProvider: () -> Float = { 0f },
    onScaleRatioSelected: (Float) -> Unit = {},
    onClose: () -> Unit = {},
) {
    val currentScaleRatio = currentScaleRatioProvider()
    val scaleRatioList = listOf(0f) + (5..20).map { it * 0.1f }

    Drawer(
        position = DrawerPosition.Bottom,
        onDismissRequest = onClose,
        header = { Text("界面整体缩放比例") },
    ) {
        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Fixed(10),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(scaleRatioList) { index, scaleRatio ->
                UiDensityScaleRatioItem(
                    modifier = Modifier.ifElse(
                        index == max(0, scaleRatioList.indexOf(currentScaleRatio)),
                        Modifier.focusOnLaunchedSaveable(),
                    ),
                    scaleRatio = scaleRatio,
                    onSelected = { onScaleRatioSelected(scaleRatio) },
                )
            }
        }
    }
}

@Composable
private fun UiDensityScaleRatioItem(
    modifier: Modifier = Modifier,
    scaleRatio: Float,
    onSelected: () -> Unit = {},
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Card(
        onClick = {},
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                isFocused = it.isFocused || it.hasFocus
            }
            .handleKeyEvents(
                isFocused = { isFocused },
                focusRequester = focusRequester,
                onSelect = onSelected,
            ),
        colors = CardDefaults.colors(
            containerColor = MaterialTheme.colors.surfaceContainerHigh,
            focusedContainerColor = MaterialTheme.colorScheme.onSurface,
        ),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onSurface),
            ),
        ),
    ) {
        Text(
            text = when (scaleRatio) {
                0f -> "自适应"
                else -> "×${DecimalFormat("#.#").format(scaleRatio)}"
            },
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 12.dp),
        )
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun UiDensityScaleRatioScreenPreview() {
    MyTVTheme {
        PreviewWithLayoutGrids {
            UiDensityScaleRatioScreen()
        }
    }
}