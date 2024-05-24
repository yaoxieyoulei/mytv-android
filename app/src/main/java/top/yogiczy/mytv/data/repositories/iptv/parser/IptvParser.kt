package top.yogiczy.mytv.data.repositories.iptv.parser

import top.yogiczy.mytv.data.entities.IptvGroupList

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
    suspend fun parse(data: String): IptvGroupList

    companion object {
        val instances = listOf(
            M3uIptvParser(),
            TvboxIptvParser(),
            DefaultIptvParser(),
        )
    }
}