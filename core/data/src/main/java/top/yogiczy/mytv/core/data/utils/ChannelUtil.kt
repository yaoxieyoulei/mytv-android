package top.yogiczy.mytv.core.data.utils

object ChannelUtil {
    private fun standardCctvChannelNameTest(keys: List<List<String>>): (String) -> Boolean {
        return { name: String -> keys.any { it.all { word -> word.lowercase() in name.lowercase() } } }
    }

    private val standardChannelNameTest: Map<String, (String) -> Boolean> = mapOf(
        "CCTV-5+赛事" to standardCctvChannelNameTest(
            listOf(
                listOf("cctv", "5+"),
                listOf("cctv", "5plus"),
                listOf("cctv", "体育"),
                listOf("中央", "5+"),
                listOf("中央", "五+"),
            )
        ),
        "CCTV-10科教" to standardCctvChannelNameTest(
            listOf(
                listOf("cctv", "10"),
                listOf("cctv", "科教"),
                listOf("中央", "10"),
                listOf("中央", "十"),
            )
        ),
        "CCTV-11戏曲" to standardCctvChannelNameTest(
            listOf(
                listOf("cctv", "11"),
                listOf("cctv", "戏曲"),
                listOf("中央", "11"),
                listOf("中央", "十一"),
            )
        ),
        "CCTV-12社法" to standardCctvChannelNameTest(
            listOf(
                listOf("cctv", "12"),
                listOf("cctv", "社法"),
                listOf("cctv", "法治"),
                listOf("cctv", "法制"),
                listOf("cctv", "社会与法"),
                listOf("中央", "12"),
                listOf("中央", "十二"),
            )
        ),
        "CCTV-13新闻" to standardCctvChannelNameTest(
            listOf(
                listOf("cctv", "13"),
                listOf("cctv", "新闻"),
                listOf("中央", "13"),
                listOf("中央", "十三"),
            )
        ),
        "CCTV-14少儿" to standardCctvChannelNameTest(
            listOf(
                listOf("cctv", "14"),
                listOf("cctv", "少儿"),
                listOf("中央", "14"),
                listOf("中央", "十四"),
                listOf("中央", "少儿"),
                listOf("中央", "少儿"),
            )
        ),
        "CCTV-15音乐" to standardCctvChannelNameTest(
            listOf(
                listOf("cctv", "15"),
                listOf("cctv", "音乐"),
                listOf("中央", "15"),
                listOf("中央", "十五"),
            )
        ),
        "CCTV-16奥匹" to standardCctvChannelNameTest(
            listOf(
                listOf("cctv", "16"),
                listOf("cctv", "奥林匹克"),
                listOf("中央", "16"),
                listOf("中央", "十六"),
            )
        ),
        "CCTV-17农村" to standardCctvChannelNameTest(
            listOf(
                listOf("cctv", "17"),
                listOf("cctv", "农村"),
                listOf("cctv", "农业"),
                listOf("中央", "17"),
                listOf("中央", "十七"),
            )
        ),
        "CCTV-1综合" to standardCctvChannelNameTest(
            listOf(
                listOf("cctv", "1"),
                listOf("cctv", "综合"),
                listOf("中央", "1"),
                listOf("中央", "一"),
            )
        ),
        "CCTV-2财经" to standardCctvChannelNameTest(
            listOf(
                listOf("cctv", "2"),
                listOf("cctv", "财经"),
                listOf("中央", "2"),
                listOf("中央", "二"),
            )
        ),
        "CCTV-3综艺" to standardCctvChannelNameTest(
            listOf(
                listOf("cctv", "3"),
                listOf("cctv", "综艺"),
                listOf("中央", "3"),
                listOf("中央", "三"),
            )
        ),
        "CCTV-4国际" to standardCctvChannelNameTest(
            listOf(
                listOf("cctv", "4"),
                listOf("cctv", "国际"),
                listOf("中央", "4"),
                listOf("中央", "四"),
            )
        ),
        "CCTV-5体育" to standardCctvChannelNameTest(
            listOf(
                listOf("cctv", "5"),
                listOf("cctv", "体育"),
                listOf("中央", "5"),
                listOf("中央", "五"),
            )
        ),
        "CCTV-6电影" to standardCctvChannelNameTest(
            listOf(
                listOf("cctv", "6"),
                listOf("cctv", "电影"),
                listOf("中央", "6"),
                listOf("中央", "六"),
            )
        ),
        "CCTV-7军事" to standardCctvChannelNameTest(
            listOf(
                listOf("cctv", "7"),
                listOf("cctv", "军事"),
                listOf("cctv", "国防"),
                listOf("cctv", "军农"),
                listOf("中央", "7"),
                listOf("中央", "七"),
            )
        ),
        "CCTV-8电视" to standardCctvChannelNameTest(
            listOf(
                listOf("cctv", "8"),
                listOf("cctv", "电视"),
                listOf("中央", "8"),
                listOf("中央", "八"),
            )
        ),
        "CCTV-9纪录" to standardCctvChannelNameTest(
            listOf(
                listOf("cctv", "9"),
                listOf("cctv", "纪录"),
                listOf("中央", "9"),
                listOf("中央", "九"),
            )
        ),
        "上海卫视" to { name: String ->
            name.contains("上海卫视")
                    || name.contains("东方卫视")
                    || name.contains("上海台")
                    || name.contains("上海东方卫视")
        },
        "福建卫视" to { name: String ->
            name.contains("福建卫视")
                    || name.contains("福建东南卫视")
                    || name.contains("东南卫视")
        },
    )

