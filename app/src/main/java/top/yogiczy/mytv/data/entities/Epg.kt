package top.yogiczy.mytv.data.entities

/**
 * 频道节目单
 */
data class Epg(
    /**
     * 频道名称
     */
    val channel: String,

    /**
     * 节目列表
     */
    val programmes: EpgProgrammeList
)
