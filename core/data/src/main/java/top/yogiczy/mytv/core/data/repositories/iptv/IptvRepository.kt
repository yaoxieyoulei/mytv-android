package top.yogiczy.mytv.core.data.repositories.iptv

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList
import top.yogiczy.mytv.core.data.entities.iptvsource.IptvSource
import top.yogiczy.mytv.core.data.network.await
import top.yogiczy.mytv.core.data.repositories.FileCacheRepository
import top.yogiczy.mytv.core.data.repositories.iptv.parser.IptvParser
import top.yogiczy.mytv.core.data.utils.Logger

/**
 * 直播源数据获取
 */
class IptvRepository(
    private val source: IptvSource,
) : FileCacheRepository(
    if (source.isLocal) source.url
    else "iptv-${source.url.hashCode().toUInt().toString(16)}.txt",
    source.isLocal,
) {
    private val log = Logger.create(javaClass.simpleName)

    /**
     * 获取直播源数据
     */
    private suspend fun fetchSource(sourceUrl: String): String {
        log.d("获取远程直播源: $source")

        val client = OkHttpClient()
        val request = Request.Builder().url(sourceUrl).build()

        try {
            val response = client.newCall(request).await()

            if (!response.isSuccessful) throw Exception("${response.code}: ${response.message}")

            return withContext(Dispatchers.IO) {
                response.body?.string() ?: ""
            }
        } catch (ex: Exception) {
            log.e("获取直播源失败", ex)
            throw Exception("获取直播源失败，请检查网络连接", ex)
        }
    }

    /**
     * 获取直播源分组列表
     */
    suspend fun getChannelGroupList(cacheTime: Long): ChannelGroupList {
        try {
            val sourceData = getOrRefresh(if (source.isLocal) Long.MAX_VALUE else cacheTime) {
                fetchSource(source.url)
            }

            val parser = IptvParser.instances.first { it.isSupport(source.url, sourceData) }
            val startTime = System.currentTimeMillis()
            val groupList = parser.parse(sourceData)
            log.i(
                listOf(
                    "解析直播源（${source.name}）完成：${groupList.size}个分组",
                    "${groupList.sumOf { it.channelList.size }}个频道",
                    "${groupList.sumOf { it.channelList.sumOf { channel -> channel.urlList.size } }}条线路",
                    "耗时：${System.currentTimeMillis() - startTime}ms",
                ).joinToString()
            )

            return groupList
        } catch (ex: Exception) {
            log.e("获取直播源失败", ex)
            throw Exception(ex)
        }
    }

    override suspend fun clearCache() {
        if (source.isLocal) return
        super.clearCache()
    }
}