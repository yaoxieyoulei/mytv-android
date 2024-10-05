package top.yogiczy.mytv.core.data.repositories.iptv.parser

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yogiczy.mytv.core.data.entities.channel.Channel
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroup
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList
import top.yogiczy.mytv.core.data.entities.channel.ChannelLine
import top.yogiczy.mytv.core.data.entities.channel.ChannelLineList
import top.yogiczy.mytv.core.data.entities.channel.ChannelList

/**
 * m3u直播源解析
 */
class M3uIptvParser : IptvParser {

    override fun isSupport(url: String, data: String): Boolean {
        return data.startsWith("#EXTM3U")
    }

    override suspend fun parse(
        data: String,
        logoProvider: (name: String, logo: String?) -> String?,
    ): ChannelGroupList =
        withContext(Dispatchers.Default) {
            val lines = data.split("\r\n", "\n")
            val iptvList = mutableListOf<ChannelItem>()

            lines.forEachIndexed { index, line ->
                if (!line.startsWith("#EXTINF")) return@forEachIndexed

                val name = line.split(",").last().trim()
                val channelName =
                    Regex("tvg-name=\"(.+?)\"").find(line)?.groupValues?.get(1)?.trim()
                        ?: name
                val groupNames =
                    Regex("group-title=\"(.+?)\"").find(line)?.groupValues?.get(1)?.split(";")
                        ?.map { it.trim() }
                        ?: listOf("其他")
                val logo = Regex("tvg-logo=\"(.+?)\"").find(line)?.groupValues?.get(1)?.trim()
                val httpUserAgent =
                    Regex("http-user-agent=\"(.+?)\"").find(line)?.groupValues?.get(1)?.trim()
                val url = lines.getOrNull(index + 1)?.trim()

                url?.let {
                    iptvList.addAll(
                        groupNames.map { groupName ->
                            ChannelItem(
                                name = name,
                                epgName = channelName,
                                groupName = groupName,
                                url = url,
                                logo = logo,
                                httpUserAgent = httpUserAgent,
                            )
                        }
                    )
                }
            }

            return@withContext ChannelGroupList(iptvList.groupBy { it.groupName }
                .map { (groupName, channelList) ->
                    ChannelGroup(
                        name = groupName,
                        channelList = ChannelList(channelList.groupBy { it.name }
                            .map { (channelName, channelList) ->
                                Channel(
                                    name = channelName,
                                    epgName = channelList.first().epgName,
                                    lineList = ChannelLineList(
                                        channelList.distinctBy { it.url }
                                            .map {
                                                ChannelLine(
                                                    url = it.url,
                                                    httpUserAgent = it.httpUserAgent,
                                                )
                                            }
                                    ),
                                    logo = logoProvider(channelName, channelList.first().logo),
                                )
                            })
                    )
                })
        }

    override suspend fun getEpgUrl(data: String): String? {
        val lines = data.split("\r\n", "\n")
        return lines.firstOrNull { it.startsWith("#EXTM3U") }?.let { defLine ->
            Regex("x-tvg-url=\"(.+?)\"").find(defLine)?.groupValues?.get(1)
                ?.split(",")
                ?.firstOrNull()
                ?.trim()
        }
    }

    private data class ChannelItem(
        val name: String,
        val epgName: String,
        val groupName: String,
        val url: String,
        val logo: String?,
        val httpUserAgent: String?,
    )
}