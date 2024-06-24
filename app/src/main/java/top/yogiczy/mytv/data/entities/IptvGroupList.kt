package top.yogiczy.mytv.data.entities

import androidx.compose.runtime.Immutable

/**
 * 直播源分组列表
 */
@Immutable
data class IptvGroupList(
    val value: List<IptvGroup> = emptyList(),
) : List<IptvGroup> by value {
    companion object {
        val EXAMPLE = IptvGroupList(List(5) { groupIdx ->
            IptvGroup(
                name = "频道分组${groupIdx + 1}",
                iptvList = IptvList(
                    List(10) { idx ->
                        Iptv(
                            name = "频道${groupIdx + 1}-${idx + 1}",
                            channelName = "频道${groupIdx + 1}-${idx + 1}",
                            urlList = emptyList(),
                        )
                    },
                )
            )
        })

        fun IptvGroupList.iptvGroupIdx(iptv: Iptv) =
            this.indexOfFirst { group -> group.iptvList.any { it == iptv } }

        fun IptvGroupList.iptvIdx(iptv: Iptv) =
            this.flatMap { it.iptvList }.indexOfFirst { it == iptv }

        val IptvGroupList.iptvList: List<Iptv>
            get() = this.flatMap { it.iptvList }
    }
}