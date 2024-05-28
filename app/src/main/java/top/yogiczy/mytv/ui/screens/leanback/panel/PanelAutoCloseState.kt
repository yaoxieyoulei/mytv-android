package top.yogiczy.mytv.ui.screens.leanback.panel

import androidx.annotation.IntRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce

@Stable
class PanelAutoCloseState internal constructor(
    @IntRange(from = 0) private val timeout: Long,
    private val onTimeout: () -> Unit = {},
) {
    fun active() {
        channel.trySend(timeout)
    }

    private val channel = Channel<Long>(Channel.CONFLATED)

    @OptIn(FlowPreview::class)
    suspend fun observe() {
        channel.consumeAsFlow().debounce { it }.collect {
            onTimeout()
        }
    }
}

@Composable
fun rememberPanelAutoCloseState(
    @IntRange(from = 0) timeout: Long = 1000L * 15,
    onTimeout: () -> Unit = {},
) = remember { PanelAutoCloseState(timeout = timeout, onTimeout = onTimeout) }.also {
    LaunchedEffect(it) { it.observe() }
}