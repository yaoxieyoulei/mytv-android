package top.yogiczy.mytv.tv.ui.screens.videoplayerdiaplaymode.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.RadioButton
import androidx.tv.material3.Text
import top.yogiczy.mytv.tv.ui.screens.videoplayer.VideoPlayerDisplayMode
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.utils.focusOnLaunchedSaveable
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents
import top.yogiczy.mytv.tv.ui.utils.ifElse

@Composable
fun VideoPlayerDisplayModeItem(
    modifier: Modifier = Modifier,
    displayModeProvider: () -> VideoPlayerDisplayMode = { VideoPlayerDisplayMode.ORIGINAL },
    isSelectedProvider: () -> Boolean = { false },
    onSelected: () -> Unit = {},
) {
    val displayMode = displayModeProvider()
    val isSelected = isSelectedProvider()

    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    ListItem(
        modifier = modifier
            .ifElse(isSelected, Modifier.focusOnLaunchedSaveable())
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
            .handleKeyEvents(
                isFocused = { isFocused },
                focusRequester = focusRequester,
                onSelect = onSelected,
            ),
        selected = false,
        onClick = {},
        headlineContent = { Text(displayMode.label) },
        trailingContent = {
            RadioButton(selected = isSelected, onClick = {})
        },
    )
}

@Preview
@Composable
private fun VideoPlayerDisplayModeItemPreview() {
    MyTVTheme {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            VideoPlayerDisplayModeItem(
                displayModeProvider = { VideoPlayerDisplayMode.ORIGINAL },
                isSelectedProvider = { true },
            )

            VideoPlayerDisplayModeItem(
                displayModeProvider = { VideoPlayerDisplayMode.ORIGINAL },
            )
        }
    }
}