package top.yogiczy.mytv.data.repositories.git.parser

import top.yogiczy.mytv.data.entities.GitRelease

/**
 * git发行版解析
 */
interface GitReleaseParser {
    /**
     * 是否支持该格式
     */
    fun isSupport(url: String): Boolean

    /**
     * 解析数据
     */
    suspend fun parse(data: String): GitRelease

    companion object {
        val instances = listOf(
            GithubGitReleaseParser(),
            GiteeGitReleaseParser(),
        )
    }
}