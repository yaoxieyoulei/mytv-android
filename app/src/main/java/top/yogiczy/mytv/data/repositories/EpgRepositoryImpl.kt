package top.yogiczy.mytv.data.repositories

import android.content.Context
import android.util.Log
import android.util.Xml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
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
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.ui.utils.SP
import java.io.File
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Singleton

@Singleton
class EpgRepositoryImpl(private val context: Context) : EpgRepository {
    override fun getEpgs(filteredChannels: List<String>) = flow {
        if (!SP.epgEnable) {
            emit(EpgList())
            return@flow
        }

        val xml = getXml()
        val hashCode = filteredChannels.hashCode()

        if (SP.epgCacheHash == hashCode) {
            val cache = getCache()
            if (cache != null) {
                Log.d(TAG, "使用缓存epg")
                emit(cache)
                return@flow
            }
        }

        val epgList = parseFromXml(xml, filteredChannels)
        val cacheFile = getCacheFile()
        cacheFile.writeText(Json.encodeToString(epgList.value))
        SP.epgCacheHash = hashCode

        emit(epgList)
        return@flow
    }

    private suspend fun fetchXml(retry: Int = 0): String = withContext(Dispatchers.IO) {
        Log.d(TAG, "获取远程xml($retry): ${Constants.EPG_XML_URL}")

        val client = OkHttpClient()
        val request = Request.Builder().url(Constants.EPG_XML_URL).build()

        try {
            return@withContext with(client.newCall(request).execute()) {
                if (!isSuccessful) {
                    throw Exception("获取远程xml: $code")
                }

                return@with body!!.string()
            }
        } catch (e: Exception) {
            Log.e(TAG, "获取远程xml失败", e)
            if (retry < 10) {
                delay(3_000)
                return@withContext fetchXml(retry + 1)
            }
            throw Exception("获取远程EPG失败，请检查网络连接", e.cause)
        }
    }

    private fun getCacheXmlFile(): File {
        return File(context.cacheDir, "epg.xml")
    }

    private fun getCacheXml(): String {
        val file = getCacheXmlFile()
        return if (file.exists()) file.readText() else ""
    }

    private suspend fun getXml(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        if (dateFormat.format(System.currentTimeMillis()) == dateFormat.format(SP.epgXmlCacheTime)) {
            val cache = getCacheXml()
            if (cache.isNotBlank()) {
                Log.d(IptvRepositoryImpl.TAG, "使用缓存xml")
                return cache
            }
        } else {
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 1) {
                Log.d(IptvRepositoryImpl.TAG, "未到1点，不刷新epg")
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            }
        }

        val xml = fetchXml(0)
        val cacheFile = getCacheXmlFile()
        cacheFile.writeText(xml)
        SP.epgXmlCacheTime = System.currentTimeMillis()
        SP.epgCacheHash = 0

        return xml
    }

    private suspend fun parseFromXml(
        xmlString: String,
        filteredChannels: List<String> = emptyList(),
    ) = withContext(Dispatchers.IO) {
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

                            return SimpleDateFormat("yyyyMMddHHmmss Z", Locale.getDefault())
                                .parse(time)?.time ?: 0
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

        Log.d(TAG, "解析epg完成，共${epgMap.size}个频道")
        return@withContext EpgList(epgMap.values.toList())
    }

    private fun getCacheFile(): File {
        return File(context.cacheDir, "epg.json")
    }

    private fun getCache(): EpgList? {
        val file = getCacheFile()
        return if (file.exists()) {
            EpgList(Json.decodeFromString<List<Epg>>(file.readText()))
        } else {
            null
        }
    }

    companion object {
        const val TAG = "EpgRepositoryImpl"
    }
}