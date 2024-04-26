package top.yogiczy.mytv.ui.screens.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import top.yogiczy.mytv.data.entities.EpgProgramme
import top.yogiczy.mytv.data.entities.EpgProgramme.Companion.progress
import top.yogiczy.mytv.data.entities.EpgProgrammeCurrent
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.screens.panel.components.PanelChannelNo
import top.yogiczy.mytv.ui.screens.panel.components.PanelIptvInfo
import top.yogiczy.mytv.ui.theme.MyTVTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PanelTempScreen(
    modifier: Modifier = Modifier,
    channelNo: Int = 0,
    currentIptv: Iptv = Iptv.EMPTY,
    currentIptvUrlIdx: Int = 0,
    playerError: Boolean = false,
    currentProgrammes: EpgProgrammeCurrent? = null,
    showProgrammeProgress: Boolean = false,
) {
    val childPadding = rememberChildPadding()

    Box(modifier = modifier.fillMaxSize()) {
        PanelChannelNo(
            channelNo = channelNo.toString().padStart(2, '0'),
            modifier = Modifier
                .padding(top = childPadding.top, end = childPadding.end)
                .align(Alignment.TopEnd),
        )

        Layout(
            content = {
                PanelIptvInfo(
                    modifier = Modifier
                        .layoutId("info")
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .sizeIn(maxWidth = 500.dp),
                    iptv = currentIptv,
                    iptvUrlIdx = currentIptvUrlIdx,
                    currentProgrammes = currentProgrammes,
                    playerError = playerError,
                )

                if (showProgrammeProgress && currentProgrammes?.now != null) {
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
                .padding(start = childPadding.start, bottom = childPadding.bottom)
                .align(Alignment.BottomStart)
                .background(
                    MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                    MaterialTheme.shapes.medium,
                )
                .clip(MaterialTheme.shapes.medium),
        ) { measurables, constraints ->
            val infoPlaceable = measurables.find { it.layoutId == "info" }?.measure(constraints)
            val progressPlaceable =
                measurables.find { it.layoutId == "progress" }
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
private fun IptvTempPanelPreview() {
    MyTVTheme {
        PanelTempScreen(
            channelNo = 1,
            currentIptv = Iptv.EXAMPLE,
            playerError = true,
            currentProgrammes = EpgProgrammeCurrent(
                now = EpgProgramme(
                    startAt = System.currentTimeMillis() - 100000,
                    endAt = System.currentTimeMillis() + 200000,
                    title = "实况录像-2023/"
                ),
                next = null,
            ),
            showProgrammeProgress = true,
        )
    }
}