package top.yogiczy.mytv.ui

import androidx.annotation.IntRange
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import top.yogiczy.mytv.ui.screens.leanback.components.LeanbackPadding
import top.yogiczy.mytv.ui.screens.leanback.main.LeanbackMainScreen
import top.yogiczy.mytv.ui.screens.leanback.toast.LeanbackToastScreen
import top.yogiczy.mytv.ui.screens.leanback.toast.LeanbackToastState

@Composable
fun LeanbackApp(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
) {
    val context = LocalContext.current
    val doubleBackPressedExitState = rememberLeanbackDoubleBackPressedExitState()

    LeanbackToastScreen()
    LeanbackMainScreen(
        modifier = modifier,
        onBackPressed = {
            if (doubleBackPressedExitState.allowExit) {
                onBackPressed()
            } else {
                doubleBackPressedExitState.backPress()
                LeanbackToastState.I.showToast("再按一次退出")
            }
        },
    )
}


/**
 * 退出应用二次确认
 */
class LeanbackDoubleBackPressedExitState internal constructor(
    @IntRange(from = 0)
    private val resetSeconds: Int,
) {
    private var _allowExit by mutableStateOf(false)
    val allowExit get() = _allowExit

    fun backPress() {
        _allowExit = true
        channel.trySend(resetSeconds)
    }

    private val channel = Channel<Int>(Channel.CONFLATED)

    @OptIn(FlowPreview::class)
    suspend fun observe() {
        channel.consumeAsFlow()
            .debounce { it.toLong() * 1000 }
            .collect { _allowExit = false }
    }
}

/**
 * 退出应用二次确认状态
 */
@Composable
fun rememberLeanbackDoubleBackPressedExitState(@IntRange(from = 0) resetSeconds: Int = 2) =
    remember { LeanbackDoubleBackPressedExitState(resetSeconds = resetSeconds) }
        .also { LaunchedEffect(it) { it.observe() } }

val LeanbackParentPadding = PaddingValues(vertical = 12.dp, horizontal = 24.dp)

@Composable
fun rememberLeanbackChildPadding(direction: LayoutDirection = LocalLayoutDirection.current) =
    remember {
        LeanbackPadding(
            start = LeanbackParentPadding.calculateStartPadding(direction) + 8.dp,
            top = LeanbackParentPadding.calculateTopPadding(),
            end = LeanbackParentPadding.calculateEndPadding(direction) + 8.dp,
            bottom = LeanbackParentPadding.calculateBottomPadding()
        )
    }
