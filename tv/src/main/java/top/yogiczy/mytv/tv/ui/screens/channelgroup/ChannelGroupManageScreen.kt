package top.yogiczy.mytv.tv.ui.screens.channelgroup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList
import top.yogiczy.mytv.tv.ui.material.Drawer
import top.yogiczy.mytv.tv.ui.material.DrawerPosition
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.theme.colors
import top.yogiczy.mytv.tv.ui.tooling.PreviewWithLayoutGrids
import top.yogiczy.mytv.tv.ui.utils.focusOnLaunchedSaveable
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents
import top.yogiczy.mytv.tv.ui.utils.ifElse

@Composable
fun ChannelGroupManageScreen(
    modifier: Modifier = Modifier,
    channelGroupListProvider: () -> ImmutableList<String> = { persistentListOf() },
    channelGroupHiddenListProvider: () -> ImmutableList<String> = { persistentListOf() },
    onChannelGroupHiddenListChange: (List<String>) -> Unit = {},
    onClose: () -> Unit = {},
) {
    val channelGroupList = channelGroupListProvider()

    Drawer(
        position = DrawerPosition.Bottom,
        onDismissRequest = onClose,
        header = { Text("频道分组管理") },
    ) {
        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Fixed(6),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(channelGroupList) { index, group ->
                ChannelGroupManageItem(
                    modifier = Modifier.ifElse(
                        index == 0,
                        Modifier.focusOnLaunchedSaveable(),
                    ),
                    groupProvider = { group },
                    isHiddenProvider = { channelGroupHiddenListProvider().contains(group) },
                    onToggleHidden = {
                        val channelGroupHiddenList = channelGroupHiddenListProvider()

                        onChannelGroupHiddenListChange(
                            if (channelGroupHiddenList.contains(group)) {
                                channelGroupHiddenList - group
                            } else {
                                channelGroupHiddenList + group
                            }
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun ChannelGroupManageItem(
    modifier: Modifier = Modifier,
    groupProvider: () -> String = { "" },
    isHiddenProvider: () -> Boolean = { false },
    onToggleHidden: () -> Unit = {},
) {
    val group = groupProvider()
    val isHidden = isHiddenProvider()

    var isFocused by remember { mutableStateOf(false) }

    Card(
        onClick = {},
        modifier = modifier
            .onFocusChanged {
                isFocused = it.isFocused || it.hasFocus
            }
            .handleKeyEvents(onSelect = onToggleHidden),
        colors = CardDefaults.colors(
            containerColor = MaterialTheme.colors.surfaceContainerHigh,
            focusedContainerColor = MaterialTheme.colorScheme.onSurface,
        ),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onSurface),
            ),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = group,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .fillMaxWidth(0.8f)
                    .ifElse(isFocused, Modifier.basicMarquee()),
            )

            if (isHidden) {
                Icon(
                    Icons.Default.VisibilityOff,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.typography.bodyMedium.fontSize.value.dp),
                )
            }
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun ChannelGroupManageScreenPreview() {
    MyTVTheme {
        PreviewWithLayoutGrids {
            ChannelGroupManageScreen(
                channelGroupListProvider = {
                    ChannelGroupList.EXAMPLE.map { it.name }.let {
                        it + "•央视「IPV6」"
                    }.toPersistentList()
                },
                channelGroupHiddenListProvider = {
                    persistentListOf(
                        "频道分组1",
                        "频道分组3",
                        "频道分组5",
                        "频道分组6",
                        "•央视「IPV6」",
                    )
                },
            )
        }
    }
}