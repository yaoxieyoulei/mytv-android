package top.yogiczy.mytv.core.data.entities.epg

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import top.yogiczy.mytv.core.data.entities.channel.Channel
import top.yogiczy.mytv.core.data.entities.channel.ChannelList
import top.yogiczy.mytv.core.data.entities.epg.Epg.Companion.recentProgramme

/**
 * 频道节目单列表
 */
@Serializable
@Immutable
data class EpgList(
    val value: List<Epg> = emptyList(),
) : List<Epg> by value {
    companion object {
        private val matchCache =
            object : LinkedHashMap<String, Epg?>(128, 0.75f, true) {
                override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Epg?>?): Boolean {
                    return size > 1024
                }
            }

        fun EpgList.recentProgramme(channel: Channel): EpgProgrammeRecent? {
            if (isEmpty()) return null

            return match(channel)?.recentProgramme()
        }

        fun EpgList.match(channel: Channel): Epg? {
            if (isEmpty()) return null

            return matchCache.getOrPut(channel.epgName) {
                firstOrNull { epg -> epg.channel.equals(channel.epgName, ignoreCase = true) } ?: Epg()
            }
        }

        fun clearCache() {
            matchCache.clear()
        }

        fun example(channelList: ChannelList): EpgList {
            return EpgList(channelList.map(Epg.Companion::example))
        }
    }
}