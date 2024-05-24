package top.yogiczy.mytv.data.entities

/**
 * git版本
 */
data class GitRelease(
    val version: String = "0.0.0",
    val downloadUrl: String = "",
    val description: String = "",
)
