package top.yogiczy.mytv.tv.ui.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Text
import top.yogiczy.mytv.core.data.entities.epg.EpgProgrammeReserve
import top.yogiczy.mytv.core.data.entities.epg.EpgProgrammeReserveList
import top.yogiczy.mytv.tv.ui.screens.settings.SettingsViewModel
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SettingsCategoryEpgReserve(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(),
) {
    val focusManager = LocalFocusManager.current
    val epgChannelReserveList = settingsViewModel.epgChannelReserveList.sortedBy { it.startAt }

    SettingsContentList(modifier) {
        item {
            SettingsListItem(
                modifier = Modifier.focusRequester(it),
                headlineContent = "当前已预约",
                trailingContent = "${settingsViewModel.epgChannelReserveList.size}个节目",
            )
        }

        item {
            SettingsListItem(
                headlineContent = "清空全部预约",
                supportingContent = "短按立即清空全部预约",
                onSelected = {
                    settingsViewModel.epgChannelReserveList = EpgProgrammeReserveList()
                }
            )
        }

        items(epgChannelReserveList) {
            SettingsCategoryEpgReserveItem(
                epgProgrammeReserve = it,
                onCancelReserve = {
                    if (it == epgChannelReserveList.last()) {
                        focusManager.moveFocus(FocusDirection.Up)
                    }

                    settingsViewModel.epgChannelReserveList =
                        EpgProgrammeReserveList(settingsViewModel.epgChannelReserveList - it)
                },
            )
        }
    }
}

@Composable
private fun SettingsCategoryEpgReserveItem(
    modifier: Modifier = Modifier,
    epgProgrammeReserve: EpgProgrammeReserve,
    onCancelReserve: () -> Unit = {},
) {
    val dateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val today = dateFormat.format(System.currentTimeMillis())
    val tomorrow = dateFormat.format(System.currentTimeMillis() + 24 * 3600 * 1000)

    SettingsListItem(
        modifier = modifier,
        headlineContent = epgProgrammeReserve.channel,
        supportingContent = epgProgrammeReserve.programme,
        trailingContent = {
            val start = timeFormat.format(epgProgrammeReserve.startAt)
            val end = timeFormat.format(epgProgrammeReserve.endAt)

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val date = dateFormat.format(epgProgrammeReserve.startAt)

                if (date != today) {
                    when (date) {
                        tomorrow -> Text("明天")
                        else -> Text(date)
                    }
                }

                Text("$start  ~ $end")
            }
        },
        onSelected = onCancelReserve,
    )
}

@Preview
@Composable
private fun SettingsCategoryEpgReserveItemPreview() {
    MyTVTheme {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SettingsCategoryEpgReserveItem(
                epgProgrammeReserve = EpgProgrammeReserve(
                    channel = "测试频道",
                    programme = "测试节目",
                    startAt = System.currentTimeMillis() + 100000,
                    endAt = System.currentTimeMillis() + 200000,
                )
            )

            SettingsCategoryEpgReserveItem(
                epgProgrammeReserve = EpgProgrammeReserve(
                    channel = "测试频道",
                    programme = "测试节目",
                    startAt = System.currentTimeMillis() + 100000 + (24 * 3600 * 1000),
                    endAt = System.currentTimeMillis() + 200000 + (24 * 3600 * 1000),
                )
            )

            SettingsCategoryEpgReserveItem(
                epgProgrammeReserve = EpgProgrammeReserve(
                    channel = "测试频道",
                    programme = "测试节目",
                    startAt = System.currentTimeMillis() + 100000 + (2 * 24 * 3600 * 1000),
                    endAt = System.currentTimeMillis() + 200000 + (2 * 24 * 3600 * 1000),
                )
            )
        }
    }
}