package top.yogiczy.mytv.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import top.yogiczy.mytv.data.entities.IptvGroupList
import top.yogiczy.mytv.data.repositories.IptvRepository
import javax.inject.Inject

@HiltViewModel
class HomeScreeViewModel @Inject constructor(iptvRepository: IptvRepository) : ViewModel() {
    val uiState = iptvRepository.getIptvGroups()
        .map<_, HomeScreenUiState> { HomeScreenUiState.Ready(it) }
        .catch { emit(HomeScreenUiState.Error(it.message)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = HomeScreenUiState.Loading
        )
}

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState
    data class Error(val message: String?) : HomeScreenUiState
    data class Ready(
        val iptvGroupList: IptvGroupList,
    ) : HomeScreenUiState
}