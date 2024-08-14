package top.yogiczy.mytv.tv.ui.screens.guide

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.WideButton
import top.yogiczy.mytv.tv.ui.screens.guide.components.GuideTvRemote
import top.yogiczy.mytv.tv.ui.screens.guide.components.GuideTvRemoteKeys
import top.yogiczy.mytv.tv.ui.screens.guide.components.LocalGuideTvRemoteActiveKeys
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.tooling.PreviewWithLayoutGrids
import top.yogiczy.mytv.tv.ui.utils.captureBackKey
import top.yogiczy.mytv.tv.ui.utils.customBackground
import top.yogiczy.mytv.tv.ui.utils.focusOnLaunched
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents

@Composable
fun GuideScreen(
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
) {
    var activeKey by remember { mutableStateOf(GuideTvRemoteKeys.OK) }

    Row(
        modifier = modifier
            .fillMaxSize()
            .customBackground()
            .captureBackKey { onClose() }
            .pointerInput(Unit) { detectTapGestures { } }
            .padding(horizontal = 130.dp, vertical = 70.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(modifier = Modifier.padding()) {
            Box(modifier = Modifier.width(124.dp)) {
                GuideScreenTvRemote(activeKeyProvider = { activeKey })
            }

            Column(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .width(268.dp),
            ) {

                Text("操作说明", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(16.dp))

                GuideScreenTvRemoteKeyInfo(activeKeyProvider = { activeKey })
            }
        }

        GuideScreenActions(
            activeKeyProvider = { activeKey },
            onChangeActiveKey = { activeKey = it },
            onClose = onClose,
        )
    }
}

@Composable
private fun GuideScreenTvRemote(
    modifier: Modifier = Modifier,
    activeKeyProvider: () -> GuideTvRemoteKeys = { GuideTvRemoteKeys.entries.first() },
) {
    CompositionLocalProvider(
        LocalGuideTvRemoteActiveKeys provides listOf(activeKeyProvider())
    ) { GuideTvRemote(modifier) }
}

@Composable
private fun GuideScreenTvRemoteKeyInfo(
    modifier: Modifier = Modifier,
    activeKeyProvider: () -> GuideTvRemoteKeys = { GuideTvRemoteKeys.entries.first() },
) {
    val activeKey = activeKeyProvider()

    Text(activeKey.label, style = MaterialTheme.typography.titleLarge)

    Column(modifier) {
        if (!activeKey.onTap.isNullOrBlank()) {
            Text(
                "短按：${activeKey.onTap}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(0.8f),
            )
        }
        if (!activeKey.onLongPress.isNullOrBlank()) {
            Text(
                "长按：${activeKey.onLongPress}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(0.8f),
            )
        }
        if (!activeKey.onDoubleTap.isNullOrBlank()) {
            Text(
                "双击：${activeKey.onDoubleTap}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(0.8f),
            )
        }
    }
}

@Composable
private fun GuideScreenActions(
    modifier: Modifier = Modifier,
    activeKeyProvider: () -> GuideTvRemoteKeys = { GuideTvRemoteKeys.entries.first() },
    onChangeActiveKey: (GuideTvRemoteKeys) -> Unit = {},
    onClose: () -> Unit = {},
) {
    val isLastKey by remember {
        derivedStateOf { activeKeyProvider() == GuideTvRemoteKeys.entries.last() }
    }

    val isFirstKey by remember {
        derivedStateOf { activeKeyProvider() == GuideTvRemoteKeys.entries.first() }
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (isLastKey) {
            WideButton(
                modifier = Modifier
                    .focusOnLaunched()
                    .handleKeyEvents(onSelect = { onClose() }),
                onClick = { },
                title = { Text("完成") }
            )
        } else {
            WideButton(
                modifier = Modifier
                    .focusOnLaunched(isFirstKey)
                    .handleKeyEvents(onSelect = {
                        val activeKey = activeKeyProvider()
                        onChangeActiveKey(
                            GuideTvRemoteKeys.entries[GuideTvRemoteKeys.entries.indexOf(activeKey) + 1]
                        )
                    }),
                onClick = { },
                title = { Text("下一步") },
            )
        }

        if (!isFirstKey) {
            WideButton(
                modifier = Modifier
                    .handleKeyEvents(onSelect = {
                        val activeKey = activeKeyProvider()
                        onChangeActiveKey(
                            GuideTvRemoteKeys.entries[GuideTvRemoteKeys.entries.indexOf(activeKey) - 1]
                        )
                    }),
                onClick = { },
                title = { Text("上一步") },
            )
        }

        if (!isLastKey) {
            WideButton(
                modifier = Modifier.handleKeyEvents(onSelect = { onClose() }),
                onClick = { },
                title = { Text("跳过") },
            )
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun GuideScreenPreview() {
    MyTVTheme {
        PreviewWithLayoutGrids {
            GuideScreen()
        }
    }
}