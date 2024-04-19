package top.yogiczy.mytv.ui.screens.settings.components

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import top.yogiczy.mytv.data.entities.GithubRelease
import top.yogiczy.mytv.data.repositories.GithubRepository
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.ui.screens.toast.ToastProperty
import top.yogiczy.mytv.ui.screens.toast.ToastState
import top.yogiczy.mytv.ui.utils.ApkInstaller
import top.yogiczy.mytv.ui.utils.DownloadUtil
import top.yogiczy.mytv.ui.utils.SP
import java.io.File

@Stable
data class UpdateState(
    private val context: Context,
    private val packageInfo: PackageInfo,
    private val coroutineScope: CoroutineScope,
    val latestFile: File = File(context.cacheDir, "latest.apk"),
) {
    private var _isChecking by mutableStateOf(false)

    private var _isUpdating by mutableStateOf(false)

    private var _isUpdateAvailable by mutableStateOf(false)
    val isUpdateAvailable get() = _isUpdateAvailable

    private var _updateDownloaded by mutableStateOf(false)
    val updateDownloaded get() = _updateDownloaded

    private var _latestRelease by mutableStateOf(GithubRelease())
    val latestRelease get() = _latestRelease

    suspend fun checkUpdate() {
        if (_isChecking) return
        if (_isUpdateAvailable) return

        try {
            _isChecking = true
            _latestRelease = GithubRepository().latestRelease()
            _isUpdateAvailable = compareVersion(
                _latestRelease.tagName.substring(1), packageInfo.versionName
            ) > 0

            if (_isUpdateAvailable && _latestRelease.tagName != SP.appLastLatestVersion) {
                ToastState.I.showToast("新版本: ${_latestRelease.tagName}")
                SP.appLastLatestVersion = _latestRelease.tagName
            }
        } catch (e: Exception) {
            ToastState.I.showToast("检查更新失败")
        } finally {
            _isChecking = false
        }
    }

    suspend fun downloadAndUpdate() {
        if (!_isUpdateAvailable) return
        if (_isUpdating) return

        _isUpdating = true
        _updateDownloaded = false
        ToastState.I.showToast("开始下载更新", ToastProperty.Duration.Custom(10_000))

        try {
            DownloadUtil.downloadTo(
                "${Constants.GITHUB_PROXY}${_latestRelease.downloadUrl}", latestFile.path
            ) {
                ToastState.I.showToast(
                    "正在下载更新: $it%",
                    ToastProperty.Duration.Custom(10_000),
                )
            }

            _updateDownloaded = true
            ToastState.I.showToast("下载更新成功")
        } catch (ex: Exception) {
            ToastState.I.showToast("下载更新失败")
        } finally {
            _isUpdating = false
        }
    }
}

@Composable
fun rememberUpdateState(
    context: Context = LocalContext.current,
): UpdateState {
    val coroutineScope = rememberCoroutineScope()
    val packageInfo = rememberPackageInfo()

    val state = remember {
        UpdateState(
            context = context,
            packageInfo = packageInfo,
            coroutineScope = coroutineScope,
        )
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (context.packageManager.canRequestPackageInstalls()) ApkInstaller.installApk(
                    context, state.latestFile.path
                )
                else ToastState.I.showToast("未授予安装权限")
            }
        }

    LaunchedEffect(state.updateDownloaded) {
        if (!state.updateDownloaded) return@LaunchedEffect

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ApkInstaller.installApk(context, state.latestFile.path)
        } else {
            if (context.packageManager.canRequestPackageInstalls()) {
                ApkInstaller.installApk(context, state.latestFile.path)
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                launcher.launch(intent)
            }
        }
    }

    return state
}

private fun compareVersion(version1: String, version2: String): Int {
    val v1 = version1.split(".").map { it.toInt() }
    val v2 = version2.split(".").map { it.toInt() }
    val maxLength = maxOf(v1.size, v2.size)
    for (i in 0 until maxLength) {
        if (v1.getOrElse(i) { 0 } > v2.getOrElse(i) { 0 }) return 1
        else if (v1.getOrElse(i) { 0 } < v2.getOrElse(i) { 0 }) return -1
    }

    return 0
}