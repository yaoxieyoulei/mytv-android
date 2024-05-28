package top.yogiczy.mytv.ui.screens.leanback.classicpanel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import androidx.tv.material3.ListItemDefaults
import kotlinx.coroutines.flow.distinctUntilChanged
import top.yogiczy.mytv.data.entities.IptvGroup
import top.yogiczy.mytv.data.entities.IptvGroupList
import top.yogiczy.mytv.ui.rememberLeanbackChildPadding
import top.yogiczy.mytv.ui.theme.LeanbackTheme
import top.yogiczy.mytv.ui.utils.handleLeanbackKeyEvents
import kotlin.math.max

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LeanbackClassicPanelIptvGroupList(
    modifier: Modifier = Modifier,
    iptvGroupListProvider: () -> IptvGroupList = { IptvGroupList() },
    initialIptvGroupProvider: () -> IptvGroup = { IptvGroup() },
    exitFocusRequesterProvider: () -> FocusRequester = { FocusRequester.Default },
    onIptvGroupFocused: (IptvGroup) -> Unit = {},
    onFocusEnter: () -> Unit = {},
    onFocusExit: () -> Unit = {},
    onUserAction: () -> Unit = {},
) {
    val iptvGroupList = iptvGroupListProvider()
    val initialIptvGroup = initialIptvGroupProvider()

    val childPadding = rememberLeanbackChildPadding()
    val focusRequester = remember { FocusRequester() }
    var hasFocused by remember { mutableStateOf(false) }
    var focusedIptvGroup by remember { mutableStateOf(initialIptvGroup) }

    val listState =
        rememberTvLazyListState(
            max(0, iptvGroupList.indexOf(initialIptvGroup) - 2)
        )

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { _ -> onUserAction() }
    }

    Column(
        modifier = modifier.width(200.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = "频道分类", style = MaterialTheme.typography.titleMedium)

        TvLazyColumn(
            state = listState,
            contentPadding = PaddingValues(top = 8.dp, bottom = childPadding.bottom),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .focusRequester(focusRequester)
                .focusProperties {
                    exit = {
                        onFocusExit()
                        focusRequester.saveFocusedChild()
                        exitFocusRequesterProvider()
                    }
                    enter = {
                        onFocusEnter()
                        if (focusRequester.restoreFocusedChild()) FocusRequester.Cancel
                        else FocusRequester.Default
                    }
                },
        ) {
            items(iptvGroupList) { iptvGroup ->
                val isSelected by remember { derivedStateOf { iptvGroup == focusedIptvGroup } }
                val initialFocused by remember {
                    derivedStateOf { !hasFocused && iptvGroup == initialIptvGroup }
                }

                LeanbackClassicPanelIptvGroupItem(
                    iptvGroupProvider = { iptvGroup },
                    isSelectedProvider = { isSelected },
                    initialFocusedProvider = { initialFocused },
                    onInitialFocused = { hasFocused = true },
                    onFocused = {
                        focusedIptvGroup = it
                        onIptvGroupFocused(it)
                    },
                )
            }
        }
    }
}

@Composable
private fun LeanbackClassicPanelIptvGroupItem(
    modifier: Modifier = Modifier,
    iptvGroupProvider: () -> IptvGroup = { IptvGroup() },
    isSelectedProvider: () -> Boolean = { false },
    initialFocusedProvider: () -> Boolean = { false },
    onInitialFocused: () -> Unit = {},
    onFocused: (IptvGroup) -> Unit = {},
) {
    val iptvGroup = iptvGroupProvider()

    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (initialFocusedProvider()) {
            onInitialFocused()
            focusRequester.requestFocus()
        }
    }

    CompositionLocalProvider(
        LocalContentColor provides if (isFocused) MaterialTheme.colorScheme.background
        else MaterialTheme.colorScheme.onBackground
    ) {
        androidx.tv.material3.ListItem(
            modifier = modifier
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused = it.isFocused || it.hasFocus

                    if (isFocused) {
                        onFocused(iptvGroup)
                    }
                }
                .handleLeanbackKeyEvents(
                    onSelect = {
                        focusRequester.requestFocus()
                    },
                ),
            colors = ListItemDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.onBackground,
                selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.5f
                ),
            ),
            selected = isSelectedProvider(),
            onClick = { },
            headlineContent = {
                Text(
                    text = iptvGroup.name,
                    maxLines = 2
                )
            },
            trailingContent = {
                Text(
                    text = "${iptvGroup.iptvList.size}个频道",
                    color = LocalContentColor.current.copy(0.8f),
                )
            },
        )
    }
}

@Preview
@Composable
private fun LeanbackClassicPanelIptvGroupListPreview() {
    LeanbackTheme {
        LeanbackClassicPanelIptvGroupList(
            modifier = Modifier.padding(20.dp),
            iptvGroupListProvider = { IptvGroupList.EXAMPLE },
        )
    }
}