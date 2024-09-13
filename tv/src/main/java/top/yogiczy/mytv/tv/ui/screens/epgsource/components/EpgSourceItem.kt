package top.yogiczy.mytv.tv.ui.screens.epgsource.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.RadioButton
import androidx.tv.material3.Text
import top.yogiczy.mytv.core.data.entities.epgsource.EpgSource
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.utils.focusOnLaunchedSaveable
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents
import top.yogiczy.mytv.tv.ui.utils.ifElse

@Composable
fun EpgSourceItem(
    modifier: Modifier = Modifier,
    epgSourceProvider: () -> EpgSource,
    isSelectedProvider: () -> Boolean = { false },
    onSelected: () -> Unit = {},
    onDeleted: () -> Unit = {},
) {
    val epgSource = epgSourceProvider()
    val isSelected = isSelectedProvider()

    var isFocused by remember { mutableStateOf(false) }

    ListItem(
        modifier = modifier
            .ifElse(isSelected, Modifier.focusOnLaunchedSaveable())
            .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
            .handleKeyEvents(
                onSelect = onSelected,
                onLongSelect = onDeleted,
            ),
        selected = false,
        onClick = {},
        headlineContent = { Text(epgSource.name) },
        supportingContent = {
            Text(
                epgSource.url,
                maxLines = if (isFocused) Int.MAX_VALUE else 1,
            )
        },
        trailingContent = {
            RadioButton(selected = isSelected, onClick = onSelected)
        },
    )
}

@Preview
@Composable
private fun EpgSourceItemPreview() {
    MyTVTheme {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            EpgSourceItem(
                epgSourceProvider = {
                    EpgSource(
                        name = "EPG源1", url = "https://iptv-org.github.io/epg.xml"
                    )
                },
                isSelectedProvider = { true },
            )
            EpgSourceItem(
                epgSourceProvider = {
                    EpgSource(
                        name = "EPG源1", url = "https://iptv-org.github.io/epg.xml"
                    )
                },
            )
        }
    }
}