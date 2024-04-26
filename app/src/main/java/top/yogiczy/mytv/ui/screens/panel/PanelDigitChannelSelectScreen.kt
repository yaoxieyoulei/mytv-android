package top.yogiczy.mytv.ui.screens.panel

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
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.screens.panel.components.PanelChannelNo
import top.yogiczy.mytv.ui.theme.MyTVTheme

@Composable
fun PanelDigitChannelSelectScreen(
    modifier: Modifier = Modifier,
    state: DigitChannelSelectState = rememberDigitChannelSelectState(),
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

@Preview(device = "id:Android TV (720p)")
@Composable
private fun PanelDigitChannelSelectPreview() {
    MyTVTheme {
        PanelDigitChannelSelectScreen(
            state = DigitChannelSelectState(initialChannelNo = "123")
        )
    }
}

class DigitChannelSelectState internal constructor(
    private val onChannelNoConfirm: (String) -> Unit = {},
    initialChannelNo: String = "",
) {
    private var _channelNo by mutableStateOf(initialChannelNo)
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
fun rememberDigitChannelSelectState(onChannelNoConfirm: (String) -> Unit = {}) =
    remember { DigitChannelSelectState(onChannelNoConfirm) }.also { LaunchedEffect(it) { it.observe() } }
