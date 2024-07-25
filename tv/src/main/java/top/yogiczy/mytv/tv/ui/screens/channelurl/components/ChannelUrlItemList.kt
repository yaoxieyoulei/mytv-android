package top.yogiczy.mytv.tv.ui.screens.channelurl.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.distinctUntilChanged
import top.yogiczy.mytv.core.data.entities.channel.Channel
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import kotlin.math.max

@Composable
fun ChannelUrlItemList(
    modifier: Modifier = Modifier,
    urlListProvider: () -> ImmutableList<String> = { persistentListOf() },
    currentUrlProvider: () -> String = { "" },
    onSelected: (String) -> Unit = {},
    onUserAction: () -> Unit = {},
) {
    val urlList = urlListProvider()

    val listState = rememberLazyListState(max(0, urlList.indexOf(currentUrlProvider()) - 2))

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { _ -> onUserAction() }
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        itemsIndexed(urlList) { index, url ->
            ChannelUrlItem(
                urlProvider = { url },
                urlIdxProvider = { index },
                selectedProvider = { url == currentUrlProvider() },
                onSelected = { onSelected(url) },
            )
        }
    }
}

@Preview
@Composable
private fun ChannelUrlItemListPreview() {
    MyTVTheme {
        ChannelUrlItemList(
            urlListProvider = {
                Channel.EXAMPLE.urlList.toPersistentList()
            },
            currentUrlProvider = { Channel.EXAMPLE.urlList.first() },
        )
    }
}