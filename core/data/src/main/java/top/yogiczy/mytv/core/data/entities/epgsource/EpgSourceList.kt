package top.yogiczy.mytv.core.data.entities.epgsource

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * 节目单来源列表
 */
@Serializable
@Immutable
data class EpgSourceList(
    val value: List<EpgSource> = emptyList(),
) : List<EpgSource> by value