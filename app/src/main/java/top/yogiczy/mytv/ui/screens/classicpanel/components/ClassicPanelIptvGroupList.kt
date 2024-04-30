package top.yogiczy.mytv.ui.screens.classicpanel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyListState
import androidx.tv.foundation.lazy.list.itemsIndexed
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.ListItem
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.data.entities.IptvGroup
import top.yogiczy.mytv.data.entities.IptvGroupList
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.focusOnInitSaveable
import top.yogiczy.mytv.ui.utils.handleDPadKeyEvents
import kotlin.math.max

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ClassicPanelIptvGroupList(
    modifier: Modifier = Modifier,
    iptvGroupList: IptvGroupList = IptvGroupList(),
    currentIptvGroup: IptvGroup = IptvGroup.EMPTY,
    currentIptvGroupIdx: Int = max(0, iptvGroupList.indexOf(currentIptvGroup)),
    onChangeFocused: (IptvGroup) -> Unit = {},
    state: TvLazyListState = rememberTvLazyListState(currentIptvGroupIdx),
    exitFocusRequester: FocusRequester = FocusRequester.Cancel,
    onEnter: () -> Unit = {},
    onExit: () -> Unit = {},
) {
    val childPadding = rememberChildPadding()
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier.width(200.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = "频道分类", style = MaterialTheme.typography.titleMedium)

        TvLazyColumn(
            state = state,
            contentPadding = PaddingValues(top = 8.dp, bottom = childPadding.bottom),
            modifier = Modifier
                .focusRequester(focusRequester)
                .focusProperties {
                    exit = {
                        onExit()
                        focusRequester.saveFocusedChild()
                        exitFocusRequester
                    }
                    enter = {
                        onEnter()
                        if (focusRequester.restoreFocusedChild()) FocusRequester.Cancel
                        else FocusRequester.Default
                    }
                },
        ) {
            itemsIndexed(iptvGroupList) { index, group ->
                val itemFocusRequester = remember { FocusRequester() }

                ListItem(
                    modifier = Modifier
                        .focusRequester(itemFocusRequester)
                        .focusOnInitSaveable(index == currentIptvGroupIdx)
                        .onFocusChanged { if (it.isFocused || it.hasFocus) onChangeFocused(group) }
                        .handleDPadKeyEvents(onSelect = { itemFocusRequester.requestFocus() }),
                    selected = index == currentIptvGroupIdx,
                    onClick = { },
                    headlineContent = { Text(text = group.name, maxLines = 2) },
                    trailingContent = {
                        Text(
                            text = "${group.iptvs.size}个频道",
                            color = LocalContentColor.current.copy(0.8f),
                        )
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun ClassicPanelIptvGroupListPreview() {
    MyTVTheme {
        ClassicPanelIptvGroupList(iptvGroupList = IptvGroupList.EXAMPLE)
    }
}