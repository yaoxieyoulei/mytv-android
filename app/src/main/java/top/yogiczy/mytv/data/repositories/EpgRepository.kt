package top.yogiczy.mytv.data.repositories


import kotlinx.coroutines.flow.Flow
import top.yogiczy.mytv.data.entities.EpgList

interface EpgRepository {
    fun getEpgs(filteredChannels: List<String>): Flow<EpgList>
}