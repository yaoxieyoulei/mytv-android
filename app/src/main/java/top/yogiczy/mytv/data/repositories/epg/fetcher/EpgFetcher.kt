package top.yogiczy.mytv.data.repositories.epg.fetcher

import okhttp3.Response

/**
 * 节目单获取接口
 */
interface EpgFetcher {
    /**
     * 是否支持该格式
     */
    fun isSupport(url: String): Boolean

    /**
     * 获取节目单
     */
    fun fetch(response: Response): String

    companion object {
        val instances = listOf(
            XmlEpgFetcher(),
            XmlGzEpgFetcher(),
            DefaultEpgFetcher(),
        )
    }
}