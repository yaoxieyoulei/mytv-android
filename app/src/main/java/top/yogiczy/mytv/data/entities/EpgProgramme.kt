package top.yogiczy.mytv.data.entities

import kotlinx.serialization.Serializable

/**
 * 频道节目
 */
@Serializable
data class EpgProgramme(
    /**
     * 开始时间（时间戳）
     */
    val startAt: Long,

    /**
     * 结束时间（时间戳）
     */
    val endAt: Long,

    /**
     * 节目名称
     */
    val title: String,
) {
    companion object {
        fun EpgProgramme.isLive() = System.currentTimeMillis() in startAt..<endAt
    }
}