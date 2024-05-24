package top.yogiczy.mytv.ui.screens.leanback.settings.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItemDefaults
import top.yogiczy.mytv.ui.utils.handleLeanbackKeyEvents

@Composable
fun LeanbackSettingsCategoryListItem(
    modifier: Modifier = Modifier,
    headlineContent: String,
    supportingContent: String? = null,
    trailingContent: @Composable () -> Unit = {},
    onSelected: () -> Unit = {},
    onLongSelected: () -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    androidx.tv.material3.ListItem(
        selected = false,
        onClick = { },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        ),
        headlineContent = {
            androidx.tv.material3.Text(text = headlineContent)
        },
        trailingContent = trailingContent,
        supportingContent = { supportingContent?.let { androidx.tv.material3.Text(it) } },
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
            .handleLeanbackKeyEvents(
                onSelect = {
                    if (isFocused) onSelected()
                    else focusRequester.requestFocus()
                },
                onLongSelect = {
                    if (isFocused) onLongSelected()
                    else focusRequester.requestFocus()
                },
            ),
    )
}

@Composable
fun LeanbackSettingsCategoryListItem(
    modifier: Modifier = Modifier,
    headlineContent: String,
    supportingContent: String? = null,
    trailingContent: String,
    onSelected: () -> Unit = {},
    onLongSelected: () -> Unit = {},
) {
    LeanbackSettingsCategoryListItem(
        modifier = modifier,
        headlineContent = headlineContent,
        supportingContent = supportingContent,
        trailingContent = { androidx.tv.material3.Text(trailingContent) },
        onSelected = onSelected,
        onLongSelected = onLongSelected,
    )
}