    private val hybridWebViewUrl = mapOf(
        "CCTV-1综合" to listOf(
            "https://tv.cctv.com/live/cctv1/",
            "https://yangshipin.cn/tv/home?pid=600001859",
        ),
        "CCTV-2财经" to listOf(
            "https://tv.cctv.com/live/cctv2/",
            "https://yangshipin.cn/tv/home?pid=600001800",
        ),
        "CCTV-3综艺" to listOf(
            "https://tv.cctv.com/live/cctv3/",
        ),
        "CCTV-4国际" to listOf(
            "https://tv.cctv.com/live/cctv4/",
            "https://yangshipin.cn/tv/home?pid=600001814",
        ),
        "CCTV-5体育" to listOf(
            "https://tv.cctv.com/live/cctv5/",
            "https://yangshipin.cn/tv/home?pid=600001818",
        ),
        "CCTV-5+赛事" to listOf(
            "https://tv.cctv.com/live/cctv5plus/",
            "https://yangshipin.cn/tv/home?pid=600001817",
        ),
        "CCTV-6电影" to listOf(
            "https://tv.cctv.com/live/cctv6/",
        ),
        "CCTV-7军事" to listOf(
            "https://tv.cctv.com/live/cctv7/",
            "https://yangshipin.cn/tv/home?pid=600004092",
        ),
        "CCTV-8电视" to listOf(
            "https://tv.cctv.com/live/cctv8/",
        ),
        "CCTV-9纪录" to listOf(
            "https://tv.cctv.com/live/cctvjilu/",
            "https://yangshipin.cn/tv/home?pid=600004078",
        ),
        "CCTV-10科教" to listOf(
            "https://tv.cctv.com/live/cctv10/",
            "https://yangshipin.cn/tv/home?pid=600001805",
        ),
        "CCTV-11戏曲" to listOf(
            "https://tv.cctv.com/live/cctv11/",
            "https://yangshipin.cn/tv/home?pid=600001806",
        ),
        "CCTV-12社法" to listOf(
            "https://tv.cctv.com/live/cctv12/",
            "https://yangshipin.cn/tv/home?pid=600001807",
        ),
        "CCTV-13新闻" to listOf(
            "https://tv.cctv.com/live/cctv13/",
            "https://yangshipin.cn/tv/home?pid=600001811",
        ),
        "CCTV-14少儿" to listOf(
            "https://tv.cctv.com/live/cctvchild/",
            "https://yangshipin.cn/tv/home?pid=600001809",
        ),
        "CCTV-15音乐" to listOf(
            "https://tv.cctv.com/live/cctv15/",
            "https://yangshipin.cn/tv/home?pid=600001815",
        ),
        "CCTV-16奥匹" to listOf(
            "https://tv.cctv.com/live/cctv16/",
            "https://yangshipin.cn/tv/home?pid=600098637",
        ),
        "CCTV-17农村" to listOf(
            "https://tv.cctv.com/live/cctv17/",
        ),
        "北京卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002309",
        ),
        "江苏卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002521",
        ),
        "上海卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002483",
        ),
        "浙江卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002520",
        ),
        "湖南卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002475",
        ),
        "湖北卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002508",
        ),
        "广东卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002485",
        ),
        "广西卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002509",
        ),
        "黑龙江卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002498",
        ),
        "海南卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002506",
        ),
        "重庆卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002531",
        ),
        "深圳卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002481",
        ),
        "四川卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002516",
        ),
        "河南卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002525",
        ),
        "福建卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002484",
        ),
        "贵州卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002490",
        ),
        "江西卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002503",
        ),
        "辽宁卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002505",
        ),
        "安徽卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002532",
        ),
        "河北卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002493",
        ),
        "山东卫视" to listOf(
            "https://yangshipin.cn/tv/home?pid=600002513",
        ),
    )

    private fun standardChannelName(name: String): String {
        return standardChannelNameTest.entries.firstOrNull { it.value.invoke(name) }?.key
            ?: name
    }

    const val HYBRID_WEB_VIEW_URL_PREFIX = "hybrid-webview://"

    fun getHybridWebViewUrl(channelName: String): List<String>? {
        return hybridWebViewUrl[standardChannelName(channelName)]?.map { "${HYBRID_WEB_VIEW_URL_PREFIX}${it}" }
    }

    fun isHybridWebViewUrl(url: String): Boolean {
        return url.startsWith(HYBRID_WEB_VIEW_URL_PREFIX)
    }

    fun getHybridWebViewUrlProvider(url: String): String {
        return if (url.contains("https://tv.cctv.com")) "央视网"
        else if (url.contains("https://yangshipin.cn")) "央视频"
        else "未知"
    }

    fun urlSupportPlayback(url: String): Boolean {
        return listOf("pltv", "PLTV", "tvod", "TVOD").any { url.contains(it) }
    }

    fun urlToCanPlayback(url: String): String {
        return url
            .replace("PLTV", "tvod")
            .replace("pltv", "tvod")
    }
}