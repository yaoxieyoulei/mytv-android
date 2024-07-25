package top.yogiczy.mytv.core.data.repositories.iptv.parser

import top.yogiczy.mytv.core.data.entities.channel.Channel
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroup
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList
import top.yogiczy.mytv.core.data.entities.channel.ChannelList

/**
 * 缺省直播源解析
 */
class DefaultIptvParser : IptvParser {

    override fun isSupport(url: String, data: String): Boolean {
        return true
    }

    override suspend fun parse(data: String): ChannelGroupList {
        return ChannelGroupList(
            listOf(
                ChannelGroup(
                    name = "不支持当前直播源格式",
                    channelList = ChannelList(
                        listOf(
                            Channel(name = "支持m3u", epgName = "m3u", urlList = listOf("http://1.2.3.4")),
                            Channel(name = "支持txt", epgName = "txt", urlList = listOf("http://1.2.3.4")),
                        )
                    )
                ),
                ChannelGroup(
                    name = "不支持当前直播源格式",
                    channelList = ChannelList(
                        listOf(
                            Channel(name = "支持m3u", epgName = "m3u", urlList = listOf("http://1.2.3.4")),
                            Channel(name = "支持txt", epgName = "txt", urlList = listOf("http://1.2.3.4")),
                        )
                    )
                ),
            )
        )
    }
}