package top.yogiczy.mytv.data.entities

/**
 * 当前节目/下一个节目
 */
data class EpgProgrammeCurrent(
    /**
     * 当前正在播放
     */
    val now: EpgProgramme? = null,

    /**
     * 稍后播放
     */
    val next: EpgProgramme? = null,
) {
    companion object {
        val EXAMPLE = EpgProgrammeCurrent(
            now = EpgProgramme(
                startAt = 0,
                endAt = 0,
                title = "实况录像-2023/2024赛季中国男子篮球职业联赛季后赛12进8第五场",
            ),
            next = null,
        )
    }
}