package top.yogiczy.mytv.tv.sync.repositories

import top.yogiczy.mytv.tv.sync.CloudSyncDate

interface CloudSyncRepository {
    suspend fun push(data: CloudSyncDate): Boolean
    suspend fun pull(): CloudSyncDate
}