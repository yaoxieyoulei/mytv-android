package top.yogiczy.mytv.ui.screens.leanback.toast

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import top.yogiczy.mytv.ui.screens.leanback.toast.LeanbackToastProperty.Companion.toMs

@Stable
class LeanbackToastState {
    private var _visible by mutableStateOf(false)
    val visible get() = _visible

    private var _current by mutableStateOf(LeanbackToastProperty())
    val current get() = _current

    private fun showToast(toast: LeanbackToastProperty) {
        // TODO 消息变化较生硬
        _current = toast
        _visible = true
        channel.trySend(toast.duration.toMs())
    }

    fun showToast(
        message: String,
        duration: LeanbackToastProperty.Duration = LeanbackToastProperty.Duration.Default,
    ) {
        showToast(LeanbackToastProperty(message = message, duration = duration))
    }

    private val channel = Channel<Int>(Channel.CONFLATED)

    @OptIn(FlowPreview::class)
    suspend fun observe() {
        channel.consumeAsFlow().debounce { it.toLong() }.collect { _visible = false }
    }

    companion object {
        // TODO 这种方法可能违反了 Compose 的规则
        lateinit var I: LeanbackToastState
    }
}

@Composable
fun rememberLeanbackToastState() = remember { LeanbackToastState() }.also {
    LeanbackToastState.I = it
    LaunchedEffect(it) { it.observe() }
}

data class LeanbackToastProperty(
    val message: String = "",
    val duration: Duration = Duration.Default,
) {
    sealed interface Duration {
        data object Default : Duration
        data class Custom(val duration: Int) : Duration
    }

    companion object {
        fun Duration.toMs(): Int = when (val it = this) {
            is Duration.Default -> 2300
            is Duration.Custom -> it.duration
        }
    }
}