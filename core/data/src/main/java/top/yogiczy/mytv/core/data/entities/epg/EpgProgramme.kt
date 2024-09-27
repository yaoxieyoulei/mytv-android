package top.yogiczy.mytv.core.data.entities.epg

import kotlinx.serialization.Serializable
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * 频道节目
 */
@Serializable
data class EpgProgramme(
    /**
     * 开始时间（时间戳）
     */
    val startAt: Long = 0,

    /**
     * 结束时间（时间戳）
     */
    val endAt: Long = 0,

    /**
     * 节目名称
     */
    val title: String = "",
) {
    companion object {
        /**
         * 是否正在直播
         */
        fun EpgProgramme.isLive() = System.currentTimeMillis() in startAt..<endAt

        /**
         * 节目进度
         */
        fun EpgProgramme.progress(current: Long = System.currentTimeMillis()) =
            (current - startAt).toFloat() / (endAt - startAt)

        fun EpgProgramme.remainingMinutes(current: Long = System.currentTimeMillis()) =
            ceil((endAt - current) / 60_000f).roundToInt()

        val EXAMPLE = EpgProgramme(
            startAt = System.currentTimeMillis() - 3600 * 1000,
            endAt = System.currentTimeMillis() + 3600 * 1000,
            title = "节目标题",
        )
    }
}