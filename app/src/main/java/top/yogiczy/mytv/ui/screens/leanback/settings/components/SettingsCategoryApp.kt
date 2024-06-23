package top.yogiczy.mytv.ui.screens.leanback.settings.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.foundation.lazy.list.TvLazyColumn
import top.yogiczy.mytv.ui.screens.leanback.settings.LeanbackSettingsViewModel
import top.yogiczy.mytv.ui.screens.leanback.update.LeanBackUpdateViewModel
import top.yogiczy.mytv.ui.theme.LeanbackTheme
import top.yogiczy.mytv.ui.utils.SP

@Composable
fun LeanbackSettingsCategoryApp(
    modifier: Modifier = Modifier,
    settingsViewModel: LeanbackSettingsViewModel = viewModel(),
    updateViewModel: LeanBackUpdateViewModel = viewModel(),
) {

    TvLazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 10.dp),
    ) {
        item {
            LeanbackSettingsCategoryListItem(
                headlineContent = "开机自启",
                supportingContent = "请确保当前设备支持该功能",
                trailingContent = {
                    Switch(checked = settingsViewModel.appBootLaunch, onCheckedChange = null)
                },
                onSelected = {
                    settingsViewModel.appBootLaunch = !settingsViewModel.appBootLaunch
                },
            )
        }

        item {
            val context = LocalContext.current

            LeanbackSettingsCategoryListItem(
                headlineContent = "显示模式",
                supportingContent = "短按切换应用显示模式",
                trailingContent = when (settingsViewModel.appDeviceDisplayType) {
                    SP.AppDeviceDisplayType.LEANBACK -> "TV"
                    SP.AppDeviceDisplayType.PAD -> "Pad"
                    SP.AppDeviceDisplayType.MOBILE -> "手机"
                },
                onSelected = {
                    Toast.makeText(context, "暂未开放", Toast.LENGTH_SHORT).show()
//                    settingsViewModel.appDeviceDisplayType = SP.AppDeviceDisplayType.entries[
//                        (settingsViewModel.appDeviceDisplayType.ordinal + 1) % SP.AppDeviceDisplayType.entries.size
//                    ]
                },
            )
        }

        item {
            LeanbackSettingsCategoryListItem(
                headlineContent = "应用更新",
                supportingContent = "最新版本：v${updateViewModel.latestRelease.version}",
                trailingContent = if (updateViewModel.isUpdateAvailable) "发现新版本" else "无更新",
                onSelected = {
                    if (updateViewModel.isUpdateAvailable)
                        updateViewModel.showDialog = true
                },
            )
        }
    }
}

@Preview
@Composable
private fun LeanbackSettingsCategoryAppPreview() {
    SP.init(LocalContext.current)
    LeanbackTheme {
        LeanbackSettingsCategoryApp(
            modifier = Modifier.padding(20.dp),
            settingsViewModel = LeanbackSettingsViewModel(),
        )
    }
}
