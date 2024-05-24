package top.yogiczy.mytv.data.entities

/**
 * 直播源分组
 */
data class IptvGroup(
    /**
     * 分组名称
     */
    val name: String = "",

    /**
     * 直播源列表
     */
    val iptvList: IptvList = IptvList(),
)