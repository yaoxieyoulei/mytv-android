package top.yogiczy.mytv.tv.ui.screens.epg.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.distinctUntilChanged
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import kotlin.math.max

@Composable
fun EpgDayItemList(
    modifier: Modifier = Modifier,
    dayListProvider: () -> ImmutableList<String> = { persistentListOf() },
    currentDayProvider: () -> String = { "" },
    onDaySelected: (String) -> Unit = {},
    onUserAction: () -> Unit = {},
) {
    val dayList = dayListProvider()
    val currentDay = currentDayProvider()

    val listState = rememberLazyListState(max(0, dayList.indexOf(currentDay) - 2))

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { _ -> onUserAction() }
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(dayList) { day ->
            EpgDayItem(
                dayProvider = { day },
                selectedProvider = { day == currentDay },
                onDaySelected = { onDaySelected(day) },
            )
        }
    }
}

@Preview
@Composable
private fun EpgDayItemListPreview() {
    MyTVTheme {
        EpgDayItemList(
            modifier = Modifier.padding(20.dp),
            dayListProvider = {
                persistentListOf(
                    "周一 01-01",
                    "周二 02-02",
                    "周三 03-03",
                    "周四 04-04",
                    "周五 05-05",
                )
            },
            currentDayProvider = { "周二 02-02" },
        )
    }
}