package top.yogiczy.mytv.ui.screens.settings.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.yogiczy.mytv.data.entities.GithubRelease
import top.yogiczy.mytv.tvmaterial.StandardDialog
import top.yogiczy.mytv.ui.theme.MyTVTheme

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun SettingsUpdateItem(
    modifier: Modifier = Modifier,
    updateState: UpdateState = rememberUpdateState(),
) {
    var showDialog by remember { mutableStateOf(false) }

    SettingsItem(
        modifier = modifier,
        title = "应用更新",
        value = if (updateState.isUpdateAvailable) "新版本" else "无更新",
        description = "最新版本：${updateState.latestRelease.tagName}",
        onClick = {
            if (updateState.isUpdateAvailable) {
                showDialog = true
            }
        },
    )

    SettingsUpdateInfoDialog(
        showDialog = showDialog,
        onDismissRequest = { showDialog = false },
        release = updateState.latestRelease,
        onUpdateAndInstall = {
            showDialog = false
            GlobalScope.launch(Dispatchers.IO) {
                updateState.downloadAndUpdate()
            }
        },
    )
}

@OptIn(
    ExperimentalTvMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
private fun SettingsUpdateInfoDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean = false,
    onDismissRequest: () -> Unit = {},
    release: GithubRelease = GithubRelease(),
    onUpdateAndInstall: () -> Unit = {},
) {
    StandardDialog(
        modifier = modifier,
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        title = { Text(text = release.tagName) },
        text = { TvLazyColumn { item { Text(text = release.description) } } },
        confirmButton = {
            Button(
                onClick = onUpdateAndInstall,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onUpdateAndInstall() },
                    )
                },
            ) {
                Text(text = "立即更新")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onDismissRequest() },
                    )
                },
            ) {
                Text(text = "忽略")
            }
        },
    )
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsUpdateInfoDialogPreview() {
    MyTVTheme {
        SettingsUpdateInfoDialog(
            showDialog = true,
            release = GithubRelease(
                tagName = "v1.0.0",
                description = "版本更新日志".repeat(10),
            ),
        )
    }
}