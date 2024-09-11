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
 * txt直播源解析
 */
class TxtIptvParser : IptvParser {

    override fun isSupport(url: String, data: String): Boolean {
        return data.contains("#genre#")
    }

    override suspend fun parse(
        data: String,
        logoProvider: (name: String, logo: String?) -> String?,
    ): ChannelGroupList = withContext(Dispatchers.Default) {
        val lines = data.split("\r\n", "\n")
        val iptvList = mutableListOf<ChannelItem>()

        var groupName: String? = null
        lines.forEach { line ->
            if (line.isBlank() || line.startsWith("#")) return@forEach

            if (line.contains("#genre#")) {
                groupName = line.split(",", "，").firstOrNull()?.trim()
            } else {
                val res = line.split(",", "，")
                if (res.size < 2) return@forEach

                iptvList.addAll(res[1].split("#").map { url ->
                    ChannelItem(
                        name = res[0].trim(),
                        epgName = res[0].trim(),
                        groupName = groupName ?: "其他",
                        url = url.trim(),
                    )
                })
            }
        }

        return@withContext ChannelGroupList(iptvList.groupBy { it.groupName }.map { groupEntry ->
            ChannelGroup(
                name = groupEntry.key,
                channelList = ChannelList(groupEntry.value.groupBy { it.name }.map { nameEntry ->
                    Channel(
                        name = nameEntry.key,
                        epgName = nameEntry.value.first().epgName,
                        lineList = ChannelLineList(
                            nameEntry.value.map { it.url }.distinct().map { ChannelLine(it) }
                        ),
                        logo = logoProvider(nameEntry.key, null),
                    )
                }),
            )
        })
    }

    private data class ChannelItem(
        val name: String,
        val epgName: String,
        val groupName: String,
        val url: String,
    )
}