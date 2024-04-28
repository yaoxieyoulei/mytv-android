package top.yogiczy.mytv.data.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import top.yogiczy.mytv.data.entities.GithubRelease
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.ui.utils.Loggable

class GithubRepository : Loggable() {
    suspend fun latestRelease() = withContext(Dispatchers.IO) {
        log.d("获取最新release: ${Constants.GITHUB_RELEASE_LATEST_URL}")

        val client = OkHttpClient()
        val request = Request.Builder().url(Constants.GITHUB_RELEASE_LATEST_URL).build()

        try {
            return@withContext with(client.newCall(request).execute()) {
                if (!isSuccessful) {
                    throw Exception("获取最新release失败: $code")
                }

                val json = JSONObject(body!!.string())

                val release = GithubRelease(
                    tagName = json["tag_name"] as String,
                    downloadUrl = (json["assets"] as JSONArray).getJSONObject(0)["browser_download_url"] as String,
                    description = json["body"] as String
                )
                log.i("最新release: ${release.tagName}")

                return@with release
            }
        } catch (e: Exception) {
            log.e("获取最新release失败", e)
            throw Exception("获取最新release失败，请检查网络连接", e.cause)
        }
    }
}