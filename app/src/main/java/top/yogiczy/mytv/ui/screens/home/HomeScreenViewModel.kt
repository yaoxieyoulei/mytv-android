package top.yogiczy.mytv.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import top.yogiczy.mytv.data.entities.EpgList
import top.yogiczy.mytv.data.entities.EpgProgrammeList
import top.yogiczy.mytv.data.entities.IptvGroupList
import top.yogiczy.mytv.data.repositories.EpgRepository
import top.yogiczy.mytv.data.repositories.IptvRepository
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.ui.utils.SP

class HomeScreeViewModel : ViewModel() {
    private val iptvRepository = IptvRepository()
    private val epgRepository = EpgRepository()

    private val _uiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading(""))
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // 获取直播源
            flow { emit(iptvRepository.getIptvGroups()) }
                .retryWhen { _, attempt ->
                    if (attempt >= Constants.HTTP_RETRY_COUNT) return@retryWhen false

                    _uiState.value =
                        HomeScreenUiState.Loading("获取远程直播源(${attempt + 1}/${Constants.HTTP_RETRY_COUNT})...")
                    delay(Constants.HTTP_RETRY_INTERVAL)
                    true
                }
                .catch {
                    _uiState.value = HomeScreenUiState.Error(it.message)
                    SP.iptvSourceUrlHistoryList -= SP.iptvSourceUrl
                }
                .map {
                    _uiState.value = HomeScreenUiState.Ready(iptvGroupList = it)
                    SP.iptvSourceUrlHistoryList += SP.iptvSourceUrl
                    it
                }
                .collect()

            // 开始获取epg
            if (_uiState.value is HomeScreenUiState.Ready) {
                val channels = (_uiState.value as HomeScreenUiState.Ready)
                    .iptvGroupList.flatMap { it.iptvs }
                    .map { iptv -> iptv.channelName }

                flow { emit(epgRepository.getEpgs(channels)) }
                    .retry(Constants.HTTP_RETRY_COUNT) { delay(Constants.HTTP_RETRY_INTERVAL); true }
                    .catch {
                        emit(EpgList())
                        SP.epgXmlUrlHistoryList -= SP.epgXmlUrl
                    }
                    .map { epgList ->
                        // 移除过期节目
                        epgList.copy(value = epgList.map { epg ->
                            epg.copy(
                                programmes = EpgProgrammeList(
                                    epg.programmes.filter { programme ->
                                        System.currentTimeMillis() < programme.endAt
                                    },
                                )
                            )
                        })
                    }
                    .map { epgList ->
                        _uiState.value =
                            (_uiState.value as HomeScreenUiState.Ready).copy(epgList = epgList)
                        SP.epgXmlUrlHistoryList += SP.epgXmlUrl
                    }
                    .collect()
            }
        }
    }
}

sealed interface HomeScreenUiState {
    data class Loading(val message: String?) : HomeScreenUiState
    data class Error(val message: String?) : HomeScreenUiState
    data class Ready(
        val iptvGroupList: IptvGroupList = IptvGroupList(),
        val epgList: EpgList = EpgList(),
    ) : HomeScreenUiState
}