package top.yogiczy.mytv.ui.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.handleDPadKeyEvents

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    title: String = "",
    value: String = "",
    description: String = "",
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    initialFocus: Boolean = false,
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (initialFocus) focusRequester.requestFocus()
    }

    LaunchedEffect(isFocused) {
        if (isFocused) focusRequester.requestFocus()
    }

    Card(
        modifier = modifier
            .width(190.dp)
            .height(90.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
            .handleDPadKeyEvents(
                onSelect = {
                    focusRequester.requestFocus()
                    onClick()
                },
                onLongSelect = {
                    focusRequester.requestFocus()
                    onLongClick()
                },
            ),
        scale = CardDefaults.scale(focusedScale = 1.05f),
        colors = CardDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            contentColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = MaterialTheme.colorScheme.onSurface,
            focusedContentColor = MaterialTheme.colorScheme.surface,
        ),
        onClick = {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Start)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                )

                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
                )
            }

            Text(
                text = description,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Preview
@Composable
private fun SettingsItemPreview() {
    MyTVTheme {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SettingsItem(
                title = "开机自启",
                value = "启用",
                description = "App will launch on boot App will launch on boot",
            )

            SettingsItem(
                title = "开机自启",
                value = "启用",
                description = "App will launch on boot App will launch on boot",
                initialFocus = true,
            )
        }
    }
}