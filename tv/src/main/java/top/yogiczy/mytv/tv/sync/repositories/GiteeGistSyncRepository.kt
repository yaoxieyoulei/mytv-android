package top.yogiczy.mytv.tv.sync.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import top.yogiczy.mytv.core.data.network.await
import top.yogiczy.mytv.core.data.utils.Globals
import top.yogiczy.mytv.core.data.utils.Loggable
import top.yogiczy.mytv.tv.sync.CloudSyncDate
import java.net.URL

class GiteeGistSyncRepository(
    private val gistId: String,
    private val token: String,
) : CloudSyncRepository, Loggable("GiteeGistSyncRepository") {
    override suspend fun push(data: CloudSyncDate) = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder().url("https://gitee.com/api/v5/gists/${gistId}")
            .addHeader("Content-Type", "application/json")
            .patch(
                "{ \"access_token\": \"$token\", \"files\": { \"all_configs.json\": ${
                    Globals.json.encodeToString(mapOf("content" to Globals.json.encodeToString(data)))
                }}}".toRequestBody()
            )
            .build()

        try {
            val response = client.newCall(request).await()

            if (!response.isSuccessful) throw Exception("${response.code}: ${response.message}")

            return@withContext true
        } catch (ex: Exception) {
            log.e("推送云端失败", ex)
            throw Exception("推送云端失败", ex)
        }
    }

    override suspend fun pull() = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request =
            Request.Builder().url("https://gitee.com/api/v5/gists/${gistId}?access_token=${token}")
                .build()

        try {
            val response = client.newCall(request).await()

            if (!response.isSuccessful) throw Exception("${response.code}: ${response.message}")

            return@withContext response.body?.string()?.let { body ->
                val res = Globals.json.parseToJsonElement(body).jsonObject
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