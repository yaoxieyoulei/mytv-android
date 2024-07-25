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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.RadioButton
import androidx.tv.material3.Text
import top.yogiczy.mytv.core.data.utils.Constants
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.utils.focusOnLaunchedSaveable
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents
import top.yogiczy.mytv.tv.ui.utils.ifElse

@Composable
fun EpgSourceItem(
    modifier: Modifier = Modifier, epgXmlUrlProvider: () -> String = { "" },
    selectedProvider: () -> Boolean = { false },
    onSelected: () -> Unit = {},
    onDeleted: () -> Unit = {},
) {
    val epgXmlUrl = epgXmlUrlProvider()
    val selected = selectedProvider()

    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    ListItem(
        modifier = modifier
            .ifElse(selected, Modifier.focusOnLaunchedSaveable())
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
            .handleKeyEvents(
                isFocused = { isFocused },
                focusRequester = focusRequester,
                onSelect = onSelected,
                onLongSelect = onDeleted,
            ),
        selected = false,
        onClick = {},
        headlineContent = {
            Text(
                if (epgXmlUrl == Constants.EPG_XML_URL) "默认节目单" else epgXmlUrl,
                maxLines = if (isFocused) Int.MAX_VALUE else 1,
            )
        },
        trailingContent = {
            RadioButton(selected = selected, onClick = onSelected)
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
                epgXmlUrlProvider = { Constants.EPG_XML_URL },
                selectedProvider = { true },
            )
            EpgSourceItem(
                epgXmlUrlProvider = { "https://iptv-org.github.io/epg.xml" },
            )
        }
    }
}