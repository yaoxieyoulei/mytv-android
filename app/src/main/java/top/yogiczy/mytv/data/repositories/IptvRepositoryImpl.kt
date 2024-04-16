package top.yogiczy.mytv.data.repositories

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.data.entities.IptvGroup
import top.yogiczy.mytv.data.entities.IptvGroupList
import top.yogiczy.mytv.data.entities.IptvList
import top.yogiczy.mytv.data.models.IptvResponseItem
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.ui.utils.SP
import java.io.File
import javax.inject.Singleton

@Singleton
class IptvRepositoryImpl(private val context: Context) : IptvRepository {
    override fun getIptvGroups() = flow {
        val now = System.currentTimeMillis()

        if (now - SP.iptvSourceCacheTime < 24 * 60 * 60 * 1000) {
            val cache = getCache()
            if (cache.isNotBlank()) {
                Log.d(TAG, "使用缓存直播源")
                emit(parseSource(cache))
                return@flow
            }
        }

        val data = fetchSource()

        getCacheFile().writeText(data)
        SP.iptvSourceCacheTime = now

        emit(parseSource(data))
        return@flow
    }

    private fun getSource() = SP.iptvCustomSource.ifBlank { Constants.IPTV_SOURCE_URL }

    private fun getSourceType(): SourceType {
        return if (getSource().endsWith(".m3u")) SourceType.M3U else SourceType.TVBOX
    }

    private suspend fun fetchSource(retry: Int = 0): String = withContext(Dispatchers.IO) {
        Log.d(TAG, "获取远程直播源($retry): ${getSource()}")

        val client = OkHttpClient()
        val request = Request.Builder().url(getSource()).build()

        try {
            return@withContext with(client.newCall(request).execute()) {
                if (!isSuccessful) {
                    throw Exception("获取远程直播源: $code")
                }

                return@with body!!.string()
            }
        } catch (e: Exception) {
            Log.e(TAG, "获取远程直播源失败", e)
            if (retry < 10) {
                delay(3_000)
                return@withContext fetchSource(retry + 1)
            }
            throw Exception("获取远程直播源失败，请检查网络连接", e.cause)
        }
    }

    private fun getCacheFile(): File {
        return if (getSourceType() == SourceType.M3U) File(context.cacheDir, "iptv.m3u")
        else File(context.cacheDir, "iptv-tvbox.txt")
    }

    private fun getCache(): String {
        return if (getCacheFile().exists()) getCacheFile().readText() else ""
    }

    private fun parseSourceM3u(data: String): IptvGroupList {
        val lines = data.split("\n")
        val iptvList = mutableListOf<IptvResponseItem>()

        lines.forEachIndexed { index, line ->
            if (!line.startsWith("#EXTINF")) return@forEachIndexed

            val name = line.split(",").last()
            val channelName =
                Regex("tvg-name=\"(.+?)\"").find(line)?.groupValues?.get(1) ?: name
            val groupName =
                Regex("group-title=\"(.+?)\"").find(line)?.groupValues?.get(1) ?: "其他"

            if (SP.iptvSourceSimplify) {
                if (!name.lowercase().startsWith("cctv") && !name.lowercase()
                        .endsWith("卫视")
                ) return@forEachIndexed
            }

            iptvList.add(
                IptvResponseItem(
                    name = name,
                    channelName = channelName,
                    groupName = groupName,
                    url = lines[index + 1]
                )
            )
        }

        return IptvGroupList(iptvList.groupBy { it.groupName }.map { groupEntry ->
            IptvGroup(
                name = groupEntry.key,
                iptvs = IptvList(groupEntry.value.groupBy { it.name }.map { nameEntry ->
                    Iptv(
                        name = nameEntry.key,
                        channelName = nameEntry.value.first().channelName,
                        urlList = nameEntry.value.map { it.url },
                    )
                }),
            )
        }).also {
            Log.d(
                TAG,
                "解析m3u完成: ${it.size}个分组, ${it.flatMap { group -> group.iptvs }.size}个频道"
            )
        }
    }

    private fun parseSourceTvbox(data: String): IptvGroupList {
        val lines = data.split("\n")
        val iptvList = mutableListOf<IptvResponseItem>()

        var groupName: String? = null
        lines.forEach { line ->
            if (line.isBlank() || line.startsWith("#")) return@forEach

            if (line.endsWith("#genre#")) {
                groupName = line.split(",").first()
            } else {
                val res = line.replace("，", ",").split(",")
                if (res.size < 2) return@forEach

                iptvList.add(
                    IptvResponseItem(
                        name = res[0],
                        channelName = res[0],
                        url = res[1],
                        groupName = groupName ?: "其他",
                    )
                )
            }
        }

        return IptvGroupList(iptvList.groupBy { it.groupName }.map { groupEntry ->
            IptvGroup(
                name = groupEntry.key,
                iptvs = IptvList(groupEntry.value.groupBy { it.name }.map { nameEntry ->
                    Iptv(
                        name = nameEntry.key,
                        channelName = nameEntry.value.first().channelName,
                        urlList = nameEntry.value.map { it.url },
                    )
                }),
            )
        }).also {
            Log.d(
                TAG,
                "解析tvbox完成: ${it.size}个分组, ${it.flatMap { group -> group.iptvs }.size}个频道"
            )
        }
    }

    private fun parseSource(data: String): IptvGroupList {
        try {
            return if (getSourceType() == SourceType.M3U) {
                parseSourceM3u(data)
            } else {
                parseSourceTvbox(data)
            }
        } catch (e: Exception) {
            Log.e(TAG, "解析直播源失败，请检查直播源格式", e)
            throw Exception("解析直播源失败，请检查直播源格式", e.cause)
        }
    }

    companion object {
        const val TAG = "IptvRepositoryImpl"

        enum class SourceType {
            M3U, TVBOX,
        }
    }
}