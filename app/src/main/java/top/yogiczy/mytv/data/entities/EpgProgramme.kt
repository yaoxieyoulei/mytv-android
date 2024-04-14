package top.yogiczy.mytv.data.entities

/**
 * 频道界面
 */
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
)
