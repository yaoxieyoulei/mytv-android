package top.yogiczy.mytv.tv.ui.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.Text
import top.yogiczy.mytv.tv.ui.material.LocalPopupManager
import top.yogiczy.mytv.tv.ui.material.SimplePopup
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents

@Composable
fun SettingsListItem(
    modifier: Modifier = Modifier,
    headlineContent: String,
    supportingContent: String? = null,
    trailingContent: @Composable () -> Unit = {},
    trailingIcon: ImageVector? = null,
    onSelected: (() -> Unit)? = null,
    onLongSelected: () -> Unit = {},
    locK: Boolean = false,
    remoteConfig: Boolean = false,
) {
    val popupManager = LocalPopupManager.current
    val focusRequester = remember { FocusRequester() }

    var showPush by remember { mutableStateOf(false) }

    ListItem(
        selected = false,
        onClick = {},
        headlineContent = { Text(text = headlineContent) },
        trailingContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                trailingContent()
                trailingIcon?.let {
                    Icon(it, contentDescription = null, modifier = Modifier.size(16.dp))
                }

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
        supportingContent = { supportingContent?.let { Text(it) } },
        modifier = modifier
            .focusRequester(focusRequester)
            .handleKeyEvents(
                onSelect = {
                    if (onSelected != null) onSelected()
                    else if (remoteConfig) {
                        popupManager.push(focusRequester, true)
                        showPush = true
                    }
                },
                onLongSelect = { onLongSelected() },
            ),
    )

    SimplePopup(
        visibleProvider = { showPush },
        onDismissRequest = { showPush = false },
    ) {
        SettingsCategoryPush()
    }
}

@Composable
fun SettingsListItem(
    modifier: Modifier = Modifier,
    headlineContent: String,
    supportingContent: String? = null,
    trailingContent: String,
    trailingIcon: ImageVector? = null,
    onSelected: () -> Unit = {},
    onLongSelected: () -> Unit = {},
    locK: Boolean = false,
    remoteConfig: Boolean = false,
) {
    SettingsListItem(
        modifier = modifier,
        headlineContent = headlineContent,
        supportingContent = supportingContent,
        trailingContent = { Text(trailingContent) },
        trailingIcon = trailingIcon,
        onSelected = onSelected,
        onLongSelected = onLongSelected,
        locK = locK,
        remoteConfig = remoteConfig,
    )
}

@Preview
@Composable
private fun SettingsListItemPreview() {
    MyTVTheme {
        SettingsListItem(
            headlineContent = "关于",
            supportingContent = "版本号",
            trailingContent = "1.0.0",
            trailingIcon = Icons.Default.Circle,
            remoteConfig = true,
            locK = true,
        )
    }
}