package top.yogiczy.mytv.data.entities

import androidx.compose.runtime.Immutable

@Immutable
data class IptvGroupList(
    val value: List<IptvGroup> = emptyList(),
) : List<IptvGroup> by value {
    companion object {
        val EXAMPLE = IptvGroupList(List(10) {
            IptvGroup(
                name = "央视高清",
                iptvs = IptvList.EXAMPLE
            )
        })

        fun IptvGroupList.iptvGroupIdx(iptv: Iptv) =
            this.indexOfFirst { group -> group.iptvs.any { it.name == iptv.name } }

        fun IptvGroupList.iptvIdx(iptv: Iptv) =
            this.flatMap { it.iptvs }.indexOfFirst { it.name == iptv.name }
    }
}