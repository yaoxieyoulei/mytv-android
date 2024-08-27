package top.yogiczy.mytv.tv.ui.screensold.videoplayercontroller.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents

@Composable
fun VideoPlayerControllerBtn(
    modifier: Modifier = Modifier,
    onSelect: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    IconButton(
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
            .handleKeyEvents(
                isFocused = { isFocused },
                focusRequester = focusRequester,
                onSelect = onSelect,
            ),
        onClick = {},
    ) {
        content()
    }
}

@Composable
fun VideoPlayerControllerBtn(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    onSelect: () -> Unit = {},
) {
    VideoPlayerControllerBtn(
        modifier = modifier,
        onSelect = onSelect,
        content = { Icon(imageVector = imageVector, contentDescription = null) },
    )
}