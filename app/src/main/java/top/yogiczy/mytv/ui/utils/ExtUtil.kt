package top.yogiczy.mytv.ui.utils

object ExtUtil {
    val Long.humanizeMs: String
        get() = when (this) {
            in 0..<60_000 -> "${this / 1000}秒"
            in 60_000..<3_600_000 -> "${this / 60_000}分钟"
            else -> "${this / 3_600_000}小时"
        }
}