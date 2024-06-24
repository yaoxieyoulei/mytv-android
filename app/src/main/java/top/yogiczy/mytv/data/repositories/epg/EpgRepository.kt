package top.yogiczy.mytv.data.repositories.epg

import android.util.Xml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import top.yogiczy.mytv.data.entities.Epg
import top.yogiczy.mytv.data.entities.EpgList
import top.yogiczy.mytv.data.entities.EpgProgramme
import top.yogiczy.mytv.data.entities.EpgProgrammeList
import top.yogiczy.mytv.data.repositories.FileCacheRepository
import top.yogiczy.mytv.data.repositories.epg.fetcher.EpgFetcher
import top.yogiczy.mytv.utils.Logger
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * 节目单获取
 */
class EpgRepository : FileCacheRepository("epg.json") {
    private val log = Logger.create(javaClass.simpleName)
    private val epgXmlRepository = EpgXmlRepository()

    /**
     * 解析节目单xml
     */
    private suspend fun parseFromXml(
        xmlString: String,
        filteredChannels: List<String> = emptyList(),
    ) = withContext(Dispatchers.Default) {
        val parser: XmlPullParser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(StringReader(xmlString))

        val epgMap = mutableMapOf<String, Epg>()

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (parser.name == "channel") {
                        val channelId = parser.getAttributeValue(null, "id")
                        parser.nextTag()
                        val channelName = parser.nextText()

                        if (filteredChannels.isEmpty() || filteredChannels.contains(channelName)) {
                            epgMap[channelId] = Epg(channelName, EpgProgrammeList())
                        }
                    } else if (parser.name == "programme") {
                        val channelId = parser.getAttributeValue(null, "channel")
                        val startTime = parser.getAttributeValue(null, "start")
                        val stopTime = parser.getAttributeValue(null, "stop")
                        parser.nextTag()
                        val title = parser.nextText()

                        fun parseTime(time: String): Long {
                            if (time.length < 14) return 0

                            return SimpleDateFormat("yyyyMMddHHmmss Z", Locale.getDefault()).parse(
                                time
                            )?.time ?: 0
                        }

                        if (epgMap.containsKey(channelId)) {
                            epgMap[channelId] = epgMap[channelId]!!.copy(
                                programmes = EpgProgrammeList(
                                    epgMap[channelId]!!.programmes + listOf(
                                        EpgProgramme(
                                            startAt = parseTime(startTime),
                                            endAt = parseTime(stopTime),
                                            title = title,
                                        )
                                    )
                                )
                            )
                        }
                    }
                }
            }
            eventType = parser.next()
        }

        log.i("解析节目单完成，共${epgMap.size}个频道")
        return@withContext EpgList(epgMap.values.toList())
    }

    suspend fun getEpgList(
        xmlUrl: String,
        filteredChannels: List<String> = emptyList(),
        refreshTimeThreshold: Int,
    ) = withContext(Dispatchers.Default) {
        try {
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < refreshTimeThreshold) {
                log.d("未到时间点，不刷新节目单")
                return@withContext EpgList()
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val xmlJson = getOrRefresh({ lastModified, _ ->
                dateFormat.format(System.currentTimeMillis()) != dateFormat.format(lastModified)
            }) {
                val xmlString = epgXmlRepository.getEpgXml(xmlUrl)
                Json.encodeToString(parseFromXml(xmlString, filteredChannels).value)
            }

            EpgList(Json.decodeFromString<List<Epg>>(xmlJson))
        } catch (ex: Exception) {
            log.e("获取节目单失败", ex)
            throw Exception(ex)
        }
    }
}

/**
 * 节目单xml获取
 */
private class EpgXmlRepository : FileCacheRepository("epg.xml") {
    private val log = Logger.create(javaClass.simpleName)

    /**
     * 获取远程xml
     */
    private suspend fun fetchXml(url: String): String = withContext(Dispatchers.IO) {
        log.d("获取远程节目单xml: $url")

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        try {
            with(client.newCall(request).execute()) {
                if (!isSuccessful) {
                    throw Exception("获取远程节目单xml失败: $code")
                }

                val fetcher = EpgFetcher.instances.first { it.isSupport(url) }

                return@with fetcher.fetch(this)
            }
        } catch (ex: Exception) {
            throw Exception("获取远程节目单xml失败，请检查网络连接", ex)
        }
    }

    /**
     * 获取xml
     */
    suspend fun getEpgXml(url: String): String {
        return getOrRefresh(0) {
            fetchXml(url)
        }
    }
}
