package top.yogiczy.mytv.data.entities

import androidx.compose.runtime.Immutable

@Immutable
data class EpgList(
    val value: List<Epg> = emptyList(),
) : List<Epg> by value {
    companion object {

        fun EpgList.currentProgrammes(iptv: Iptv): Pair<String?, String?> {
            val epg = firstOrNull { it.channel == iptv.channelName } ?: return Pair(null, null)

            val currentProgramme =
                epg.programmes.firstOrNull { it.startAt <= System.currentTimeMillis() && it.endAt >= System.currentTimeMillis() }
                    ?: return Pair(null, null)

            return Pair(
                currentProgramme.title,
                epg.programmes.indexOf(currentProgramme).let { index ->
                    if (index + 1 < epg.programmes.size) epg.programmes[index + 1].title else null
                },
            )
        }
    }
}