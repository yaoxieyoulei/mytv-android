package top.yogiczy.mytv.tv.ui.screens.classicchannel.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import kotlinx.collections.immutable.toPersistentList
import top.yogiczy.mytv.core.data.entities.epg.Epg
import top.yogiczy.mytv.core.data.entities.epg.EpgProgramme
import top.yogiczy.mytv.core.data.entities.epg.EpgProgrammeList
import top.yogiczy.mytv.core.data.entities.epg.EpgProgrammeReserveList
import top.yogiczy.mytv.tv.ui.screens.epg.components.EpgDayItemList
import top.yogiczy.mytv.tv.ui.screens.epg.components.EpgProgrammeItemList
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ClassicEpgItemList(
    modifier: Modifier = Modifier,
    epgProvider: () -> Epg? = { null },
    epgProgrammeReserveListProvider: () -> EpgProgrammeReserveList = { EpgProgrammeReserveList() },
    supportPlaybackProvider: () -> Boolean = { false },
    currentPlaybackEpgProgrammeProvider: () -> EpgProgramme? = { null },
    onEpgProgrammePlayback: (EpgProgramme) -> Unit = {},
    onEpgProgrammeReserve: (EpgProgramme) -> Unit = {},
    onUserAction: () -> Unit = {},
) {
    val epg = epgProvider() ?: return
    val dateFormat = SimpleDateFormat("E MM-dd", Locale.getDefault())
    val programDayGroup = epg.programmeList.groupBy { dateFormat.format(it.startAt) }
    var currentDay by remember { mutableStateOf(dateFormat.format(System.currentTimeMillis())) }

    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface.copy(0.7f))
            .padding(start = 12.dp, end = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        EpgProgrammeItemList(
            modifier = Modifier.width(268.dp),
            epgProgrammeListProvider = {
                EpgProgrammeList(programDayGroup.getOrElse(currentDay) { emptyList() })
            },
            epgProgrammeReserveListProvider = epgProgrammeReserveListProvider,
            supportPlaybackProvider = supportPlaybackProvider,
            currentPlaybackProvider = currentPlaybackEpgProgrammeProvider,
            onPlayback = onEpgProgrammePlayback,
            onReserve = onEpgProgrammeReserve,
            focusOnLive = false,
            onUserAction = onUserAction,
        )

        if (programDayGroup.size > 1) {
            EpgDayItemList(
                modifier = Modifier.width(80.dp),
                dayListProvider = { programDayGroup.keys.toPersistentList() },
                currentDayProvider = { currentDay },
                onDaySelected = { currentDay = it },
                onUserAction = onUserAction,
            )
        }
    }
}