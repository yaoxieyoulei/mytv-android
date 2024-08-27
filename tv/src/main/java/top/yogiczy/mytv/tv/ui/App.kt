package top.yogiczy.mytv.tv.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import top.yogiczy.mytv.core.data.entities.iptvsource.IptvSource.Companion.needExternalStoragePermission
import top.yogiczy.mytv.tv.ui.material.Padding
import top.yogiczy.mytv.tv.ui.material.PopupHandleableApplication
import top.yogiczy.mytv.tv.ui.material.Snackbar
import top.yogiczy.mytv.tv.ui.material.SnackbarType
import top.yogiczy.mytv.tv.ui.material.SnackbarUI
import top.yogiczy.mytv.tv.ui.material.Visible
import top.yogiczy.mytv.tv.ui.screens.agreement.AgreementScreen
import top.yogiczy.mytv.tv.ui.screens.main.MainScreen
import top.yogiczy.mytv.tv.ui.screens.settings.LocalSettings
import top.yogiczy.mytv.tv.ui.screens.settings.LocalSettingsCurrent
import top.yogiczy.mytv.tv.ui.screens.settings.SettingsViewModel
import top.yogiczy.mytv.tv.ui.tooling.PreviewWithLayoutGrids

@Composable
fun App(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
    settingsViewModel: SettingsViewModel = viewModel(),
) {
    if (settingsViewModel.iptvSourceCurrent.needExternalStoragePermission()) {
        requestExternalStoragePermission()
    }

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
        PopupHandleableApplication {
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
                    onDisableUiFocusOptimize = { settingsViewModel.uiFocusOptimize = false },
                )
            }
        }

        SnackbarUI()
        Visible({ settingsViewModel.debugShowLayoutGrids }) { PreviewWithLayoutGrids { } }
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

@Composable
fun requestExternalStoragePermission(): Boolean {
    val context = LocalContext.current

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        fun isPermissionGranted(): Boolean {
            return Environment.isExternalStorageManager()
        }

        var permissionGranted by remember { mutableStateOf(isPermissionGranted()) }

        val intentLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) {
            permissionGranted = isPermissionGranted()
        }

        LaunchedEffect(Unit) {
            if (!permissionGranted) {
                try {
                    val intent =
                        Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                            data = Uri.parse("package:${context.packageName}")
                        }
                    intentLauncher.launch(intent)
                } catch (ex: Exception) {
                    Snackbar.show("无法找到管理全部文件设置项", type = SnackbarType.ERROR)
                }
            }
        }

        return permissionGranted
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var permissionGranted by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            )
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted -> permissionGranted = isGranted }

        LaunchedEffect(Unit) {
            if (!permissionGranted) {
                launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        return permissionGranted
    } else {
        return true
    }
}