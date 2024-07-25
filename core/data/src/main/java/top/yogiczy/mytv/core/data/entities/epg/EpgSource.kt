package top.yogiczy.mytv.core.data.entities.epg

import kotlinx.serialization.Serializable

/**
 * 节目单来源
 */
@Serializable
data class EpgSource(
    val name: String,
    val url: String,
)