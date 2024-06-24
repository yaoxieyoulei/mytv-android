package top.yogiczy.mytv.utils

import java.util.regex.Pattern

fun Long.humanizeMs(): String {
    return when (this) {
        in 0..<60_000 -> "${this / 1000}秒"
        in 60_000..<3_600_000 -> "${this / 60_000}分钟"
        else -> "${this / 3_600_000}小时"
    }
}

fun String.isIPv6(): Boolean {
    val urlPattern = Pattern.compile(
        "^((http|https)://)?(\\[[0-9a-fA-F:]+])(:[0-9]+)?(/.*)?$"
    )
    return urlPattern.matcher(this).matches()
}

fun String.compareVersion(version2: String): Int {
    fun parseVersion(version: String): Pair<List<Int>, String?> {
        val mainParts = version.split("-", limit = 2)
        val versionNumbers = mainParts[0].split(".").map { it.toInt() }
        val preReleaseLabel = if (mainParts.size > 1) mainParts[1] else null
        return versionNumbers to preReleaseLabel
    }

    fun comparePreRelease(label1: String?, label2: String?): Int {
        if (label1 == null && label2 == null) return 0
        if (label1 == null) return 1 // Non-pre-release version is greater
        if (label2 == null) return -1 // Non-pre-release version is greater

        // Compare pre-release labels lexicographically
        return label1.compareTo(label2)
    }

    val (v1, preRelease1) = parseVersion(this)
    val (v2, preRelease2) = parseVersion(version2)
    val maxLength = maxOf(v1.size, v2.size)

    for (i in 0 until maxLength) {
        val part1 = v1.getOrElse(i) { 0 }
        val part2 = v2.getOrElse(i) { 0 }
        if (part1 > part2) return 1
        if (part1 < part2) return -1
    }

    // If main version parts are equal, compare pre-release labels
    return comparePreRelease(preRelease1, preRelease2)
}