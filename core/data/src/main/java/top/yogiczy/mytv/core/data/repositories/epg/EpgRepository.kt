package top.yogiczy.mytv.core.data.repositories.epg

import android.util.Xml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import org.xmlpull.v1.XmlPullParser
import top.yogiczy.mytv.core.data.entities.epg.Epg
import top.yogiczy.mytv.core.data.entities.epg.EpgList
import top.yogiczy.mytv.core.data.entities.epg.EpgProgramme
import top.yogiczy.mytv.core.data.entities.epg.EpgProgrammeList
import top.yogiczy.mytv.core.data.entities.epgsource.EpgSource
import top.yogiczy.mytv.core.data.network.HttpException
import top.yogiczy.mytv.core.data.network.request
import top.yogiczy.mytv.core.data.repositories.FileCacheRepository
import top.yogiczy.mytv.core.data.repositories.epg.fetcher.EpgFetcher
import top.yogiczy.mytv.core.data.utils.Globals
import top.yogiczy.mytv.core.data.utils.Logger
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * 节目单获取
 */
class EpgRepository(
    source: EpgSource,
) : FileCacheRepository("epg-${source.url.hashCode().toUInt().toString(16)}.json") {
    private val log = Logger.create("EpgRepository")
    private val epgXmlRepository = EpgXmlRepository(source.url)

    /**
     * 解析节目单xml
     */
    private suspend fun parseFromXml(xmlString: String) = withContext(Dispatchers.Default) {
        fun parseTime(time: String): Long {
            if (time.length < 14) return 0

            return SimpleDateFormat("yyyyMMddHHmmss Z", Locale.getDefault()).parse(time)?.time ?: 0
        }

        data class ChannelItem(
            val id: String,
            val displayNames: MutableList<String> = mutableListOf(),
            var icon: String? = null,
        )

        data class ProgrammeItem(
            val channel: String,
            val start: Long,
            val end: Long,
            val title: String,
        )

        val parser: XmlPullParser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(StringReader(xmlString))

        var lastChannel: ChannelItem? = null
        val channelList = mutableListOf<ChannelItem>()
        val programmeList = mutableListOf<ProgrammeItem>()

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "channel" -> {
                            lastChannel = ChannelItem(parser.getAttributeValue(null, "id"))
                        }

                        "display-name" -> {
                            lastChannel?.let {
                                runCatching {
                                    val displayName = parser.nextText()
                                    lastChannel.displayNames.add(displayName)
                                }
                            }
                        }

                        "icon" -> {
                            lastChannel?.let {
                                lastChannel.icon = parser.getAttributeValue(null, "src")
                            }
                        }

                        "programme" -> {
                            val channel = parser.getAttributeValue(null, "channel")
                            if (channelList.any { it.id == channel }) {
                                val start = parser.getAttributeValue(null, "start")
                                val stop = parser.getAttributeValue(null, "stop")
                                parser.nextTag()
                                val title = parser.nextText()

                                programmeList.add(
                                    ProgrammeItem(channel, parseTime(start), parseTime(stop), title)
                                )
                            }
                        }
                    }
                }

                XmlPullParser.END_TAG -> {
                    when (parser.name) {
                        "channel" -> {
                            lastChannel?.let {
                                if (it.displayNames.isNotEmpty()) {
                                    channelList.add(it)
                                }
                            }
                        }
                    }
                }
            }
            eventType = parser.next()
        }

        val epgList = EpgList(channelList.map { channel ->
            Epg(
                channelList = channel.displayNames,
                logo = channel.icon,
                programmeList = EpgProgrammeList(programmeList
                    .filter { it.channel == channel.id }
                    .map { programme ->
                        EpgProgramme(programme.start, programme.end, programme.title)
                    }),
            )
        })

        log.i("解析节目单完成，共${epgList.size}个频道，${epgList.sumOf { it.programmeList.size }}个节目")
        return@withContext epgList
    }

    /**
     * 获取节目单列表
     */
    suspend fun getEpgList(
        refreshTimeThreshold: Int,
    ): EpgList = withContext(Dispatchers.Default) {
        try {
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < refreshTimeThreshold) {
                log.i("未到时间点，不刷新节目单")
                return@withContext EpgList()
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val xmlJson = getOrRefresh({ lastModified, cacheData ->
                dateFormat.format(System.currentTimeMillis()) != dateFormat.format(lastModified)
                        || (cacheData?.let { Globals.json.decodeFromString(it) }
                    ?: EpgList()).size == 0
            }) {
                val xmlString = epgXmlRepository.getEpgXml()
                val epgList = parseFromXml(xmlString)
                Globals.json.encodeToString(epgList)
            }

            return@withContext Globals.json.decodeFromString(xmlJson)
        } catch (ex: Exception) {
            log.e("获取节目单失败", ex)
            throw ex
        }
    }
}

/**
 * 节目单xml获取
 */
private class EpgXmlRepository(
    private val url: String
) : FileCacheRepository("epg-${url.hashCode().toUInt().toString(16)}.xml") {
    private val log = Logger.create("EpgXmlRepository")

    /**
     * 获取远程xml
     */
    private suspend fun fetchXml(): String {
        log.i("获取节目单xml: $url")

        try {
            return url.request { response, request ->
                val fetcher = EpgFetcher.instances.first { it.isSupport(request.url.toString()) }
                fetcher.fetch(response.body!!)
            }
        } catch (ex: Exception) {
            log.e("获取节目单xml失败", ex)
            throw HttpException("获取节目单xml失败，请检查网络连接", ex)
        }
    }

    /**
     * 获取xml
     */
    suspend fun getEpgXml(): String {
        return getOrRefresh(0) {
            fetchXml()
        }
    }
}
