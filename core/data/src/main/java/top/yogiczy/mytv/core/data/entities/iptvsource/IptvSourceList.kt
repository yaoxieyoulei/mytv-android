package top.yogiczy.mytv.core.data.entities.iptvsource

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * 直播源列表
 */
@Serializable
@Immutable
data class IptvSourceList(
    val value: List<IptvSource> = emptyList(),
) : List<IptvSource> by value
