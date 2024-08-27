package top.yogiczy.mytv.core.data.entities.epgsource

import kotlinx.serialization.Serializable

/**
 * 节目单来源
 */
@Serializable
data class EpgSource(
    /**
     * 名称
     */
    val name: String = "",

    /**
     * 链接
     */
    val url: String = "",
) {
    companion object {
        val EXAMPLE = EpgSource(
            name = "测试节目单1",
            url = "http://1.2.3.4/all.xml",
        )
    }
}