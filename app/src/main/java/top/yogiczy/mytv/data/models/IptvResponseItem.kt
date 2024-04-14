package top.yogiczy.mytv.data.models

data class IptvResponseItem(
    /**
     * 直播源名称
     */
    val name: String,

    /**
     * 频道名称，用于查询节目单
     */
    val channelName: String,

    /**
     * 分组名称
     */
    val groupName: String,

    /**
     * 播放地址
     */
    val url: String,
)
