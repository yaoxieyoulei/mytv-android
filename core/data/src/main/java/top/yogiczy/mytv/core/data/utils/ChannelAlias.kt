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
        val normalizedSuffixes = getNormalizedSuffixes()
        val nameWithoutSuffix =
            normalizedSuffixes.fold(name) { acc, suffix -> acc.removeSuffix(suffix) }.trim()

        return findAliasName(nameWithoutSuffix) ?: name
    }

    private fun getNormalizedSuffixes(): List<String> {
        return (_aliasMap.getOrElse("__suffix") { emptyList() } +
                defaultAlias.getOrElse("__suffix") { emptyList() })
    }

    private fun findAliasName(name: String): String? {
        return aliasMap.keys.firstOrNull { it.equals(name, ignoreCase = true) }
            ?: aliasMap.entries.firstOrNull { entry ->
                entry.value.any { it.equals(name, ignoreCase = true) }
            }?.key
            ?: defaultAlias.keys.firstOrNull { it.equals(name, ignoreCase = true) }
            ?: defaultAlias.entries.firstOrNull { entry ->
                entry.value.any { it.equals(name, ignoreCase = true) }
            }?.key
    }

    private val defaultAlias by lazy {
        Globals.json.decodeFromString<Map<String, List<String>>>(
            Globals.resources.openRawResource(R.raw.channel_name_alias).bufferedReader()
                .use { it.readText() })
    }
}
