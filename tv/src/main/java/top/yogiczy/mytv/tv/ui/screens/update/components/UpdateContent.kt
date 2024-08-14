package top.yogiczy.mytv.tv.ui.screens.update.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.WideButton
import top.yogiczy.mytv.core.data.entities.git.GitRelease
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.tooling.PreviewWithLayoutGrids
import top.yogiczy.mytv.tv.ui.utils.customBackground
import top.yogiczy.mytv.tv.ui.utils.focusOnLaunched
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents

@Composable
fun UpdateContent(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    releaseProvider: () -> GitRelease = { GitRelease() },
    isUpdateAvailableProvider: () -> Boolean = { false },
    onUpdateAndInstall: () -> Unit = {},
) {
    val release = releaseProvider()

    Row(
        modifier = modifier
            .fillMaxSize()
            .customBackground()
            .padding(horizontal = 130.dp, vertical = 88.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.width(340.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "最新版本: v${release.version}",
                style = MaterialTheme.typography.headlineMedium
            )

            LazyColumn {
                item {
                    Text(release.description, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        if (isUpdateAvailableProvider()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                WideButton(
                    modifier = Modifier
                        .focusOnLaunched()
                        .handleKeyEvents(onSelect = onUpdateAndInstall),
                    onClick = { },
                    title = { Text("立即更新") },
                )

                WideButton(
                    modifier = Modifier.handleKeyEvents(onSelect = onDismissRequest),
                    onClick = { },
                    title = { Text("忽略") },
                )
            }
        } else {
            WideButton(
                modifier = Modifier
                    .focusOnLaunched()
                    .handleKeyEvents(onSelect = onDismissRequest),
                onClick = { },
                title = { Text("当前为最新版本") },
            )
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun UpdateDialogPreview() {
    MyTVTheme {
        PreviewWithLayoutGrids {
            UpdateContent(
                releaseProvider = {
                    GitRelease(
                        version = "1.0.0",
                        downloadUrl = "",
                        description = "更新日志".repeat(100),
                    )
                },
            )
        }
    }
}