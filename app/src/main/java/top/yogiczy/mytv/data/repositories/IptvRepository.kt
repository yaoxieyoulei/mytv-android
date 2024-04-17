package top.yogiczy.mytv.data.repositories

import top.yogiczy.mytv.data.entities.IptvGroupList

interface IptvRepository {
    suspend fun getIptvGroups(): IptvGroupList
}