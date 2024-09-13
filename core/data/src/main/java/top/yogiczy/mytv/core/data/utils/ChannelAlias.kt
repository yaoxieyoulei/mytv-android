package top.yogiczy.mytv.core.data.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import top.yogiczy.mytv.core.data.R
import java.io.File

object ChannelAlias : Loggable("ChannelAlias") {
    val aliasFile by lazy { File(Globals.cacheDir, "channel_name_alias.json") }

    private var _aliasMap = mapOf<String, List<String>>()
    val aliasMap get() = _aliasMap

    suspend fun refresh() = withContext(Dispatchers.IO) {
        _aliasMap = runCatching {
            Json.decodeFromString<Map<String, List<String>>>(aliasFile.readText())
        }.getOrElse { emptyMap() }
    }

    fun standardChannelName(name: String): String {
        val suffixList =
            listOf("高码", "50-FPS", "HEVC", "HD", "高清", "IPV4", "IPV6", "「IPV4」", "「IPV6」", "-")

        val nameWithoutSuffix = suffixList.fold(name) { acc, suffix ->
            acc.removeSuffix(suffix)
                .removeSuffix(suffix.lowercase())
        }.trim()

        fun getName(aliasMap: Map<String, List<String>>): String? {
            return aliasMap.keys.firstOrNull { it.lowercase() == nameWithoutSuffix.lowercase() }
                ?: aliasMap.entries.firstOrNull { entry ->
                    entry.value.map { it.lowercase() }.contains(nameWithoutSuffix.lowercase())
                }?.key
        }

        return getName(aliasMap) ?: getName(defaultAlias) ?: name
    }

    private val defaultAlias by lazy {
        Json.decodeFromString<Map<String, List<String>>>(
            Globals.resources.openRawResource(R.raw.channel_name_alias).bufferedReader()
                .use { it.readText() })
    }
}
