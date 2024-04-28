package top.yogiczy.mytv.ui.screens.settings.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import top.yogiczy.mytv.data.entities.GithubRelease
import top.yogiczy.mytv.tvmaterial.StandardDialog
import top.yogiczy.mytv.ui.theme.MyTVTheme

@OptIn(
    ExperimentalTvMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun SettingsUpdaterDialog(
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
                    detectTapGestures(onTap = { onDismissRequest() })
                },
            ) {
                Text(text = "忽略")
            }
        },
    )
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsUpdaterDialogPreview() {
    MyTVTheme {
        SettingsUpdaterDialog(
            showDialog = true,
            release = GithubRelease(
                tagName = "v1.0.0",
                description = "版本更新日志".repeat(10),
            ),
        )
    }
}