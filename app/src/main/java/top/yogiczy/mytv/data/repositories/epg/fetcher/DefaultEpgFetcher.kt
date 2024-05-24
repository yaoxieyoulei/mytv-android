package top.yogiczy.mytv.data.repositories.epg.fetcher

import okhttp3.Response

class DefaultEpgFetcher : EpgFetcher {
    override fun isSupport(url: String): Boolean {
        return true
    }

    override fun fetch(response: Response): String {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
    }
}