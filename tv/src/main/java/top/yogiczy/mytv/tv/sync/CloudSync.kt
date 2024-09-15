package top.yogiczy.mytv.tv.sync

import kotlinx.serialization.Serializable
import top.yogiczy.mytv.core.data.utils.Globals
import top.yogiczy.mytv.core.data.utils.Loggable
import top.yogiczy.mytv.tv.BuildConfig
import top.yogiczy.mytv.tv.sync.repositories.CloudSyncRepository
import top.yogiczy.mytv.tv.sync.repositories.GiteeGistSyncRepository
import top.yogiczy.mytv.tv.sync.repositories.GithubGistSyncRepository
import top.yogiczy.mytv.tv.sync.repositories.NetworkUrlSyncRepository
import top.yogiczy.mytv.tv.ui.utils.Configs

object CloudSync : Loggable("CloudSync") {
    private fun getRepository(): CloudSyncRepository {
        return when (Configs.cloudSyncProvider) {
            CloudSyncProvider.GITHUB_GIST -> GithubGistSyncRepository(
                Configs.cloudSyncGiteeGistId,
                Configs.cloudSyncGithubGistToken,
            )

            CloudSyncProvider.GITEE_GIST -> GiteeGistSyncRepository(
                Configs.cloudSyncGiteeGistId,
                Configs.cloudSyncGiteeGistToken,
            )

            CloudSyncProvider.NETWORK_URL -> NetworkUrlSyncRepository(
                Configs.cloudSyncNetworkUrl,
            )
        }
    }

    suspend fun push(configs: Configs.Partial): Boolean {
        log.i("推送云端数据")
        return getRepository().push(
            CloudSyncDate(
                version = BuildConfig.VERSION_NAME,
                syncAt = System.currentTimeMillis(),
                syncFrom = Globals.deviceName,
                configs = configs.desensitized()
            )
        )
    }

    suspend fun pull(): CloudSyncDate {
        log.i("拉取云端数据")
        return getRepository().pull().let {
            it.copy(configs = it.configs.desensitized())
        }
    }
}

@Serializable
data class CloudSyncDate(
    val version: String = "",
    val syncAt: Long = 0,
    val syncFrom: String = "",
    val configs: Configs.Partial = Configs.Partial(),
    val description: String? = null,
) {
    companion object {
        val EMPTY = CloudSyncDate()
    }
}

enum class CloudSyncProvider(
    val value: Int,
    val label: String,
    val supportPull: Boolean,
    val supportPush: Boolean,
) {
    GITHUB_GIST(0, "GitHub Gist", true, true),
    GITEE_GIST(1, "Gitee 代码片段", true, true),
    NETWORK_URL(2, "网络链接", true, false);

    companion object {
        fun fromValue(value: Int): CloudSyncProvider {
            return CloudSyncProvider.entries.firstOrNull { it.value == value } ?: GITHUB_GIST
        }
    }
}