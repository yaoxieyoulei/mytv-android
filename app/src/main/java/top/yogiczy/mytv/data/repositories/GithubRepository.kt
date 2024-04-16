package top.yogiczy.mytv.data.repositories

import top.yogiczy.mytv.data.entities.GithubRelease

interface GithubRepository {
    suspend fun latestRelease(): GithubRelease
}