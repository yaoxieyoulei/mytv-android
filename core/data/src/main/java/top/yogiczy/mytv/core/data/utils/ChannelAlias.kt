package top.yogiczy.mytv.core.data.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

object ChannelAlias : Loggable("ChannelAlias") {
    val aliasFile by lazy { File(Globals.cacheDir, "channel_name_alias.json") }

    private var _aliasMap = mapOf<String, List<String>>()
    val aliasMap get() = _aliasMap

    suspend fun refresh() = withContext(Dispatchers.IO) {
        _aliasMap = runCatching {
            Json.decodeFromString<Map<String, List<String>>>(aliasFile.readText())
        }.getOrElse { emptyMap() }
    }

    fun standardChannelName(name: String): String {
        return aliasMap.entries.firstOrNull { entry ->
            (entry.value + entry.key).map { it.lowercase() }.contains(name.lowercase())
        }?.key
            ?: defaultAlias.entries.firstOrNull { entry ->
                (entry.value + entry.key).map { it.lowercase() }.contains(name.lowercase())
            }?.key
            ?: name
    }

    private fun aliasGen(vararg keys: String): List<String> {
        if (keys.isEmpty()) return emptyList()

        val separators = listOf("", " ", "-")
        val result = mutableListOf(keys.first())

        for (index in 1 until keys.size) {
            val newResult = mutableListOf<String>()
            for (alias in result) {
                for (separator in separators) {
                    newResult.add(alias + separator + keys[index])
                }
            }
            result.clear()
            result.addAll(newResult)
        }

        return result
    }

