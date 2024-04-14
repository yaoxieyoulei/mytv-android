package top.yogiczy.mytv.data.entities

import androidx.compose.runtime.Immutable

@Immutable
data class EpgProgrammeList(
    val value: List<EpgProgramme> = emptyList()
) : List<EpgProgramme> by value
