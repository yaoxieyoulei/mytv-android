package top.yogiczy.mytv.tv.ui.screen.settings.subcategories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.Text
import top.yogiczy.mytv.core.data.entities.epgsource.EpgSource
import top.yogiczy.mytv.core.data.entities.epgsource.EpgSourceList
import top.yogiczy.mytv.core.data.utils.Constants
import top.yogiczy.mytv.tv.ui.material.SimplePopup
import top.yogiczy.mytv.tv.ui.rememberChildPadding
import top.yogiczy.mytv.tv.ui.screen.components.AppScreen
import top.yogiczy.mytv.tv.ui.screen.push.PushContent
import top.yogiczy.mytv.tv.ui.screensold.settings.LocalSettings
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme
import top.yogiczy.mytv.tv.ui.utils.focusOnLaunched
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents
import top.yogiczy.mytv.tv.ui.utils.ifElse

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingsEpgSourceScreen(
    modifier: Modifier = Modifier,
    currentEpgSourceProvider: () -> EpgSource = { EpgSource() },
    epgSourceListProvider: () -> EpgSourceList = { EpgSourceList() },
    onEpgSourceSelected: (EpgSource) -> Unit = {},
    onEpgSourceDelete: (EpgSource) -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    val epgSourceList = Constants.EPG_SOURCE_LIST + epgSourceListProvider()

    val childPadding = rememberChildPadding()
    val listState = rememberLazyListState()
    val firstItemFocusRequester = remember { FocusRequester() }
    var isFirstItemFocused by remember { mutableStateOf(false) }

    AppScreen(
        modifier = modifier,
        header = { Text("设置 / 节目单 / 自定义节目单") },
        canBack = true,
        onBackPressed = onBackPressed,
    ) {
        LazyColumn(
            modifier = Modifier
                .ifElse(
                    LocalSettings.current.uiFocusOptimize,
                    Modifier.focusRestorer { firstItemFocusRequester }
                )
                .padding(top = 10.dp),
            state = listState,
            contentPadding = childPadding.copy(top = 10.dp).paddingValues,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            itemsIndexed(epgSourceList) { index, epgSource ->
                EpgSourceItem(
                    modifier = Modifier
                        .ifElse(
                            index == 0,
                            Modifier
                                .focusRequester(firstItemFocusRequester)
                                .onFocusChanged { isFirstItemFocused = it.isFocused },
                        ),
                    epgSourceProvider = { epgSource },
                    isSelectedProvider = { currentEpgSourceProvider() == epgSource },
                    onEpgSourceSelected = { onEpgSourceSelected(epgSource) },
                    onEpgSourceDelete = { onEpgSourceDelete(epgSource) },
                )
            }

            item {
                var visible by remember { mutableStateOf(false) }

                ListItem(
                    modifier = Modifier.handleKeyEvents(onSelect = { visible = true }),
                    headlineContent = { Text("添加其他节目单") },
                    selected = false,
                    onClick = {},
                )

                SimplePopup(
                    visibleProvider = { visible },
                    onDismissRequest = { visible = false },
                ) {
                    PushContent()
                }
            }
        }
    }
}

@Composable
private fun EpgSourceItem(
    modifier: Modifier = Modifier,
    epgSourceProvider: () -> EpgSource = { EpgSource() },
    isSelectedProvider: () -> Boolean = { false },
    onEpgSourceSelected: () -> Unit = {},
    onEpgSourceDelete: () -> Unit = {},
) {
    val epgSource = epgSourceProvider()
    val isSelected = isSelectedProvider()

    ListItem(
        modifier = modifier.handleKeyEvents(
            onSelect = onEpgSourceSelected,
            onLongSelect = onEpgSourceDelete,
        ),
        headlineContent = { Text(epgSource.name) },
        supportingContent = { Text(epgSource.url) },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (isSelected) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                }
            }
        },
        selected = false,
        onClick = {},
    )
}

@Preview
@Composable
private fun EpgSourceItemPreview() {
    MyTvTheme {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            EpgSourceItem(
                epgSourceProvider = { EpgSource.EXAMPLE },
            )


            EpgSourceItem(
                epgSourceProvider = { EpgSource.EXAMPLE },
                isSelectedProvider = { true },
            )

            EpgSourceItem(
                modifier = Modifier.focusOnLaunched(),
                epgSourceProvider = { EpgSourceList.EXAMPLE.first() },
                isSelectedProvider = { true },
            )
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsEpgSourceScreenPreview() {
    MyTvTheme {
        SettingsEpgSourceScreen(
            currentEpgSourceProvider = { EpgSourceList.EXAMPLE.first() },
            epgSourceListProvider = { EpgSourceList.EXAMPLE },
            onEpgSourceSelected = {},
        )
    }
}