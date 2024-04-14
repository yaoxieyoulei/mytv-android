package top.yogiczy.mytv.data.entities

/**
 * 直播源
 */
data class Iptv(
    /**
     * 直播源名称
     */
    val name: String,

    /**
     * 频道名称，用于查询节目单
     */
    val channelName: String,

    /**
     * 播放地址
     */
    val urlList: List<String>,
) {
    companion object {
        val EMPTY = Iptv(
            name = "",
            channelName = "",
            urlList = emptyList(),
        )

        val EXAMPLE = Iptv(
            name = "CCTV-1 综合",
            channelName = "cctv1",
            urlList = emptyList(),
        )
    }
}