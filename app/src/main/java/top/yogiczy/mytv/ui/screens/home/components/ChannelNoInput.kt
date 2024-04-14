package top.yogiczy.mytv.ui.screens.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.screens.panel.components.PanelChannelNo

@Composable
fun ChannelNoInput(
    modifier: Modifier = Modifier,
    state: ChannelNoInputState = rememberChannelNoInputState(),
) {
    val childPadding = rememberChildPadding()

    Box(modifier = modifier.fillMaxSize()) {
        PanelChannelNo(
            channelNo = state.channelNo,
            modifier = Modifier
                .padding(top = childPadding.top, end = childPadding.end)
                .align(Alignment.TopEnd),
        )
    }
}

class ChannelNoInputState internal constructor(
    private val onChannelNoConfirm: (String) -> Unit,
) {
    private var _channelNo by mutableStateOf("")
    val channelNo get() = _channelNo

    fun input(no: Int) {
        _channelNo += no.toString()
        channel.trySend(_channelNo)
    }

    private val channel = Channel<String>(Channel.CONFLATED)

    @OptIn(FlowPreview::class)
    suspend fun observe() {
        channel.consumeAsFlow().debounce { (4 - it.length) * 1000L }.collect {
            onChannelNoConfirm(it)
            _channelNo = ""
        }
    }
}

@Composable
fun rememberChannelNoInputState(onChannelNoConfirm: (String) -> Unit = {}) =
    remember { ChannelNoInputState(onChannelNoConfirm) }.also { LaunchedEffect(it) { it.observe() } }
