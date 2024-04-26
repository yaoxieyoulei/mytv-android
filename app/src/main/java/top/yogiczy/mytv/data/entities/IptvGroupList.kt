package top.yogiczy.mytv.data.entities

import androidx.compose.runtime.Immutable

@Immutable
data class IptvGroupList(
    val value: List<IptvGroup> = emptyList(),
) : List<IptvGroup> by value {
    companion object {
        val EXAMPLE = IptvGroupList(List(10) { groupIdx ->
            IptvGroup(
                name = "频道分组${groupIdx + 1}",
                iptvs = IptvList(
                    List(10) { idx ->
                        Iptv(
                            name = "频道${groupIdx + 1}-${idx + 1}",
                            channelName = "",
                            urlList = emptyList(),
                        )
                    },
                )
            )
        })

        fun IptvGroupList.iptvGroupIdx(iptv: Iptv) =
            this.indexOfFirst { group -> group.iptvs.any { it == iptv } }

        fun IptvGroupList.iptvIdx(iptv: Iptv) =
            this.flatMap { it.iptvs }.indexOfFirst { it == iptv }
    }
}