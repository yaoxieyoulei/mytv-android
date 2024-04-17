package top.yogiczy.mytv.data.repositories


import top.yogiczy.mytv.data.entities.EpgList

interface EpgRepository {
    suspend fun getEpgs(filteredChannels: List<String>): EpgList
}