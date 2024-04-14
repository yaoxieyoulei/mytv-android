package top.yogiczy.mytv.data.entities

import androidx.compose.runtime.Immutable

@Immutable
data class IptvList(
    val value: List<Iptv> = emptyList(),
) : List<Iptv> by value {
    companion object {
        val EXAMPLE = IptvList(List(10) { Iptv.EXAMPLE })
    }
}