    private val defaultAlias by lazy {
        mapOf(
            "CCTV1" to aliasGen("cctv", "1")
                    + aliasGen("cctv", "1", "hd")
                    + aliasGen("cctv", "1", "高清")
                    + aliasGen("cctv", "1", "综合")
                    + aliasGen("cctv", "1", "综合", "HD")
                    + aliasGen("cctv", "1", "综合", "高清")
                    + aliasGen("cctv", "1", "高码")
                    + aliasGen("cctv", "1", "50", "fps")
                    + aliasGen("cctv", "1", "hevc"),

            "CCTV2" to aliasGen("cctv", "2")
                    + aliasGen("cctv", "2", "hd")
                    + aliasGen("cctv", "2", "高清")
                    + aliasGen("cctv", "2", "财经")
                    + aliasGen("cctv", "2", "财经", "HD")
                    + aliasGen("cctv", "2", "财经", "高清")
                    + aliasGen("cctv", "2", "高码")
                    + aliasGen("cctv", "2", "50", "fps")
                    + aliasGen("cctv", "2", "hevc"),

            "CCTV3" to aliasGen("cctv", "3")
                    + aliasGen("cctv", "3", "hd")
                    + aliasGen("cctv", "3", "高清")
                    + aliasGen("cctv", "3", "综艺")
                    + aliasGen("cctv", "3", "综艺", "HD")
                    + aliasGen("cctv", "3", "综艺", "高清")
                    + aliasGen("cctv", "3", "高码")
                    + aliasGen("cctv", "3", "50", "fps")
                    + aliasGen("cctv", "3", "hevc"),

            "CCTV4" to aliasGen("cctv", "4")
                    + aliasGen("cctv", "4", "hd")
                    + aliasGen("cctv", "4", "高清")
                    + aliasGen("cctv", "4", "国际")
                    + aliasGen("cctv", "4", "国际", "HD")
                    + aliasGen("cctv", "4", "国际", "高清")
                    + aliasGen("cctv", "4", "中文国际")
                    + aliasGen("cctv", "4", "中文国际", "HD")
                    + aliasGen("cctv", "4", "中文国际", "高清")
                    + aliasGen("cctv", "4", "高码")
                    + aliasGen("cctv", "4", "50", "fps")
                    + aliasGen("cctv", "4", "hevc"),

            "CCTV5" to aliasGen("cctv", "5")
                    + aliasGen("cctv", "5", "hd")
                    + aliasGen("cctv", "5", "高清")
                    + aliasGen("cctv", "5", "体育")
                    + aliasGen("cctv", "5", "体育", "HD")
                    + aliasGen("cctv", "5", "体育", "高清")
                    + aliasGen("cctv", "5", "高码")
                    + aliasGen("cctv", "5", "50", "fps")
                    + aliasGen("cctv", "5", "hevc"),

            "CCTV5+" to aliasGen("cctv", "5", "+")
                    + aliasGen("cctv", "5", "+", "hd")
                    + aliasGen("cctv", "5", "+", "高清")
                    + aliasGen("cctv", "5", "+", "体育赛事")
                    + aliasGen("cctv", "5", "+", "体育赛事", "HD")
                    + aliasGen("cctv", "5", "+", "体育赛事", "高清")
                    + aliasGen("cctv", "5", "+", "高码")
                    + aliasGen("cctv", "5", "+", "50", "fps")
                    + aliasGen("cctv", "5", "+", "hevc"),

            "CCTV6" to aliasGen("cctv", "6")
                    + aliasGen("cctv", "6", "hd")
                    + aliasGen("cctv", "6", "高清")
                    + aliasGen("cctv", "6", "电影")
                    + aliasGen("cctv", "6", "电影", "HD")
                    + aliasGen("cctv", "6", "电影", "高清")
                    + aliasGen("cctv", "6", "高码")
                    + aliasGen("cctv", "6", "50", "fps")
                    + aliasGen("cctv", "6", "hevc"),

            "CCTV7" to aliasGen("cctv", "7")
                    + aliasGen("cctv", "7", "hd")
                    + aliasGen("cctv", "7", "高清")
                    + aliasGen("cctv", "7", "国防军事")
                    + aliasGen("cctv", "7", "国防军事", "HD")
                    + aliasGen("cctv", "7", "国防军事", "高清")
                    + aliasGen("cctv", "7", "高码")
                    + aliasGen("cctv", "7", "50", "fps")
                    + aliasGen("cctv", "7", "hevc"),

            "CCTV8" to aliasGen("cctv", "8")
                    + aliasGen("cctv", "8", "hd")
                    + aliasGen("cctv", "8", "高清")
                    + aliasGen("cctv", "8", "电视剧")
                    + aliasGen("cctv", "8", "电视剧", "HD")
                    + aliasGen("cctv", "8", "电视剧", "高清")
                    + aliasGen("cctv", "8", "高码")
                    + aliasGen("cctv", "8", "50", "fps")
                    + aliasGen("cctv", "8", "hevc"),

            "CCTV9" to aliasGen("cctv", "9")
                    + aliasGen("cctv", "9", "hd")
                    + aliasGen("cctv", "9", "高清")
                    + aliasGen("cctv", "9", "纪录")
                    + aliasGen("cctv", "9", "纪录", "HD")
                    + aliasGen("cctv", "9", "纪录", "高清")
                    + aliasGen("cctv", "9", "高码")
                    + aliasGen("cctv", "9", "50", "fps")
                    + aliasGen("cctv", "9", "hevc"),

            "CCTV10" to aliasGen("cctv", "10")
                    + aliasGen("cctv", "10", "hd")
                    + aliasGen("cctv", "10", "高清")
                    + aliasGen("cctv", "10", "科教")
                    + aliasGen("cctv", "10", "科教", "HD")
                    + aliasGen("cctv", "10", "科教", "高清")
                    + aliasGen("cctv", "10", "高码")
                    + aliasGen("cctv", "10", "50", "fps")
                    + aliasGen("cctv", "10", "hevc"),

            "CCTV11" to aliasGen("cctv", "11")
                    + aliasGen("cctv", "11", "hd")
                    + aliasGen("cctv", "11", "高清")
                    + aliasGen("cctv", "11", "戏曲")
                    + aliasGen("cctv", "11", "戏曲", "HD")
                    + aliasGen("cctv", "11", "戏曲", "高清")
                    + aliasGen("cctv", "11", "高码")
                    + aliasGen("cctv", "11", "50", "fps")
                    + aliasGen("cctv", "11", "hevc"),

            "CCTV12" to aliasGen("cctv", "12")
                    + aliasGen("cctv", "12", "hd")
                    + aliasGen("cctv", "12", "高清")
                    + aliasGen("cctv", "12", "社会与法")
                    + aliasGen("cctv", "12", "社会与法", "HD")
                    + aliasGen("cctv", "12", "社会与法", "高清")
                    + aliasGen("cctv", "12", "高码")
                    + aliasGen("cctv", "12", "50", "fps")
                    + aliasGen("cctv", "12", "hevc"),

            "CCTV13" to aliasGen("cctv", "13")
                    + aliasGen("cctv", "13", "hd")
                    + aliasGen("cctv", "13", "高清")
                    + aliasGen("cctv", "13", "新闻")
                    + aliasGen("cctv", "13", "新闻", "HD")
                    + aliasGen("cctv", "13", "新闻", "高清")
                    + aliasGen("cctv", "13", "高码")
                    + aliasGen("cctv", "13", "50", "fps")
                    + aliasGen("cctv", "13", "hevc"),

            "CCTV14" to aliasGen("cctv", "14")
                    + aliasGen("cctv", "14", "hd")
                    + aliasGen("cctv", "14", "高清")
                    + aliasGen("cctv", "14", "少儿")
                    + aliasGen("cctv", "14", "少儿", "HD")
                    + aliasGen("cctv", "14", "少儿", "高清")
                    + aliasGen("cctv", "14", "高码")
                    + aliasGen("cctv", "14", "50", "fps")
                    + aliasGen("cctv", "14", "hevc"),

            "CCTV15" to aliasGen("cctv", "15")
                    + aliasGen("cctv", "15", "hd")
                    + aliasGen("cctv", "15", "高清")
                    + aliasGen("cctv", "15", "音乐")
                    + aliasGen("cctv", "15", "音乐", "HD")
                    + aliasGen("cctv", "15", "音乐", "高清")
                    + aliasGen("cctv", "15", "高码")
                    + aliasGen("cctv", "15", "50", "fps")
                    + aliasGen("cctv", "15", "hevc"),

            "CCTV16" to aliasGen("cctv", "16")
                    + aliasGen("cctv", "16", "hd")
                    + aliasGen("cctv", "16", "高清")
                    + aliasGen("cctv", "16", "奥林匹克")
                    + aliasGen("cctv", "16", "奥林匹克", "HD")
                    + aliasGen("cctv", "16", "奥林匹克", "高清")
                    + aliasGen("cctv", "16", "高码")
                    + aliasGen("cctv", "16", "50", "fps")
                    + aliasGen("cctv", "16", "hevc"),

            "CCTV17" to aliasGen("cctv", "17")
                    + aliasGen("cctv", "17", "hd")
                    + aliasGen("cctv", "17", "高清")
                    + aliasGen("cctv", "17", "农业农村")
                    + aliasGen("cctv", "17", "农业农村", "HD")
                    + aliasGen("cctv", "17", "农业农村", "高清")
                    + aliasGen("cctv", "17", "高码")
                    + aliasGen("cctv", "17", "50", "fps")
                    + aliasGen("cctv", "17", "hevc"),

            "CCTV4欧洲" to aliasGen("cctv", "4", "欧洲")
                    + aliasGen("cctv", "4", "欧洲", "HD")
                    + aliasGen("cctv", "4", "欧洲", "高清"),

            "CCTV4美洲" to aliasGen("cctv", "4", "美洲")
                    + aliasGen("cctv", "4", "美洲", "HD")
                    + aliasGen("cctv", "4", "美洲", "高清"),

            "CGTN西语" to aliasGen("cgtn", "西语")
                    + aliasGen("cgtn", "西语", "HD")
                    + aliasGen("cgtn", "西语", "高清")
                    + aliasGen("cgtn", "西班牙语", "HD")
                    + aliasGen("cgtn", "西班牙语", "高清"),


            "CGTN法语" to aliasGen("cgtn", "法语")
                    + aliasGen("cgtn", "法语", "HD")
                    + aliasGen("cgtn", "法语", "高清"),


            "CGTN俄语" to aliasGen("cgtn", "俄语")
                    + aliasGen("cgtn", "俄语", "HD")
                    + aliasGen("cgtn", "俄语", "高清"),

            "CGTN阿语" to aliasGen("cgtn", "阿语")
                    + aliasGen("cgtn", "阿语", "HD")
                    + aliasGen("cgtn", "阿语", "高清")
                    + aliasGen("cgtn", "阿拉伯语", "HD")
                    + aliasGen("cgtn", "阿拉伯语", "高清"),

            "CGTN" to aliasGen("cgtn", "英语")
                    + aliasGen("cgtn", "英语", "HD")
                    + aliasGen("cgtn", "英语", "高清")
                    + aliasGen("cgtn", "新闻", "HD")
                    + aliasGen("cgtn", "新闻", "高清"),

            "CCTV4K" to aliasGen("cctv", "4K")
                    + aliasGen("cctv", "4", "k")
                    + aliasGen("cctv", "4K", "超高清"),

            "CCTV8K" to aliasGen("cctv", "8K")
                    + aliasGen("cctv", "8", "k")
                    + aliasGen("cctv", "8K", "超高清"),

            "CCTVNews" to aliasGen("cctv", "news"),

            "CGTN纪录" to aliasGen("cgtn", "纪录")
                    + aliasGen("cgtn", "纪录", "HD")
                    + aliasGen("cgtn", "纪录", "高清"),

            "重庆卫视" to aliasGen("重庆卫视", "HD")
                    + aliasGen("重庆", "高清")
                    + "四川重庆卫视",

            "四川卫视" to aliasGen("四川卫视", "HD")
                    + aliasGen("四川", "高清"),

            "贵州卫视" to aliasGen("贵州卫视", "HD")
                    + aliasGen("贵州", "高清"),

            "东方卫视" to aliasGen("东方卫视", "HD")
                    + aliasGen("东方", "高清")
                    + "SiTV东方卫视",

            "湖南卫视" to aliasGen("湖南卫视", "HD")
                    + aliasGen("湖南卫视", "高清"),

            "广东卫视" to aliasGen("广东卫视", "HD")
                    + aliasGen("广东卫视", "高清"),

            "深圳卫视" to aliasGen("深圳卫视", "HD")
                    + aliasGen("深圳卫视", "高清")
                    + "广东深圳卫视",

            "天津卫视" to aliasGen("天津卫视", "HD")
                    + aliasGen("天津卫视", "高清"),

            "湖北卫视" to aliasGen("湖北卫视", "HD")
                    + aliasGen("湖北卫视", "高清"),

            "辽宁卫视" to aliasGen("辽宁卫视", "HD")
                    + aliasGen("辽宁卫视", "高清"),

            "安徽卫视" to aliasGen("安徽卫视", "HD")
                    + aliasGen("安徽卫视", "高清"),

            "浙江卫视" to aliasGen("浙江卫视", "HD")
                    + aliasGen("浙江卫视", "高清"),

            "山东卫视" to aliasGen("山东卫视", "HD")
                    + aliasGen("山东卫视", "高清"),

            "北京卫视" to aliasGen("北京卫视", "HD")
                    + aliasGen("北京卫视", "高清"),

            "江苏卫视" to aliasGen("江苏卫视", "HD")
                    + aliasGen("北京卫视", "高清"),

            "黑龙江卫视" to aliasGen("黑龙江卫视", "HD")
                    + aliasGen("黑龙江卫视", "高清"),

            "河北卫视" to aliasGen("河北卫视", "HD")
                    + aliasGen("河北卫视", "高清"),

            "云南卫视" to aliasGen("云南卫视", "HD")
                    + aliasGen("云南卫视", "高清"),

            "江西卫视" to aliasGen("江西卫视", "HD")
                    + aliasGen("江西卫视", "高清"),

            "东南卫视" to aliasGen("东南卫视", "HD")
                    + aliasGen("东南卫视", "高清")
                    + aliasGen("福建卫视", "HD")
                    + aliasGen("福建卫视", "高清")
                    + "福建东南卫视" + "福建卫视",

            "海南卫视" to aliasGen("海南卫视", "HD")
                    + aliasGen("海南卫视", "高清")
                    + "旅游卫视",

            "吉林卫视" to aliasGen("吉林卫视", "HD")
                    + aliasGen("吉林卫视", "高清"),

            "甘肃卫视" to aliasGen("甘肃卫视", "HD")
                    + aliasGen("甘肃卫视", "高清"),

            "河南卫视" to aliasGen("河南卫视", "HD")
                    + aliasGen("河南卫视", "高清"),

            "内蒙古卫视" to aliasGen("内蒙古卫视", "HD")
                    + aliasGen("内蒙古卫视", "高清"),

            "陕西卫视" to aliasGen("陕西卫视", "HD")
                    + aliasGen("陕西卫视", "高清"),

            "广西卫视" to aliasGen("广西卫视", "HD")
                    + aliasGen("广西卫视", "高清"),

            "青海卫视" to aliasGen("青海卫视", "HD")
                    + aliasGen("青海卫视", "高清"),

            "新疆卫视" to aliasGen("新疆卫视", "HD")
                    + aliasGen("新疆卫视", "高清"),

            "西藏卫视" to aliasGen("西藏卫视", "HD")
                    + aliasGen("西藏卫视", "高清"),

            "厦门卫视" to aliasGen("厦门卫视", "HD")
                    + aliasGen("厦门卫视", "高清")
                    + "福建厦门卫视",

            "宁夏卫视" to aliasGen("宁夏卫视", "HD")
                    + aliasGen("宁夏卫视", "高清"),

            "山西卫视" to aliasGen("山西卫视", "HD")
                    + aliasGen("山西卫视", "高清"),

            "兵团卫视" to aliasGen("兵团卫视", "HD")
                    + aliasGen("兵团卫视", "高清")
                    + "新疆兵团卫视",

            "康巴卫视" to aliasGen("康巴卫视", "HD")
                    + aliasGen("康巴卫视", "高清")
                    + "四川康巴卫视",

            "延边卫视" to aliasGen("延边卫视", "HD")
                    + aliasGen("延边卫视", "高清")
                    + "吉林延边卫视",

            "卡酷少儿" to listOf("北京卡酷少儿"),

            "北京纪实科教" to aliasGen("纪实科教", "HD")
                    + aliasGen("纪实科教", "高清")
                    + aliasGen("北京纪实科教", "HD")
                    + aliasGen("北京纪实科教", "高清")
                    + listOf("纪实科教", "北京纪实"),

            "第一剧场" to aliasGen("CCTV", "第一剧场")
                    + aliasGen("CCTV", "第一剧场", "HD")
                    + aliasGen("CCTV", "第一剧场", "高清"),

            "风云剧场" to aliasGen("CCTV", "风云剧场")
                    + aliasGen("CCTV", "风云剧场", "HD")
                    + aliasGen("CCTV", "风云剧场", "高清"),

            "怀旧剧场" to aliasGen("CCTV", "怀旧剧场")
                    + aliasGen("CCTV", "怀旧剧场", "HD")
                    + aliasGen("CCTV", "怀旧剧场", "高清"),

            "世界地理" to aliasGen("CCTV", "世界地理")
                    + aliasGen("CCTV", "世界地理", "HD")
                    + aliasGen("CCTV", "世界地理", "高清"),

            "风云音乐" to aliasGen("CCTV", "风云音乐")
                    + aliasGen("CCTV", "风云音乐", "HD")
                    + aliasGen("CCTV", "风云音乐", "高清"),

            "兵器科技" to aliasGen("CCTV", "兵器科技")
                    + aliasGen("CCTV", "兵器科技", "HD")
                    + aliasGen("CCTV", "兵器科技", "高清"),

            "风云足球" to aliasGen("CCTV", "风云足球")
                    + aliasGen("CCTV", "风云足球", "HD")
                    + aliasGen("CCTV", "风云足球", "高清"),

            "高尔夫网球" to aliasGen("CCTV", "高尔夫网球")
                    + aliasGen("CCTV", "高尔夫网球", "HD")
                    + aliasGen("CCTV", "高尔夫网球", "高清"),

            "女性时尚" to aliasGen("CCTV", "女性时尚")
                    + aliasGen("CCTV", "女性时尚", "HD")
                    + aliasGen("CCTV", "女性时尚", "高清"),

            "央视文化精品" to aliasGen("CCTV", "央视文化精品")
                    + aliasGen("CCTV", "央视文化精品", "HD")
                    + aliasGen("CCTV", "央视文化精品", "高清"),

            "央视台球" to aliasGen("CCTV", "央视台球")
                    + aliasGen("CCTV", "央视台球", "HD")
                    + aliasGen("CCTV", "央视台球", "高清"),

            "求索纪录" to aliasGen("求索纪录", "HD")
                    + aliasGen("求索纪录", "高清")
                    + "华数求索纪录",

            "求索科学" to aliasGen("求索科学", "HD")
                    + aliasGen("求索科学", "高清")
                    + "华数求索科学",

            "CETV1" to aliasGen("CETV", "1")
                    + aliasGen("CETV", "1", "HD")
                    + aliasGen("CETV", "1", "高清")
                    + listOf(
                "中国教育1",
                "中国教育1台",
                "中国教育1频道",
                "中国教育电视台1",
                "中国教育电视台1台",
                "中国教育电视台1频道",
            ),

            "CETV2" to aliasGen("CETV", "2")
                    + aliasGen("CETV", "2", "HD")
                    + aliasGen("CETV", "2", "高清")
                    + listOf(
                "中国教育2",
                "中国教育2台",
                "中国教育2频道",
                "中国教育电视台2",
                "中国教育电视台2台",
                "中国教育电视台2频道",
            ),
            "CETV3" to aliasGen("CETV", "3")
                    + aliasGen("CETV", "3", "HD")
                    + aliasGen("CETV", "3", "高清")
                    + listOf(
                "中国教育3",
                "中国教育3台",
                "中国教育3频道",
                "中国教育电视台3",
                "中国教育电视台3台",
                "中国教育电视台3频道",
            ),
            "CETV4" to aliasGen("CETV", "4")
                    + aliasGen("CETV", "4", "HD")
                    + aliasGen("CETV", "4", "高清")
                    + listOf(
                "中国教育4",
                "中国教育4台",
                "中国教育4频道",
                "中国教育电视台4",
                "中国教育电视台4台",
                "中国教育电视台4频道",
            ),

            "嘉佳卡通" to aliasGen("嘉佳卡通", "HD")
                    + aliasGen("嘉佳卡通", "高清")
                    + "广东嘉佳卡通",

            "大湾区卫视" to aliasGen("大湾区卫视", "HD")
                    + aliasGen("大湾区卫视", "高清")
                    + listOf("广东大湾区卫视", "南方卫视"),

            "金鹰卡通" to aliasGen("金鹰卡通", "HD")
                    + aliasGen("金鹰卡通", "高清")
                    + "湖南金鹰卡通",

            "金鹰纪实" to aliasGen("金鹰纪实", "HD")
                    + aliasGen("金鹰纪实", "高清")
                    + "湖南金鹰纪实",

            "五星体育" to aliasGen("五星体育", "HD")
                    + aliasGen("五星体育", "高清")
                    + aliasGen("SiTV", "五星体育")
                    + "上海五星体育",

            "求索生活" to aliasGen("求索生活", "HD")
                    + aliasGen("求索生活", "高清")
                    + "华数求索生活",

            "新视觉" to listOf(
                "新视觉HD",
                "新视觉 HD",
                "新视觉高清",
                "新视觉 高清",
                "SiTV新视觉",
                "SiTV 新视觉",
            ),
            "快乐垂钓" to listOf(
                "快乐垂钓HD",
                "快乐垂钓 HD",
                "快乐垂钓高清",
                "快乐垂钓 高清",
                "湖南快乐垂钓",
            ),
            "东方财经" to listOf(
                "东方财经HD",
                "东方财经 HD",
                "东方财经高清",
                "东方财经 高清",
                "SiTV东方财经",
                "SiTV东方财经HD",
                "SiTV东方财经 HD",
                "SiTV 东方财经 HD",
                "SiTV 东方财经",
                "SiTV东方财经高清",
                "SiTV东方财经 高清",
                "SiTV 东方财经 高清",
                "上海东方财经",
            ),
            "动漫秀场" to listOf(
                "动漫秀场HD",
                "动漫秀场 HD",
                "动漫秀场高清",
                "动漫秀场 高清",
                "SiTV动漫秀场",
                "SiTV动漫秀场HD",
                "SiTV动漫秀场 HD",
                "SiTV 动漫秀场 HD",
                "SiTV 动漫秀场",
                "SiTV动漫秀场高清",
                "SiTV动漫秀场 高清",
                "SiTV 动漫秀场 高清",
                "上海动漫秀场",
            ),
            "劲爆体育" to listOf(
                "劲爆体育HD",
                "劲爆体育 HD",
                "劲爆体育高清",
                "劲爆体育 高清",
                "SiTV劲爆体育",
                "SiTV劲爆体育HD",
                "SiTV劲爆体育 HD",
                "SiTV 劲爆体育 HD",
                "SiTV 劲爆体育",
                "SiTV劲爆体育高清",
                "SiTV劲爆体育 高清",
                "SiTV 劲爆体育 高清",
                "上海劲爆体育",
            ),
            "茶频道" to listOf(
                "茶频道HD",
                "茶频道 HD",
                "茶频道高清",
                "茶频道 高清",
                "湖南茶频道",
                "湖南 茶频道",
            ),
            "都市剧场" to listOf(
                "都市剧场HD",
                "都市剧场 HD",
                "都市剧场高清",
                "都市剧场 高清",
                "SiTV都市剧场",
                "SiTV都市剧场HD",
                "SiTV都市剧场 HD",
                "SiTV 都市剧场 HD",
                "SiTV 都市剧场",
                "SiTV都市剧场高清",
                "SiTV都市剧场 高清",
                "SiTV 都市剧场 高清",
                "上海都市剧场",
            ),
            "乐游" to listOf(
                "乐游HD",
                "乐游 HD",
                "乐游高清",
                "乐游 高清",
                "全纪实",
                "全纪实HD",
                "全纪实 HD",
                "全纪实高清",
                "全纪实 高清",
                "SiTV全纪实",
                "SiTV 全纪实",
                "SiTV乐游",
                "SiTV乐游HD",
                "SiTV乐游 HD",
                "SiTV乐游高清",
                "SiTV乐游 高清",
                "SiTV 乐游",
                "SiTV 乐游 HD",
                "SiTV 乐游 高清",
            ),
            "CHC动作电影" to listOf(
                "CHC动作电影HD",
                "CHC动作电影 HD",
                "CHC动作电影高清",
                "CHC动作电影 高清",
                "CHC 动作电影",
                "CHC 动作电影 HD",
                "CHC 动作电影 高清",
                "CHC-动作电影",
            ),
            "CHC家庭影院" to listOf(
                "CHC家庭影院HD",
                "CHC家庭影院 HD",
                "CHC家庭影院高清",
                "CHC动家庭影院 高清",
                "CHC 家庭影院",
                "CHC 家庭影院 HD",
                "CHC 家庭影院 高清",
                "CHC-家庭影院",
            ),
            "CHC影迷电影" to listOf(
                "CHC高清电影HD",
                "CHC高清电影 HD",
                "CHC高清电影高清",
                "CHC高清电影 高清",
                "CHC 高清电影",
                "CHC 高清电影 HD",
                "CHC 高清电影 高清",
                "CHC-高清电影",
                "CHC影迷电影HD",
                "CHC影迷电影 HD",
                "CHC影迷电影高清",
                "CHC影迷电影 高清",
                "CHC 影迷电影",
                "CHC 影迷电影 HD",
                "CHC 影迷电影 高清",
                "CHC-影迷电影",
            ),
            "欢笑剧场" to listOf(
                "欢笑剧场HD",
                "欢笑剧场 HD",
                "欢笑剧场高清",
                "欢笑剧场 高清",
                "SiTV欢笑剧场",
                "SiTV欢笑剧场HD",
                "SiTV欢笑剧场 HD",
                "SiTV欢笑剧场高清",
                "SiTV欢笑剧场 高清",
                "SiTV 欢笑剧场",
                "SiTV 欢笑剧场HD",
                "SiTV 欢笑剧场 HD",
                "SiTV 欢笑剧场高清",
                "SiTV 欢笑剧场 高清",
                "上海欢笑剧场",
            ),
            "求索动物" to listOf(
                "求索动物HD",
                "求索动物 HD",
                "求索动物高清",
                "求索动物 高清",
                "华数求索动物",
            ),
            "金色学堂" to listOf(
                "金色学堂HD",
                "金色学堂 HD",
                "金色学堂高清",
                "金色学堂 高清",
                "SiTV金色学堂",
                "SiTV金色学堂HD",
                "SiTV金色学堂 HD",
                "SiTV 金色学堂 HD",
                "SiTV 金色学堂",
                "SiTV金色学堂高清",
                "SiTV金色学堂 高清",
                "SiTV 金色学堂 高清",
                "上海金色学堂",
            ),
            "魅力足球" to listOf(
                "魅力足球HD",
                "魅力足球 HD",
                "魅力足球高清",
                "魅力足球 高清",
                "SiTV魅力足球",
                "SiTV魅力足球HD",
                "SiTV魅力足球 HD",
                "SiTV 魅力足球 HD",
                "SiTV 魅力足球",
                "SiTV魅力足球高清",
                "SiTV魅力足球 高清",
                "SiTV 魅力足球 高清",
                "上海魅力足球",
            ),
            "法治天地" to listOf(
                "法治天地HD",
                "法治天地 HD",
                "法治天地高清",
                "法治天地 高清",
                "SiTV法治天地",
                "SiTV法治天地HD",
                "SiTV法治天地 HD",
                "SiTV 法治天地 HD",
                "SiTV 法治天地",
                "SiTV法治天地高清",
                "SiTV法治天地 高清",
                "SiTV 法治天地 高清",
                "上海法治天地",
                "上视法治天地",
            ),
            "生活时尚" to listOf(
                "生活时尚HD",
                "生活时尚 HD",
                "生活时尚高清",
                "生活时尚 高清",
                "SiTV生活时尚",
                "SiTV生活时尚HD",
                "SiTV生活时尚 HD",
                "SiTV 生活时尚 HD",
                "SiTV 生活时尚",
                "SiTV生活时尚高清",
                "SiTV生活时尚 高清",
                "SiTV 生活时尚 高清",
                "上海生活时尚",
            ),
            "翡翠台" to listOf(
                "翡翠台HD",
                "翡翠台 HD",
                "翡翠台高清",
                "翡翠台 高清",
                "TVB翡翠",
                "TVB翡翠台",
                "TVB 翡翠台",
                "TVB翡翠台HD",
                "TVB翡翠台 HD",
                "TVB翡翠台高清",
                "TVB翡翠台 高清",
                "TVB 翡翠台 HD",
                "TVB 翡翠台 高清",
            ),
            "明珠台" to listOf(
                "明珠台HD",
                "明珠台 HD",
                "明珠台高清",
                "明珠台 高清",
                "TVB明珠",
                "TVB明珠台",
                "TVB 明珠台",
                "TVB明珠台HD",
                "TVB明珠台 HD",
                "TVB明珠台高清",
                "TVB明珠台 高清",
                "TVB 明珠台 HD",
                "TVB 明珠台 高清",
            ),
            "澳亚卫视" to listOf(
                "澳亚卫视HD",
                "澳亚卫视 HD",
                "澳亚卫视高清",
                "澳亚卫视 高清",
                "澳门澳亚卫视",
            ),
            "凤凰中文" to listOf(
                "凤凰中文HD",
                "凤凰中文高清",
                "凤凰中文台",
                "凤凰中文台HD",
                "凤凰中文台高清",
                "凤凰卫视中文",
                "凤凰卫视中文台",
            ),
            "凤凰资讯" to listOf(
                "凤凰资讯HD",
                "凤凰资讯高清",
                "凤凰资讯台",
                "凤凰资讯台HD",
                "凤凰资讯台高清",
                "凤凰卫视资讯",
                "凤凰卫视资讯台",
            ),
            "东方影视" to listOf(
                "东方影视HD",
                "东方影视 HD",
                "东方影视高清",
                "东方影视 高清",
                "SiTV东方影视",
                "SiTV东方影视HD",
                "SiTV东方影视 HD",
                "SiTV 东方影视 HD",
                "SiTV 东方影视",
                "SiTV东方影视高清",
                "SiTV东方影视 高清",
                "SiTV 东方影视 高清",
                "上海东方影视",
            ),
            "纪实人文" to listOf(
                "纪实人文HD",
                "纪实人文 HD",
                "纪实人文高清",
                "纪实人文 高清",
                "SiTV纪实人文",
                "SiTV纪实人文HD",
                "SiTV纪实人文 HD",
                "SiTV 纪实人文 HD",
                "SiTV 纪实人文",
                "SiTV纪实人文高清",
                "SiTV纪实人文 高清",
                "SiTV 纪实人文 高清",
                "上海纪实",
                "上海纪实人文",
            ),
            "上海外语" to listOf(
                "上海外语HD",
                "上海外语 HD",
                "上海外语高清",
                "上海外语 高清",
                "SiTV上海外语",
                "SiTV上海外语HD",
                "SiTV上海外语 HD",
                "SiTV 上海外语 HD",
                "SiTV 上海外语",
                "SiTV上海外语高清",
                "SiTV上海外语 高清",
                "SiTV 上海外语 高清",
            ),
            "第一财经" to listOf(
                "第一财经HD",
                "第一财经 HD",
                "第一财经高清",
                "第一财经 高清",
                "SiTV第一财经",
                "SiTV第一财经HD",
                "SiTV第一财经 HD",
                "SiTV 第一财经 HD",
                "SiTV 第一财经",
                "SiTV第一财经高清",
                "SiTV第一财经 高清",
                "SiTV 第一财经 高清",
                "上海第一财经",
            ),
            "上海新闻综合" to listOf(
                "上海新闻HD",
                "上海新闻 HD",
                "上海新闻高清",
                "上海新闻 高清",
                "SiTV上海新闻",
                "SiTV上海新闻HD",
                "SiTV上海新闻 HD",
                "SiTV 上海新闻 HD",
                "SiTV 上海新闻",
                "SiTV上海新闻高清",
                "SiTV上海新闻 高清",
                "SiTV 上海新闻 高清",
                "上海新闻",
                "上海新闻频道",
                "上海新闻综合HD",
                "上海新闻综合 HD",
                "上海新闻综合高清",
                "上海新闻综合 高清",
                "SiTV上海新闻综合",
            ),
            "优漫卡通" to listOf(
                "优漫卡通HD",
                "优漫卡通 HD",
                "优漫卡通高清",
                "优漫卡通 高清",
                "江苏优漫卡通",
            ),
            "哈哈炫动" to listOf(
                "哈哈炫动HD",
                "哈哈炫动 HD",
                "哈哈炫动高清",
                "哈哈炫动 高清",
                "SiTV哈哈炫动",
                "SiTV哈哈炫动HD",
                "SiTV哈哈炫动 HD",
                "SiTV 哈哈炫动 HD",
                "SiTV 哈哈炫动",
                "SiTV哈哈炫动高清",
                "SiTV哈哈炫动 高清",
                "SiTV 哈哈炫动 高清",
                "上海哈哈炫动",
            ),
            "山东教育" to listOf(
                "山东教育HD",
                "山东教育 HD",
                "山东教育高清",
                "山东教育 高清",
                "山东教育台",
                "山东教育频道",
                "山东教育电视台",
                "山东教育电视台HD",
                "山东教育电视台高清",
                "山东教育卫视",
                "山东教育卫视HD",
                "山东教育卫视 HD",
                "山东教育卫视高清",
                "山东教育卫视 高清",
            ),
            "游戏风云" to listOf(
                "游戏风云HD",
                "游戏风云 HD",
                "游戏风云高清",
                "游戏风云 高清",
                "SiTV游戏风云",
                "SiTV游戏风云HD",
                "SiTV游戏风云 HD",
                "SiTV 游戏风云 HD",
                "SiTV 游戏风云",
                "SiTV游戏风云高清",
                "SiTV游戏风云 高清",
                "SiTV 游戏风云 高清",
                "上海游戏风云",
            ),
            "TVB Plus" to listOf(
                "TVBPlus",
                "TVB Plus HD",
                "TVB Plus 高清",
            ),
            "美亚电影" to listOf(
                "美亚电影HD",
                "美亚电影 HD",
                "美亚电影高清",
                "美亚电影 高清",
            ),
            "无线新闻" to listOf(
                "TVB无线新闻",
                "TVB无线新闻台",
                "TVB 无线新闻",
                "TVB 无线新闻台",
                "无线新闻HD",
                "无线新闻 HD",
                "无线新闻高清",
                "无线新闻 高清",
                "TVB无线新闻HD",
                "TVB无线新闻 HD",
                "TVB 无线新闻 HD",
                "TVB无线新闻台HD",
                "TVB无线新闻台 HD",
                "TVB无线新闻台高清",
                "TVB无线新闻台 高清",
            ),
            "书画" to listOf(
                "书画频道",
            ),
            "农林卫视" to listOf(
                "农林卫视HD",
                "农林卫视 HD",
                "农林卫视高清",
                "农林卫视 高清",
                "陕西农林卫视",
                "陕西农林卫视HD",
                "陕西农林卫视高清",
            ),
            "中华美食" to listOf(
                "中华美食HD",
                "中华美食 HD",
                "中华美食高清",
                "中华美食 高清",
                "青岛中华美食",
            ),
            "上海都市" to listOf(
                "上海都市HD",
                "上海都市 HD",
                "上海都市高清",
                "上海都市 高清",
                "SiTV上海都市",
                "SiTV上海都市HD",
                "SiTV上海都市 HD",
                "SiTV 上海都市 HD",
                "SiTV 上海都市",
                "SiTV上海都市高清",
                "SiTV上海都市 高清",
                "SiTV 上海都市 高清",
                "SiTV都市",
            ),
            "海峡卫视" to listOf(
                "海峡卫视HD",
                "海峡卫视 HD",
                "海峡卫视高清",
                "海峡卫视 高清",
                "福建海峡卫视",
            ),
            "凤凰香港" to listOf(
                "凤凰香港HD",
                "凤凰香港高清",
                "凤凰香港台",
                "凤凰香港台HD",
                "凤凰香港台高清",
                "凤凰卫视香港",
                "凤凰卫视香港台",
            ),
            "七彩戏剧" to listOf(
                "七彩戏剧HD",
                "七彩戏剧 HD",
                "七彩戏剧高清",
                "七彩戏剧 高清",
                "SiTV七彩戏剧",
                "SiTV七彩戏剧HD",
                "SiTV七彩戏剧 HD",
                "SiTV 七彩戏剧 HD",
                "SiTV 七彩戏剧",
                "SiTV七彩戏剧高清",
                "SiTV七彩戏剧 高清",
                "SiTV 七彩戏剧 高清",
                "上海七彩戏剧",
            ),
            "三沙卫视" to listOf(
                "三沙卫视HD",
                "三沙卫视 HD",
                "三沙卫视高清",
                "三沙卫视 高清",
                "海南三沙卫视",
            ),
            "湖南爱晚" to listOf(
                "爱晚",
                "爱晚HD",
                "爱晚 HD",
                "爱晚高清",
                "爱晚 高清",
            ),
            "云南都市" to listOf(
                "云南都市HD",
                "云南都市 HD",
                "云南都市高清",
                "云南都市 高清",
                "云南都市频道",
                "云南都市频道高清",
                "云南都市频道 高清",
                "云南都市频道HD",
                "云南都市频道 HD",
            ),
            "云南娱乐" to listOf(
                "云南娱乐HD",
                "云南娱乐 HD",
                "云南娱乐高清",
                "云南娱乐 高清",
                "云南娱乐频道",
                "云南娱乐频道高清",
                "云南娱乐频道 高清",
                "云南娱乐频道HD",
                "云南娱乐频道 HD",
            ),
            "云南影视" to listOf(
                "云南影视HD",
                "云南影视 HD",
                "云南影视高清",
                "云南影视 高清",
                "云南影视频道",
                "云南影视频道高清",
                "云南影视频道 高清",
                "云南影视频道HD",
                "云南影视频道 HD",
            ),
            "云南康旅" to listOf(
                "云南康旅HD",
                "云南康旅 HD",
                "云南康旅高清",
                "云南康旅 高清",
                "云南康旅频道",
                "云南康旅频道高清",
                "云南康旅频道 高清",
                "云南康旅频道HD",
                "云南康旅频道 HD",
            ),
            "云南少儿" to listOf(
                "云南少儿HD",
                "云南少儿 HD",
                "云南少儿高清",
                "云南少儿 高清",
                "云南少儿频道",
                "云南少儿频道高清",
                "云南少儿频道 高清",
                "云南少儿频道HD",
                "云南少儿频道 HD",
            ),
            "澜湄国际" to listOf(
                "澜湄国际HD",
                "澜湄国际 HD",
                "澜湄国际高清",
                "澜湄国际 高清",
                "澜湄国际频道",
                "澜湄国际频道高清",
                "澜湄国际频道 高清",
                "澜湄国际频道HD",
                "澜湄国际频道 HD",
                "云南澜湄国际",
                "云南澜湄国际HD",
                "云南澜湄国际 HD",
                "云南澜湄国际高清",
                "云南澜湄国际 高清",
                "云南国际",
                "云南国际频道",
            ),
            "黑莓电影" to listOf(
                "黑莓电影HD",
                "黑莓电影 HD",
                "黑莓电影高清",
                "黑莓电影 高清",
                "NewTV黑莓电影",
                "NewTV 黑莓电影",
                "NewTV黑莓电影HD",
                "NewTV黑莓电影 HD",
                "NewTV 黑莓电影 HD",
                "NewTV黑莓电影高清",
                "NewTV黑莓电影 高清",
                "NewTV 黑莓电影 高清",
            ),
            "黑莓动画" to listOf(
                "黑莓动画HD",
                "黑莓动画 HD",
                "黑莓动画高清",
                "黑莓动画 高清",
                "NewTV黑莓动画",
                "NewTV 黑莓动画",
                "NewTV黑莓动画HD",
                "NewTV黑莓动画 HD",
                "NewTV 黑莓动画 HD",
                "NewTV黑莓动画高清",
                "NewTV黑莓动画 高清",
                "NewTV 黑莓动画 高清",
            ),
            "动作电影" to listOf(
                "动作电影HD",
                "动作电影 HD",
                "动作电影高清",
                "动作电影 高清",
                "NewTV动作电影",
                "NewTV 动作电影",
                "NewTV动作电影HD",
                "NewTV动作电影 HD",
                "NewTV动作电影高清",
                "NewTV动作电影 高清",
                "NewTV 动作电影 HD",
                "NewTV 动作电影 高清",
            ),
            "重温经典" to listOf(
                "重温经典HD",
                "重温经典 HD",
                "重温经典高清",
                "重温经典 高清",
            ),
            "潮妈辣婆" to listOf(
                "潮妈辣婆HD",
                "潮妈辣婆 HD",
                "潮妈辣婆高清",
                "潮妈辣婆 高清",
                "NewTV潮妈辣婆",
                "NewTV 潮妈辣婆",
                "NewTV潮妈辣婆HD",
                "NewTV潮妈辣婆 HD",
                "NewTV潮妈辣婆高清",
                "NewTV潮妈辣婆 高清",
                "NewTV 潮妈辣婆 HD",
                "NewTV 潮妈辣婆 高清",
            ),
            "哒啵赛事" to listOf(
                "哒啵赛事HD",
                "哒啵赛事 HD",
                "哒啵赛事高清",
                "哒啵赛事 高清",
                "NewTV哒啵赛事",
                "NewTV 哒啵赛事",
                "NewTV哒啵赛事HD",
                "NewTV哒啵赛事 HD",
                "NewTV哒啵赛事高清",
                "NewTV哒啵赛事 高清",
                "NewTV 哒啵赛事 HD",
                "NewTV 哒啵赛事 高清",
            ),
            "哒啵电竞" to listOf(
                "哒啵电竞HD",
                "哒啵电竞 HD",
                "哒啵电竞高清",
                "哒啵电竞 高清",
                "NewTV哒啵电竞",
                "NewTV 哒啵电竞",
                "NewTV哒啵电竞HD",
                "NewTV哒啵电竞 HD",
                "NewTV哒啵电竞高清",
                "NewTV哒啵电竞 高清",
                "NewTV 哒啵电竞 HD",
                "NewTV 哒啵电竞 高清",
            ),
            "军事评论" to listOf(
                "军事评论HD",
                "军事评论 HD",
                "军事评论高清",
                "军事评论 高清",
                "NewTV军事评论",
                "NewTV 军事评论",
                "NewTV军事评论HD",
                "NewTV军事评论 HD",
                "NewTV军事评论高清",
                "NewTV军事评论 高清",
                "NewTV 军事评论 HD",
                "NewTV 军事评论 高清",
            ),
            "炫舞未来" to listOf(
                "炫舞未来HD",
                "炫舞未来 HD",
                "炫舞未来高清",
                "炫舞未来 高清",
                "NewTV炫舞未来",
                "NewTV 炫舞未来",
                "NewTV炫舞未来HD",
                "NewTV炫舞未来 HD",
                "NewTV炫舞未来高清",
                "NewTV炫舞未来 高清",
                "NewTV 炫舞未来 HD",
                "NewTV 炫舞未来 高清",
            ),
            "古装剧场" to listOf(
                "古装剧场HD",
                "古装剧场 HD",
                "古装剧场高清",
                "古装剧场 高清",
                "NewTV古装剧场",
                "NewTV 古装剧场",
                "NewTV古装剧场HD",
                "NewTV古装剧场 HD",
                "NewTV古装剧场高清",
                "NewTV古装剧场 高清",
                "NewTV 古装剧场 HD",
                "NewTV 古装剧场 高清",
            ),
            "军旅剧场" to listOf(
                "军旅剧场HD",
                "军旅剧场 HD",
                "军旅剧场高清",
                "军旅剧场 高清",
                "NewTV军旅剧场",
                "NewTV 军旅剧场",
                "NewTV军旅剧场HD",
                "NewTV军旅剧场 HD",
                "NewTV军旅剧场高清",
                "NewTV军旅剧场 高清",
                "NewTV 军旅剧场 HD",
                "NewTV 军旅剧场 高清",
            ),
            "家庭剧场" to listOf(
                "家庭剧场HD",
                "家庭剧场 HD",
                "家庭剧场高清",
                "家庭剧场 高清",
                "NewTV家庭剧场",
                "NewTV 家庭剧场",
                "NewTV家庭剧场HD",
                "NewTV家庭剧场 HD",
                "NewTV家庭剧场高清",
                "NewTV家庭剧场 高清",
                "NewTV 家庭剧场 HD",
                "NewTV 家庭剧场 高清",
            ),
            "爱情喜剧" to listOf(
                "爱情喜剧HD",
                "爱情喜剧 HD",
                "爱情喜剧高清",
                "爱情喜剧 高清",
                "NewTV爱情喜剧",
                "NewTV 爱情喜剧",
                "NewTV爱情喜剧HD",
                "NewTV爱情喜剧 HD",
                "NewTV爱情喜剧高清",
                "NewTV爱情喜剧 高清",
                "NewTV 爱情喜剧 HD",
                "NewTV 爱情喜剧 高清",
            ),
            "热播精选" to listOf(
                "热播精选HD",
                "热播精选 HD",
                "热播精选高清",
                "热播精选 高清",
                "NewTV热播精选",
                "NewTV 热播精选",
                "NewTV热播精选HD",
                "NewTV热播精选 HD",
                "NewTV热播精选高清",
                "NewTV热播精选 高清",
                "NewTV 热播精选 HD",
                "NewTV 热播精选 高清",
            ),
            "明星大片" to listOf(
                "明星大片HD",
                "明星大片 HD",
                "明星大片高清",
                "明星大片 高清",
                "NewTV明星大片",
                "NewTV 明星大片",
                "NewTV明星大片HD",
                "NewTV明星大片 HD",
                "NewTV明星大片高清",
                "NewTV明星大片 高清",
                "NewTV 明星大片 HD",
                "NewTV 明星大片 高清",
            ),
            "惊悚悬疑" to listOf(
                "惊辣悬疑HD",
                "惊辣悬疑 HD",
                "惊辣悬疑高清",
                "惊辣悬疑 高清",
                "NewTV惊辣悬疑",
                "NewTV 惊辣悬疑",
                "NewTV惊辣悬疑HD",
                "NewTV惊辣悬疑 HD",
                "NewTV惊辣悬疑高清",
                "NewTV惊辣悬疑 高清",
                "NewTV 惊辣悬疑 HD",
                "NewTV 惊辣悬疑 高清",
            ),
            "金牌综艺" to listOf(
                "金牌综艺HD",
                "金牌综艺 HD",
                "金牌综艺高清",
                "金牌综艺 高清",
                "NewTV金牌综艺",
                "NewTV 金牌综艺",
                "NewTV金牌综艺HD",
                "NewTV金牌综艺 HD",
                "NewTV金牌综艺高清",
                "NewTV金牌综艺 高清",
                "NewTV 金牌综艺 HD",
                "NewTV 金牌综艺 高清",
            ),
            "精品纪录" to listOf(
                "精品纪录HD",
                "精品纪录 HD",
                "精品纪录高清",
                "精品纪录 高清",
                "NewTV精品纪录",
                "NewTV 精品纪录",
                "NewTV精品纪录HD",
                "NewTV精品纪录 HD",
                "NewTV精品纪录高清",
                "NewTV精品纪录 高清",
                "NewTV 精品纪录 HD",
                "NewTV 精品纪录 高清",
            ),
            "精品大剧" to listOf(
                "精品大剧HD",
                "精品大剧 HD",
                "精品大剧高清",
                "精品大剧 高清",
                "NewTV精品大剧",
                "NewTV 精品大剧",
                "NewTV精品大剧HD",
                "NewTV精品大剧 HD",
                "NewTV精品大剧高清",
                "NewTV精品大剧 高清",
                "NewTV 精品大剧 HD",
                "NewTV 精品大剧 高清",
            ),
            "精品体育" to listOf(
                "精品体育HD",
                "精品体育 HD",
                "精品体育高清",
                "精品体育 高清",
                "NewTV精品体育",
                "NewTV 精品体育",
                "NewTV精品体育HD",
                "NewTV精品体育 HD",
                "NewTV精品体育高清",
                "NewTV精品体育 高清",
                "NewTV 精品体育 HD",
                "NewTV 精品体育 高清",
            ),
            "精品萌宠" to listOf(
                "精品萌宠HD",
                "精品萌宠 HD",
                "精品萌宠高清",
                "精品萌宠 高清",
                "NewTV精品萌宠",
                "NewTV 精品萌宠",
                "NewTV精品萌宠HD",
                "NewTV精品萌宠 HD",
                "NewTV精品萌宠高清",
                "NewTV精品萌宠 高清",
                "NewTV 精品萌宠 HD",
                "NewTV 精品萌宠 高清",
            ),
            "中国功夫" to listOf(
                "中国功夫HD",
                "中国功夫 HD",
                "中国功夫高清",
                "中国功夫 高清",
                "NewTV中国功夫",
                "NewTV 中国功夫",
                "NewTV中国功夫HD",
                "NewTV中国功夫 HD",
                "NewTV中国功夫高清",
                "NewTV中国功夫 高清",
                "NewTV 中国功夫 HD",
                "NewTV 中国功夫 高清",
            ),
            "怡伴健康" to listOf(
                "怡伴健康HD",
                "怡伴健康 HD",
                "怡伴健康高清",
                "怡伴健康 高清",
                "NewTV怡伴健康",
                "NewTV 怡伴健康",
                "NewTV怡伴健康HD",
                "NewTV怡伴健康 HD",
                "NewTV怡伴健康高清",
                "NewTV怡伴健康 高清",
                "NewTV 怡伴健康 HD",
                "NewTV 怡伴健康 高清",
            ),
            "超级综艺" to listOf(
                "超级综艺HD",
                "超级综艺 HD",
                "超级综艺高清",
                "超级综艺 高清",
                "NewTV超级综艺",
                "NewTV 超级综艺",
                "NewTV超级综艺HD",
                "NewTV超级综艺 HD",
                "NewTV超级综艺高清",
                "NewTV超级综艺 高清",
                "NewTV 超级综艺 HD",
                "NewTV 超级综艺 高清",
            ),
            "超级电影" to listOf(
                "超级电影HD",
                "超级电影 HD",
                "超级电影高清",
                "超级电影 高清",
                "NewTV超级电影",
                "NewTV 超级电影",
                "NewTV超级电影HD",
                "NewTV超级电影 HD",
                "NewTV超级电影高清",
                "NewTV超级电影 高清",
                "NewTV 超级电影 HD",
                "NewTV 超级电影 高清",
            ),
            "超级电视剧" to listOf(
                "超级电视剧HD",
                "超级电视剧 HD",
                "超级电视剧高清",
                "超级电视剧 高清",
                "NewTV超级电视剧",
                "NewTV 超级电视剧",
                "NewTV超级电视剧HD",
                "NewTV超级电视剧 HD",
                "NewTV超级电视剧高清",
                "NewTV超级电视剧 高清",
                "NewTV 超级电视剧 HD",
                "NewTV 超级电视剧 高清",
            ),
            "农业致富" to listOf(
                "农业致富HD",
                "农业致富 HD",
                "农业致富高清",
                "农业致富 高清",
                "NewTV农业致富",
                "NewTV 农业致富",
                "NewTV农业致富HD",
                "NewTV农业致富 HD",
                "NewTV农业致富高清",
                "NewTV农业致富 高清",
                "NewTV 农业致富 HD",
                "NewTV 农业致富 高清",
            ),
            "东北热剧" to listOf(
                "东北热剧HD",
                "东北热剧 HD",
                "东北热剧高清",
                "东北热剧 高清",
                "NewTV东北热剧",
                "NewTV 东北热剧",
                "NewTV东北热剧HD",
                "NewTV东北热剧 HD",
                "NewTV东北热剧高清",
                "NewTV东北热剧 高清",
                "NewTV 东北热剧 HD",
                "NewTV 东北热剧 高清",
            ),
            "欢乐剧场" to listOf(
                "欢乐剧场HD",
                "欢乐剧场 HD",
                "欢乐剧场高清",
                "欢乐剧场 高清",
                "NewTV欢乐剧场",
                "NewTV 欢乐剧场",
                "NewTV欢乐剧场HD",
                "NewTV欢乐剧场 HD",
                "NewTV欢乐剧场高清",
                "NewTV欢乐剧场 高清",
                "NewTV 欢乐剧场 HD",
                "NewTV 欢乐剧场 高清",
            ),
            "电视指南" to listOf(
                "CCTV电视指南",
                "CCTV 电视指南",
                "CCTV-电视指南",
                "CCTV-电视指南HD",
                "CCTV-电视指南 HD",
                "CCTV电视指南HD",
                "CCTV 电视指南HD",
                "CCTV电视指南 HD",
                "CCTV 电视指南 HD",
                "CCTV电视指南高清",
                "CCTV 电视指南高清",
                "CCTV电视指南 高清",
                "CCTV 电视指南 高清",
            ),
            "台视" to listOf(
                "台视HD",
                "台视 HD",
            ),
            "民视" to listOf(
                "民视HD",
                "民视 HD",
            ),
            "华视" to listOf(
                "华视HD",
                "华视 HD",
            ),
            "中视" to listOf(
                "中视HD",
                "中视 HD",
            ),
            "藏语卫视" to listOf(
                "藏语卫视HD",
                "藏语卫视 HD",
                "藏语卫视高清",
                "藏语卫视 高清",
                "西藏藏语卫视",
                "西藏藏语卫视HD",
                "西藏藏语卫视 HD",
                "西藏藏语卫视高清",
                "西藏藏语卫视 高清",
            ),
            "纬来综合台" to listOf(
                "纬来综合台HD",
                "纬来综合台 HD",
                "纬来综合",
                "纬来综合HD",
                "纬来综合 HD",
            ),
            "纬来戏剧台" to listOf(
                "纬来戏剧台HD",
                "纬来戏剧台 HD",
                "纬来戏剧",
                "纬来戏剧HD",
                "纬来戏剧 HD",
            ),
            "纬来日本台" to listOf(
                "纬来日本台HD",
                "纬来日本台 HD",
                "纬来日本",
                "纬来日本HD",
                "纬来日本 HD",
            ),
            "纬来电影台" to listOf(
                "纬来电影台HD",
                "纬来电影台 HD",
                "纬来电影",
                "纬来电影HD",
                "纬来电影 HD",
            ),
            "纬来体育台" to listOf(
                "纬来体育台HD",
                "纬来体育台 HD",
                "纬来体育",
                "纬来体育HD",
                "纬来体育 HD",
            ),
            "纬来音乐台" to listOf(
                "纬来音乐台HD",
                "纬来音乐台 HD",
                "纬来音乐",
                "纬来音乐HD",
                "纬来音乐 HD",
            ),
            "纬来精彩台" to listOf(
                "纬来精彩台HD",
                "纬来精彩台 HD",
                "纬来精彩",
                "纬来精彩HD",
                "纬来精彩 HD",
            ),
            "天映经典" to listOf(
                "天映经典HD",
                "天映经典 HD",
                "CCM",
                "CCM天映经典",
                "CCM 天映经典",
                "CCM天映经典HD",
                "CCM天映经典 HD",
                "CCM 天映经典 HD",
            ),
            "星空卫视" to listOf(
                "星空卫视HD",
                "星空卫视 HD",
            ),
            "澳视澳门" to listOf(
                "澳视澳门HD",
                "澳视澳门 HD",
            ),
            "超级体育" to listOf(
                "超级体育HD",
                "超级体育 HD",
                "超级体育高清",
                "超级体育 高清",
                "NewTV超级体育",
                "NewTV 超级体育",
                "NewTV超级体育HD",
                "NewTV超级体育 HD",
                "NewTV超级体育高清",
                "NewTV超级体育 高清",
                "NewTV 超级体育 HD",
                "NewTV 超级体育 高清",
            ),
            "魅力潇湘" to listOf(
                "魅力潇湘HD",
                "魅力潇湘 HD",
                "魅力潇湘高清",
                "魅力潇湘 高清",
                "NewTV魅力潇湘",
                "NewTV 魅力潇湘",
                "NewTV魅力潇湘HD",
                "NewTV魅力潇湘 HD",
                "NewTV魅力潇湘高清",
                "NewTV魅力潇湘 高清",
                "NewTV 魅力潇湘 HD",
                "NewTV 魅力潇湘 高清",
            ),
            "蒙语卫视" to listOf(
                "蒙语卫视HD",
                "蒙语卫视 HD",
                "蒙语卫视高清",
                "蒙语卫视 高清",
                "内蒙古蒙语卫视",
            ),
            "安多卫视" to listOf(
                "安多卫视HD",
                "安多卫视 HD",
                "安多卫视高清",
                "安多卫视 高清",
                "青海安多卫视",
                "青海安多卫视HD",
                "青海安多卫视 HD",
                "青海安多卫视高清",
                "青海安多卫视 高清",
            ),
        )
    }
}
