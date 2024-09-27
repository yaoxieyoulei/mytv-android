package top.yogiczy.mytv.core.data.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yogiczy.mytv.core.data.R
import java.io.File

object ChannelAlias : Loggable("ChannelAlias") {
    val aliasFile by lazy { File(Globals.cacheDir, "channel_name_alias.json") }

    private var _aliasMap = mapOf<String, List<String>>()
    val aliasMap get() = _aliasMap

    suspend fun refresh() = withContext(Dispatchers.IO) {
        _aliasMap = runCatching {
            Globals.json.decodeFromString<Map<String, List<String>>>(aliasFile.readText())
        }.getOrElse { emptyMap() }
    }

    fun standardChannelName(name: String): String {
        val suffixList =
            _aliasMap.getOrElse("__suffix") { emptyList() } + defaultAlias.getOrElse("__suffix") { emptyList() }

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
        Globals.json.decodeFromString<Map<String, List<String>>>(
            Globals.resources.openRawResource(R.raw.channel_name_alias).bufferedReader()
                .use { it.readText() })
    }
}
