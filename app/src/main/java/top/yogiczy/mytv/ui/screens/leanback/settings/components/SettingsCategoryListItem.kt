package top.yogiczy.mytv.ui.screens.leanback.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Lock
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
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItemDefaults
import top.yogiczy.mytv.ui.screens.leanback.components.LeanbackQrcodeDialog
import top.yogiczy.mytv.ui.utils.HttpServer
import top.yogiczy.mytv.ui.utils.handleLeanbackKeyEvents

@Composable
fun LeanbackSettingsCategoryListItem(
    modifier: Modifier = Modifier,
    headlineContent: String,
    supportingContent: String? = null,
    trailingContent: @Composable () -> Unit = {},
    onSelected: (() -> Unit)? = null,
    onLongSelected: () -> Unit = {},
    locK: Boolean = false,
    remoteConfig: Boolean = false,
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    var showServerUrlDialog by remember { mutableStateOf(false) }

    androidx.tv.material3.ListItem(
        selected = false,
        onClick = { },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        ),
        headlineContent = {
            androidx.tv.material3.Text(text = headlineContent)
        },
        trailingContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                trailingContent()
                if (locK) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                }

                if (remoteConfig) {
                    Icon(
                        Icons.AutoMirrored.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        },
        supportingContent = { supportingContent?.let { androidx.tv.material3.Text(it) } },
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
            .handleLeanbackKeyEvents(
                onSelect = {
                    if (isFocused) {
                        if (onSelected != null) onSelected()
                        else if (remoteConfig) showServerUrlDialog = true
                    } else focusRequester.requestFocus()
                },
                onLongSelect = {
                    if (isFocused) onLongSelected()
                    else focusRequester.requestFocus()
                },
            ),
    )

    LeanbackQrcodeDialog(
        text = HttpServer.serverUrl,
        description = "扫码前往设置页面",
        showDialogProvider = { showServerUrlDialog },
        onDismissRequest = { showServerUrlDialog = false },
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
    locK: Boolean = false,
    remoteConfig: Boolean = false,
) {
    LeanbackSettingsCategoryListItem(
        modifier = modifier,
        headlineContent = headlineContent,
        supportingContent = supportingContent,
        trailingContent = { androidx.tv.material3.Text(trailingContent) },
        onSelected = onSelected,
        onLongSelected = onLongSelected,
        locK = locK,
        remoteConfig = remoteConfig,
    )
}