package top.yogiczy.mytv.ui.utils

object VersionUtil {
    fun compareVersion(version1: String, version2: String): Int {
        val v1 = version1.split(".").map { it.toInt() }
        val v2 = version2.split(".").map { it.toInt() }
        val maxLength = maxOf(v1.size, v2.size)
        for (i in 0 until maxLength) {
            if (v1.getOrElse(i) { 0 } > v2.getOrElse(i) { 0 })
                return 1
            else if (v1.getOrElse(i) { 0 } < v2.getOrElse(i) { 0 })
                return -1
        }

        return 0
    }
}