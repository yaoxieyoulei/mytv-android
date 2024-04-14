package top.yogiczy.mytv.data.repositories

import kotlinx.coroutines.flow.Flow
import top.yogiczy.mytv.data.entities.IptvGroupList

interface IptvRepository {
    fun getIptvGroups(): Flow<IptvGroupList>
}