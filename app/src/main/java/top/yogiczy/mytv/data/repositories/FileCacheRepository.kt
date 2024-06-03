package top.yogiczy.mytv.data.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yogiczy.mytv.AppGlobal
import java.io.File

/**
 * 用于将数据缓存至本地
 */
abstract class FileCacheRepository(
    private val fileName: String,
) {
    private fun getCacheFile() = File(AppGlobal.cacheDir, fileName)

    private suspend fun getCacheData(): String? = withContext(Dispatchers.IO) {
        val file = getCacheFile()
        if (file.exists()) file.readText()
        else null
    }

    private suspend fun setCacheData(data: String) = withContext(Dispatchers.IO) {
        val file = getCacheFile()
        file.writeText(data)
    }

    protected suspend fun getOrRefresh(cacheTime: Long, refreshOp: suspend () -> String): String {
        return getOrRefresh(
            { lastModified, _ -> System.currentTimeMillis() - lastModified >= cacheTime },
            refreshOp,
        )
    }

    fun clearCache() {
        try {
            getCacheFile().delete()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    protected suspend fun getOrRefresh(
        isExpired: (lastModified: Long, cacheData: String?) -> Boolean,
        refreshOp: suspend () -> String,
    ): String {
        var data = getCacheData()

        if (isExpired(getCacheFile().lastModified(), data)) {
            data = null
        }

        if (data.isNullOrBlank()) {
            data = refreshOp()
            setCacheData(data)
        }

        return data
    }
}