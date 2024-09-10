package top.yogiczy.mytv.core.data.entities.channel

import androidx.compose.runtime.Immutable

/**
 * 频道线路
 */
@Immutable
data class ChannelLine(
    val url: String = "",
    val httpUserAgent: String? = null,
    val hybridType: HybridType = HybridType.None,
) {
    companion object {
        val EXAMPLE = ChannelLine("http://1.2.3.4", "okhttp")
    }

    enum class HybridType {
        None,
        WebView,
    }
}