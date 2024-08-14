package top.yogiczy.mytv.tv.ui

import androidx.annotation.IntRange
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import top.yogiczy.mytv.tv.ui.material.Padding
import top.yogiczy.mytv.tv.ui.material.Snackbar
import top.yogiczy.mytv.tv.ui.screens.agreement.AgreementScreen
import top.yogiczy.mytv.tv.ui.screens.main.MainScreen
import top.yogiczy.mytv.tv.ui.screens.settings.LocalSettings
import top.yogiczy.mytv.tv.ui.screens.settings.LocalSettingsCurrent
import top.yogiczy.mytv.tv.ui.screens.settings.SettingsViewModel

@Composable
fun App(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
    settingsViewModel: SettingsViewModel = viewModel(),
) {
    val configuration = LocalConfiguration.current
    val doubleBackPressedExitState = rememberDoubleBackPressedExitState()

    CompositionLocalProvider(
        LocalDensity provides Density(
            density = LocalDensity.current.density * when (settingsViewModel.uiDensityScaleRatio) {
                0f -> configuration.screenWidthDp.toFloat() / 960
                else -> settingsViewModel.uiDensityScaleRatio
            },
            fontScale = LocalDensity.current.fontScale * settingsViewModel.uiFontScaleRatio,
        ),
        LocalSettings provides LocalSettingsCurrent(
            uiFocusOptimize = settingsViewModel.uiFocusOptimize,
        ),
    ) {
        if (settingsViewModel.appAgreementAgreed) {
            MainScreen(
                modifier = modifier,
                onBackPressed = {
                    if (doubleBackPressedExitState.allowExit) {
                        onBackPressed()
                    } else {
                        doubleBackPressedExitState.backPress()
                        Snackbar.show("再按一次退出")
                    }
                },
            )
        } else {
            AgreementScreen(
                onAgree = { settingsViewModel.appAgreementAgreed = true },
                onDisagree = { onBackPressed() },
            )
        }
    }
}

/**
 * 退出应用二次确认
 */
class DoubleBackPressedExitState internal constructor(
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
fun rememberDoubleBackPressedExitState(@IntRange(from = 0) resetSeconds: Int = 2) =
    remember { DoubleBackPressedExitState(resetSeconds = resetSeconds) }
        .also { LaunchedEffect(it) { it.observe() } }

val ParentPadding = PaddingValues(vertical = 24.dp, horizontal = 58.dp)

@Composable
fun rememberChildPadding(direction: LayoutDirection = LocalLayoutDirection.current) =
    remember {
        Padding(
            start = ParentPadding.calculateStartPadding(direction),
            top = ParentPadding.calculateTopPadding(),
            end = ParentPadding.calculateEndPadding(direction),
            bottom = ParentPadding.calculateBottomPadding()
        )
    }
