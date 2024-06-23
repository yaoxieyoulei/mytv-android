package top.yogiczy.mytv.ui.utils

import java.util.regex.Pattern

object IpUtil {
    fun String.isIPv6(): Boolean {
        val urlPattern = Pattern.compile(
            "^((http|https)://)?(\\[[0-9a-fA-F:]+])(:[0-9]+)?(/.*)?$"
        )
        return urlPattern.matcher(this).matches()
    }
}