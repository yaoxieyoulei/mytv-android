package top.yogiczy.mytv.ui.screens.leanback.update

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yogiczy.mytv.AppGlobal
import top.yogiczy.mytv.ui.screens.leanback.settings.LeanbackSettingsViewModel
import top.yogiczy.mytv.ui.screens.leanback.toast.LeanbackToastState
import top.yogiczy.mytv.ui.screens.leanback.update.components.LeanbackUpdateDialog
import top.yogiczy.mytv.utils.ApkInstaller
import java.io.File

@Composable
fun LeanbackUpdateScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: LeanbackSettingsViewModel = viewModel(),
    updateViewModel: LeanBackUpdateViewModel = viewModel(),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val packageInfo = rememberPackageInfo()
    val latestFile = remember { File(AppGlobal.cacheDir, "latest.apk") }

    LaunchedEffect(Unit) {
        delay(3000)
        updateViewModel.checkUpdate(packageInfo.versionName)

        val latestRelease = updateViewModel.latestRelease
        if (
            updateViewModel.isUpdateAvailable &&
            latestRelease.version != settingsViewModel.appLastLatestVersion
        ) {
            settingsViewModel.appLastLatestVersion = latestRelease.version

            if (settingsViewModel.updateForceRemind) {
                updateViewModel.showDialog = true
            } else {
                LeanbackToastState.I.showToast("新版本: v${latestRelease.version}")
            }
        }
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (context.packageManager.canRequestPackageInstalls()) {
                    ApkInstaller.installApk(context, latestFile.path)
                } else {
                    LeanbackToastState.I.showToast("未授予安装权限")
                }
            }
        }

    LaunchedEffect(updateViewModel.updateDownloaded) {
        if (!updateViewModel.updateDownloaded) return@LaunchedEffect

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ApkInstaller.installApk(context, latestFile.path)
        } else {
            if (context.packageManager.canRequestPackageInstalls()) {
                ApkInstaller.installApk(context, latestFile.path)
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                launcher.launch(intent)
            }
        }
    }

    LeanbackUpdateDialog(
        modifier = modifier,
        showDialogProvider = { updateViewModel.showDialog },
        onDismissRequest = { updateViewModel.showDialog = false },
        releaseProvider = { updateViewModel.latestRelease },
        onUpdateAndInstall = {
            updateViewModel.showDialog = false
            coroutineScope.launch(Dispatchers.IO) {
                updateViewModel.downloadAndUpdate(latestFile)
            }
        },
    )
}

@Composable
private fun rememberPackageInfo(context: Context = LocalContext.current): PackageInfo =
    context.packageManager.getPackageInfo(context.packageName, 0)
