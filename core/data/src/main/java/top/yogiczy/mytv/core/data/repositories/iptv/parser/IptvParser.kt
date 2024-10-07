package top.yogiczy.mytv.core.data.repositories.iptv.parser

import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList

/**
 * 直播源数据解析接口
 */
interface IptvParser {
    /**
     * 是否支持该直播源格式
     */
    fun isSupport(url: String, data: String): Boolean

    /**
     * 解析直播源数据
     */
    suspend fun parse(data: String): ChannelGroupList

    suspend fun getEpgUrl(data: String): String? {
        return null
    }

    companion object {
        val instances = listOf(
            M3uIptvParser(),
            TxtIptvParser(),
            DefaultIptvParser(),
        )
    }
}