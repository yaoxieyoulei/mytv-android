package top.yogiczy.mytv.ui.screens.panel

import androidx.annotation.IntRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce

class PanelAutoCloseState internal constructor(
    @IntRange(from = 0) private val timeout: Long,
    private val onTimeout: () -> Unit = {},
) {
    private var _paused by mutableStateOf(false)

    fun active() {
        channel.trySend(timeout)
    }

    private val channel = Channel<Long>(Channel.CONFLATED)

    @OptIn(FlowPreview::class)
    suspend fun observe() {
        channel.consumeAsFlow().debounce { it }.collect {
            if (!_paused) onTimeout()
        }
    }
}

@Composable
fun rememberPanelAutoCloseState(
    @IntRange(from = 0) timeout: Long = 1000L * 10,
    onTimeout: () -> Unit = {},
    isPanelVisible: Boolean = false,
) = remember { PanelAutoCloseState(timeout = timeout, onTimeout = onTimeout) }.also {
    LaunchedEffect(it) { it.observe() }
    LaunchedEffect(isPanelVisible) { if (isPanelVisible) it.active() }
}
