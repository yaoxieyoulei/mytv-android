package top.yogiczy.mytv.data.entities

import androidx.compose.runtime.Immutable

@Immutable
data class EpgList(
    val value: List<Epg> = emptyList(),
) : List<Epg> by value {
    companion object {

        fun EpgList.currentProgrammes(iptv: Iptv): EpgProgrammeCurrent? {
            val epg = firstOrNull { it.channel == iptv.channelName } ?: return null

            val currentProgramme =
                epg.programmes.firstOrNull { it.startAt <= System.currentTimeMillis() && it.endAt >= System.currentTimeMillis() }
                    ?: return null

            return EpgProgrammeCurrent(
                now = currentProgramme,
                next = epg.programmes.indexOf(currentProgramme).let { index ->
                    if (index + 1 < epg.programmes.size) {
                        epg.programmes[index + 1]
                    } else null
                },
            )
        }
    }
}