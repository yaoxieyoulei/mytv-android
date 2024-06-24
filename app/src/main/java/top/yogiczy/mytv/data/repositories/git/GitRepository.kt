package top.yogiczy.mytv.data.repositories.git

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import top.yogiczy.mytv.data.repositories.git.parser.GitReleaseParser
import top.yogiczy.mytv.utils.Loggable

class GitRepository : Loggable() {

    suspend fun latestRelease(url: String) = withContext(Dispatchers.IO) {
        log.d("获取最新发行版: $url")

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        try {
            with(client.newCall(request).execute()) {
                if (!isSuccessful) {
                    throw Exception("获取最新发行版失败: $code")
                }

                val parser = GitReleaseParser.instances.first { it.isSupport(url) }
                return@with parser.parse(body!!.string())
            }
        } catch (ex: Exception) {
            log.e("获取最新发行版失败", ex)
            throw Exception("获取最新发行版失败，请检查网络连接", ex)
        }
    }
}