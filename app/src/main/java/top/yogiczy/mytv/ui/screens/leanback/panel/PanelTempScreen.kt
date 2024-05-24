package top.yogiczy.mytv.ui.screens.leanback.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import top.yogiczy.mytv.data.entities.EpgProgramme
import top.yogiczy.mytv.data.entities.EpgProgramme.Companion.progress
import top.yogiczy.mytv.data.entities.EpgProgrammeCurrent
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.ui.rememberLeanbackChildPadding
import top.yogiczy.mytv.ui.screens.leanback.panel.components.LeanbackPanelChannelNo
import top.yogiczy.mytv.ui.screens.leanback.panel.components.LeanbackPanelIptvInfo
import top.yogiczy.mytv.ui.theme.LeanbackTheme

@Composable
fun LeanbackPanelTempScreen(
    modifier: Modifier = Modifier,
    channelNoProvider: () -> Int = { 0 },
    currentIptvProvider: () -> Iptv = { Iptv() },
    currentIptvUrlIdxProvider: () -> Int = { 0 },
    currentProgrammesProvider: () -> EpgProgrammeCurrent? = { null },
    showProgrammeProgressProvider: () -> Boolean = { false },
) {
    val childPadding = rememberLeanbackChildPadding()

    Box(modifier = modifier.fillMaxSize()) {
        LeanbackPanelChannelNo(
            channelNoProvider = { channelNoProvider().toString().padStart(2, '0') },
            modifier = Modifier
                .padding(top = childPadding.top, end = childPadding.end)
                .align(Alignment.TopEnd),
        )

        Layout(
            content = {
                LeanbackPanelIptvInfo(
                    modifier = Modifier
                        .layoutId("info")
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .sizeIn(maxWidth = 400.dp),
                    iptvProvider = currentIptvProvider,
                    iptvUrlIdxProvider = currentIptvUrlIdxProvider,
                    currentProgrammesProvider = currentProgrammesProvider,
                )

                val currentProgrammes = currentProgrammesProvider()
                if (showProgrammeProgressProvider() && currentProgrammes?.now != null) {
                    Box(
                        modifier = Modifier
                            .layoutId("progress")
                            .align(Alignment.BottomStart)
                            .fillMaxWidth(currentProgrammes.now.progress())
                            .height(3.dp)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)),
                    )
                }
            },
            modifier = Modifier
                .padding(
                    start = childPadding.start, bottom = childPadding.bottom
                )
                .align(Alignment.BottomStart)
                .background(
                    MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                    MaterialTheme.shapes.medium,
                )
                .clip(MaterialTheme.shapes.medium),
        ) { measurables, constraints ->
            val infoPlaceable = measurables.find { it.layoutId == "info" }?.measure(constraints)
            val progressPlaceable = measurables.find { it.layoutId == "progress" }
                ?.measure(Constraints(maxWidth = infoPlaceable?.width ?: 0))

            layout(infoPlaceable?.width ?: 0, infoPlaceable?.height ?: 0) {
                infoPlaceable?.placeRelative(0, 0)
                progressPlaceable?.placeRelative(
                    0,
                    (infoPlaceable?.height ?: 0) - progressPlaceable.height,
                )
            }
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun LeanbackPanelTempScreenPreview() {
    LeanbackTheme {
        LeanbackPanelTempScreen(
            channelNoProvider = { 1 },
            currentIptvProvider = { Iptv.EXAMPLE },
            currentProgrammesProvider = {
                EpgProgrammeCurrent(
                    now = EpgProgramme(
                        startAt = System.currentTimeMillis() - 100000,
                        endAt = System.currentTimeMillis() + 200000,
                        title = "实况录像-2023/"
                    ),
                    next = null,
                )
            },
            showProgrammeProgressProvider = { true },
        )
    }
}