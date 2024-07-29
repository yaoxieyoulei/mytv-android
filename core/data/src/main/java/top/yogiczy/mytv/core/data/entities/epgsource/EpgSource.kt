package top.yogiczy.mytv.core.data.entities.epgsource

import kotlinx.serialization.Serializable

/**
 * 节目单来源
 */
@Serializable
data class EpgSource(
    /**
     * 名称
     */
    val name: String = "",

    /**
     * 链接
     */
    val url: String = "",
)