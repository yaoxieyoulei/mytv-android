package top.yogiczy.mytv.core.data.entities.iptvsource

import kotlinx.serialization.Serializable

/**
 *  直播源
 */
@Serializable
data class IptvSource(
    /**
     * 名称
     */
    val name: String,
    /**
     * 链接
     */
    val url: String,
)