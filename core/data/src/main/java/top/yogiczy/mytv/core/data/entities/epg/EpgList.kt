package top.yogiczy.mytv.core.data.entities.epg

import androidx.compose.runtime.Immutable
import top.yogiczy.mytv.core.data.entities.channel.Channel
import top.yogiczy.mytv.core.data.entities.channel.ChannelList
import top.yogiczy.mytv.core.data.entities.epg.Epg.Companion.recentProgramme

/**
 * 频道节目单列表
 */
@Immutable
data class EpgList(
    val value: List<Epg> = emptyList(),
) : List<Epg> by value {
    companion object {
        /**
         * 当前节目/下一个节目
         */
        fun EpgList.recentProgramme(channel: Channel): EpgProgrammeRecent? {
            return firstOrNull { it.channel == channel.epgName }?.recentProgramme()
        }

        fun example(channelList: ChannelList): EpgList {
            return EpgList(channelList.map(Epg.Companion::example))
        }
    }
}