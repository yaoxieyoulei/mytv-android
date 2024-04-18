package top.yogiczy.mytv.ui.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
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
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreeViewModel @Inject constructor(
    iptvRepository: IptvRepository,
    epgRepository: EpgRepository,
) : ViewModel() {
    var uiState = mutableStateOf<HomeScreenUiState>(HomeScreenUiState.Loading(""))

    init {
        viewModelScope.launch {
            flow { emit(iptvRepository.getIptvGroups()) }.retryWhen { _, attempt ->
                if (attempt >= Constants.HTTP_RETRY_COUNT) return@retryWhen false

                uiState.value =
                    HomeScreenUiState.Loading("获取远程直播源(${attempt + 1}/${Constants.HTTP_RETRY_COUNT})...")
                delay(Constants.HTTP_RETRY_INTERVAL)
                true
            }.catch { uiState.value = HomeScreenUiState.Error(it.message) }.map {
                uiState.value = HomeScreenUiState.Ready(iptvGroupList = it)
                it
            }
                // 开始获取epg
                .flatMapLatest { iptvGroupList ->
                    val channels =
                        iptvGroupList.flatMap { it.iptvs }.map { iptv -> iptv.channelName }
                    flow { emit(epgRepository.getEpgs(channels)) }
                }.retry(Constants.HTTP_RETRY_COUNT) { delay(Constants.HTTP_RETRY_INTERVAL); true }
                .catch { emit(EpgList()) }.map { epgList ->
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
                }.map { epgList ->
                    uiState.value =
                        (uiState.value as HomeScreenUiState.Ready).copy(epgList = epgList)
                }.collect()
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