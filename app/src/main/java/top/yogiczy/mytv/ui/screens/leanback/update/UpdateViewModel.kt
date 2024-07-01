package top.yogiczy.mytv.ui.screens.leanback.update

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import top.yogiczy.mytv.data.entities.GitRelease
import top.yogiczy.mytv.data.repositories.git.GitRepository
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.ui.screens.leanback.toast.LeanbackToastProperty
import top.yogiczy.mytv.ui.screens.leanback.toast.LeanbackToastState
import top.yogiczy.mytv.utils.Downloader
import top.yogiczy.mytv.utils.Logger
import top.yogiczy.mytv.utils.compareVersion
import java.io.File

class LeanBackUpdateViewModel : ViewModel() {
    private val log = Logger.create(javaClass.simpleName)

    private var _isChecking = false
    private var _isUpdating = false

    private var _isUpdateAvailable by mutableStateOf(false)
    val isUpdateAvailable get() = _isUpdateAvailable

    private var _updateDownloaded by mutableStateOf(false)
    val updateDownloaded get() = _updateDownloaded

    private var _latestRelease by mutableStateOf(GitRelease())
    val latestRelease get() = _latestRelease

    var showDialog by mutableStateOf(false)

    suspend fun checkUpdate(currentVersion: String) {
        if (_isChecking) return
        if (_isUpdateAvailable) return

        try {
            _isChecking = true
            _latestRelease = GitRepository().latestRelease(Constants.GIT_RELEASE_LATEST_URL)
            _isUpdateAvailable = _latestRelease.version.compareVersion(currentVersion) > 0
        } catch (e: Exception) {
            log.e("检查更新失败", e)
        } finally {
            _isChecking = false
        }
    }

    suspend fun downloadAndUpdate(latestFile: File) {
        if (!_isUpdateAvailable) return
        if (_isUpdating) return

        _isUpdating = true
        _updateDownloaded = false
        LeanbackToastState.I.showToast(
            "开始下载更新",
            LeanbackToastProperty.Duration.Custom(10_000),
        )

        try {
            Downloader.downloadTo(_latestRelease.downloadUrl, latestFile.path) {
                LeanbackToastState.I.showToast(
                    "正在下载更新: $it%",
                    LeanbackToastProperty.Duration.Custom(10_000),
                    "downloadProcess"
                )
            }

            _updateDownloaded = true
            LeanbackToastState.I.showToast("下载更新成功")
        } catch (ex: Exception) {
            LeanbackToastState.I.showToast("下载更新失败")
        } finally {
            _isUpdating = false
        }
    }
}
