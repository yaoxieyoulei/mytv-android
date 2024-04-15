package top.yogiczy.mytv.data.entities

import kotlinx.serialization.Serializable

/**
 * 频道节目单
 */
@Serializable
data class Epg(
    /**
     * 频道名称
     */
    val channel: String,

    /**
     * 节目列表
     */
    val programmes: EpgProgrammeList,
)
