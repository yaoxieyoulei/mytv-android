package top.yogiczy.mytv.tv.sync.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.RequestBody.Companion.toRequestBody
import top.yogiczy.mytv.core.data.network.request
import top.yogiczy.mytv.core.data.utils.Globals
import top.yogiczy.mytv.core.data.utils.Loggable
import top.yogiczy.mytv.tv.sync.CloudSyncDate
import java.net.URL

class GiteeGistSyncRepository(
    private val gistId: String,
    private val token: String,
) : CloudSyncRepository, Loggable("GiteeGistSyncRepository") {
    override suspend fun push(data: CloudSyncDate) = withContext(Dispatchers.IO) {
        try {
            return@withContext "https://gitee.com/api/v5/gists/${gistId}".request({ builder ->
                val body = "{ \"access_token\": \"$token\", \"files\": { \"all_configs.json\": ${
                    Globals.json.encodeToString(mapOf("content" to Globals.json.encodeToString(data)))
                }}}"

                builder
                    .addHeader("Content-Type", "application/json")
                    .patch(body.toRequestBody())
            }) { true }!!
        } catch (ex: Exception) {
            log.e("推送云端失败", ex)
            throw Exception("推送云端失败", ex)
        }
    }

    override suspend fun pull() = withContext(Dispatchers.IO) {
        try {
            return@withContext "https://gitee.com/api/v5/gists/${gistId}?access_token=${token}".request { body ->
                val res = Globals.json.parseToJsonElement(body.string()).jsonObject
                val file = res["files"]?.jsonObject?.get("all_configs.json")?.jsonObject

                file?.get("truncated")?.jsonPrimitive?.booleanOrNull?.let { nnTruncated ->
                    if (nnTruncated) {
                        file["raw_url"]?.jsonPrimitive?.content?.let { rawUrl ->
                            Globals.json.decodeFromString<CloudSyncDate>(URL(rawUrl).readText())
                        }
                    } else {
                        file["content"]?.jsonPrimitive?.content?.let {
                            Globals.json.decodeFromString<CloudSyncDate>(it)
                        }
                    }
                }?.let { syncData ->
                    res["description"]?.jsonPrimitive?.content?.let {
                        syncData.copy(description = it)
                    } ?: syncData
                }
            } ?: CloudSyncDate.EMPTY
        } catch (ex: Exception) {
            log.e("拉取云端失败", ex)
            throw Exception("拉取云端失败", ex)
        }
    }
}