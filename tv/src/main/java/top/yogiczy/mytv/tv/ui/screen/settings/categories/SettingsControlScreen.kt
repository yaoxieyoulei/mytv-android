package top.yogiczy.mytv.tv.ui.screen.settings.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Switch
import androidx.tv.material3.Text
import top.yogiczy.mytv.tv.ui.rememberChildPadding
import top.yogiczy.mytv.tv.ui.screen.components.AppScreen
import top.yogiczy.mytv.tv.ui.screen.settings.components.SettingsListItem
import top.yogiczy.mytv.tv.ui.screensold.settings.SettingsViewModel
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme

@Composable
fun SettingsControlScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(),
    onBackPressed: () -> Unit = {},
) {
    val childPadding = rememberChildPadding()

    AppScreen(
        modifier = modifier.padding(top = 10.dp),
        header = { Text("设置 / 控制") },
        canBack = true,
        onBackPressed = onBackPressed,
    ) {
        LazyColumn(
            contentPadding = childPadding.copy(top = 10.dp).paddingValues,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                val enable = settingsViewModel.iptvChannelNoSelectEnable

                SettingsListItem(
                    headlineContent = "数字选台",
                    supportingContent = "通过数字键选择频道",
                    trailingContent = {
                        Switch(enable, null)
                    },
                    onSelected = {
                        settingsViewModel.iptvChannelNoSelectEnable = !enable
                    },
                )
            }

            item {
                val flip = settingsViewModel.iptvChannelChangeFlip

                SettingsListItem(
                    headlineContent = "换台反转",
                    supportingContent = if (flip) "方向键上：下一个频道；方向键下：上一个频道"
                    else "方向键上：上一个频道；方向键下：下一个频道",
                    trailingContent = {
                        Switch(flip, null)
                    },
                    onSelected = {
                        settingsViewModel.iptvChannelChangeFlip = !flip
                    },
                )
            }
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsControlScreenPreview() {
    MyTvTheme {
        SettingsControlScreen()
    }
}