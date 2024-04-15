package top.yogiczy.mytv.ui.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import top.yogiczy.mytv.data.entities.EpgList
import top.yogiczy.mytv.data.entities.EpgProgrammeList
import top.yogiczy.mytv.data.entities.IptvGroupList
import top.yogiczy.mytv.data.repositories.EpgRepository
import top.yogiczy.mytv.data.repositories.IptvRepository
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreeViewModel @Inject constructor(
    iptvRepository: IptvRepository,
    epgRepository: EpgRepository,
) : ViewModel() {
    var uiState = mutableStateOf<HomeScreenUiState>(HomeScreenUiState.Loading)

    init {
        viewModelScope.launch {
            try {
                val iptvGroupList = iptvRepository.getIptvGroups().first()
                uiState.value = HomeScreenUiState.Ready(iptvGroupList = iptvGroupList)

                val epgList = EpgList(epgRepository.getEpgs(iptvGroupList.flatMap { it.iptvs }
                    .map { iptv -> iptv.channelName }).first().map { epg ->
                    epg.copy(
                        programmes = EpgProgrammeList(
                            epg.programmes.filter { programme ->
                                System.currentTimeMillis() < programme.endAt
                            },
                        )
                    )
                })

                uiState.value =
                    HomeScreenUiState.Ready(iptvGroupList = iptvGroupList, epgList = epgList)

            } catch (e: Exception) {
                uiState.value = HomeScreenUiState.Error(e.message)
            }

        }
    }
}

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState
    data class Error(val message: String?) : HomeScreenUiState
    data class Ready(
        val iptvGroupList: IptvGroupList = IptvGroupList(),
        val epgList: EpgList = EpgList(),
    ) : HomeScreenUiState
}