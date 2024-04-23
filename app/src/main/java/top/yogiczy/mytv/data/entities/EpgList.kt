package top.yogiczy.mytv.data.entities

import androidx.compose.runtime.Immutable
import top.yogiczy.mytv.data.entities.Epg.Companion.currentProgrammes

@Immutable
data class EpgList(
    val value: List<Epg> = emptyList(),
) : List<Epg> by value {
    companion object {

        fun EpgList.currentProgrammes(iptv: Iptv): EpgProgrammeCurrent? {
            return firstOrNull { it.channel == iptv.channelName }?.currentProgrammes()
        }
    }
}