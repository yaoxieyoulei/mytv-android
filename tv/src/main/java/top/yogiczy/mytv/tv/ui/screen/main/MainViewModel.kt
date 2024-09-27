package top.yogiczy.mytv.tv.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList.Companion.channelList
import top.yogiczy.mytv.core.data.entities.channel.ChannelLineList
import top.yogiczy.mytv.core.data.entities.channel.ChannelList
import top.yogiczy.mytv.core.data.entities.epg.EpgList
import top.yogiczy.mytv.core.data.entities.epg.EpgList.Companion.match
import top.yogiczy.mytv.core.data.entities.epgsource.EpgSource
import top.yogiczy.mytv.core.data.network.HttpException
import top.yogiczy.mytv.core.data.repositories.epg.EpgRepository
import top.yogiczy.mytv.core.data.repositories.iptv.IptvRepository
import top.yogiczy.mytv.core.data.utils.ChannelAlias
import top.yogiczy.mytv.core.data.utils.ChannelUtil
import top.yogiczy.mytv.core.data.utils.Constants
import top.yogiczy.mytv.tv.sync.CloudSync
import top.yogiczy.mytv.tv.sync.CloudSyncDate
import top.yogiczy.mytv.tv.ui.material.Snackbar
import top.yogiczy.mytv.tv.ui.material.SnackbarType
import top.yogiczy.mytv.tv.ui.utils.Configs

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var _lastJob: Job? = null

    var onCloudSyncDone: () -> Unit = {}

    init {
        viewModelScope.launch {
            pullCloudSyncData()
            init()
        }
    }

    fun init() {
        _lastJob?.cancel()
        _lastJob = viewModelScope.launch {
            ChannelAlias.refresh()
            refreshChannel()
            refreshEpg()
            mergeEpgMetadata()
        }
    }

    private suspend fun pullCloudSyncData() {
        if (!Configs.cloudSyncAutoPull) return

        _uiState.value = MainUiState.Loading("拉取云端数据")
        runCatching {
            val syncData = CloudSync.pull()

            if (syncData != CloudSyncDate.EMPTY) {
                syncData.apply()
                onCloudSyncDone()
            }
        }
    }

    private suspend fun refreshChannel() {
        _uiState.value = MainUiState.Loading("加载直播源")

        flow {
            emit(
                IptvRepository(Configs.iptvSourceCurrent)
                    .getChannelGroupList(
                        Configs.iptvSourceCacheTime,
                        Configs.iptvChannelLogoProvider,
                        Configs.iptvChannelLogoOverride,
                    )
            )
        }
            .retryWhen { e, attempt ->
                if (attempt >= Constants.NETWORK_RETRY_COUNT) return@retryWhen false
                if (e !is HttpException) return@retryWhen false

                _uiState.value =
                    MainUiState.Loading("加载直播源(${attempt + 1}/${Constants.NETWORK_RETRY_COUNT})...")
                delay(Constants.NETWORK_RETRY_INTERVAL)
                true
            }
            .catch {
                _uiState.value = MainUiState.Error(it.message)
            }
            .map { mergeSimilarChannel(it) }
            .map { hybridChannel(it) }
            .map {
                _uiState.value = MainUiState.Ready(channelGroupList = it)
                it
            }
            .collect()
    }

    private suspend fun mergeSimilarChannel(channelGroupList: ChannelGroupList) =
        withContext(Dispatchers.Default) {
            if (!Configs.iptvSimilarChannelMerge) return@withContext channelGroupList

            _uiState.value = MainUiState.Loading("合并相似频道")
            return@withContext ChannelGroupList(channelGroupList.map { group ->
                group.copy(
                    channelList = ChannelList(group.channelList
                        .groupBy { channel ->
                            ChannelAlias.standardChannelName(channel.name)
                        }
                        .map { nameEntry ->
                            nameEntry.value.first().copy(
                                name = nameEntry.key,
                                lineList = ChannelLineList(nameEntry.value
                                    .map { channel ->
                                        if (nameEntry.value.size > 1) {
                                            channel.lineList.map { line -> line.copy(name = channel.name) }
                                        } else channel.lineList
                                    }
                                    .flatten()
                                    .distinctBy { it.url })
                            )
                        })
                )
            })
        }

    private suspend fun hybridChannel(channelGroupList: ChannelGroupList) =
        withContext(Dispatchers.Default) {
            _uiState.value = MainUiState.Loading("混合直播源")

            val hybridMode = Configs.iptvHybridMode
            return@withContext when (hybridMode) {
                Configs.IptvHybridMode.DISABLE -> channelGroupList
                Configs.IptvHybridMode.IPTV_FIRST -> {
                    ChannelGroupList(channelGroupList.map { group ->
                        group.copy(channelList = ChannelList(group.channelList.map { channel ->
                            channel.copy(
                                lineList = ChannelLineList(
                                    channel.lineList + ChannelUtil.getHybridWebViewLines(channel.name)
                                )
                            )
                        }))
                    })
                }

                Configs.IptvHybridMode.HYBRID_FIRST -> {
                    ChannelGroupList(channelGroupList.map { group ->
                        group.copy(channelList = ChannelList(group.channelList.map { channel ->
                            channel.copy(
                                lineList = ChannelLineList(
                                    ChannelUtil.getHybridWebViewLines(channel.name) + channel.lineList
                                )
                            )
                        }))
                    })
                }
            }
        }

    private suspend fun refreshEpg() {
        if (!Configs.epgEnable) return

        if (_uiState.value is MainUiState.Ready) {
            val channelGroupList = (_uiState.value as MainUiState.Ready).channelGroupList

            flow {
                val epgSource = if (Configs.epgSourceFollowIptv) {
                    val iptvRepository = IptvRepository(Configs.iptvSourceCurrent)
                    iptvRepository.getEpgUrl()?.let { epgUrl -> EpgSource(url = epgUrl) }
                        ?: Configs.epgSourceCurrent
                } else Configs.epgSourceCurrent

                emit(
                    EpgRepository(epgSource).getEpgList(
                        refreshTimeThreshold = Configs.epgRefreshTimeThreshold,
                    )
                )
            }
                .retryWhen { e, attempt ->
                    if (attempt >= Constants.NETWORK_RETRY_COUNT) return@retryWhen false
                    if (e !is HttpException) return@retryWhen false

                    delay(Constants.NETWORK_RETRY_INTERVAL)
                    true
                }
                .catch {
                    emit(EpgList())
                    Snackbar.show("节目单获取失败，请检查网络连接", type = SnackbarType.ERROR)
                }
                .map { epgList ->
                    val filteredChannels =
                        channelGroupList.channelList.map { it.epgName.lowercase() }

                    EpgList(epgList.filter { epg -> epg.channelList.any { it.lowercase() in filteredChannels } })
                }
                .map { epgList ->
                    _uiState.value = (_uiState.value as MainUiState.Ready).copy(epgList = epgList)
                }
                .collect()
        }
    }

    private suspend fun mergeEpgMetadata() = withContext(Dispatchers.Default) {
        if (_uiState.value is MainUiState.Ready) {
            val channelGroupList = (_uiState.value as MainUiState.Ready).channelGroupList
            val epgList = (_uiState.value as MainUiState.Ready).epgList

            if (epgList.all { epg -> epg.logo == null }) return@withContext

            _uiState.value = (_uiState.value as MainUiState.Ready).copy(
                channelGroupList = ChannelGroupList(channelGroupList.map { group ->
                    group.copy(channelList = ChannelList(group.channelList.map { channel ->
                        channel.copy(
                            logo = epgList.match(channel)?.logo ?: channel.logo
                        )
                    }))
                }),
                epgList = epgList,
            )
        }
    }
}

sealed interface MainUiState {
    data class Loading(val message: String? = null) : MainUiState
    data class Error(val message: String? = null) : MainUiState
    data class Ready(
        val channelGroupList: ChannelGroupList = ChannelGroupList(),
        val epgList: EpgList = EpgList(),
    ) : MainUiState
}