package top.yogiczy.mytv.tv.ui.screens.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Switch
import top.yogiczy.mytv.tv.ui.screens.settings.SettingsViewModel

@Composable
fun SettingsCategoryFavorite(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(),
) {
    SettingsContentList(modifier) {
        item {
            SettingsListItem(
                modifier = Modifier.focusRequester(it),
                headlineContent = "收藏启用",
                trailingContent = {
                    Switch(settingsViewModel.iptvChannelFavoriteEnable, null)
                },
                onSelected = {
                    settingsViewModel.iptvChannelFavoriteEnable =
                        !settingsViewModel.iptvChannelFavoriteEnable
                    if (!settingsViewModel.iptvChannelFavoriteEnable) {
                        settingsViewModel.iptvChannelFavoriteListVisible = false
                    }
                },
            )
        }

        item {
            SettingsListItem(
                headlineContent = "当前已收藏",
                supportingContent = "包括不存在直播源中的频道",
                trailingContent = "${settingsViewModel.iptvChannelFavoriteList.size}个频道",
            )
        }

        item {
            SettingsListItem(
                headlineContent = "清空全部收藏",
                supportingContent = "短按立即清空全部收藏",
                onSelected = {
                    settingsViewModel.iptvChannelFavoriteList = emptySet()
                    settingsViewModel.iptvChannelFavoriteListVisible = false
                }
            )
        }

        item {
            SettingsListItem(
                headlineContent = "收藏换台边界跳出",
                supportingContent = if (settingsViewModel.iptvChannelFavoriteChangeBoundaryJumpOut)
                    "当在收藏列表边界时，再次换台将跳出收藏列表"
                else
                    "在收藏列表可见情况下，将在收藏列表中循环换台",
                trailingContent = {
                    Switch(settingsViewModel.iptvChannelFavoriteChangeBoundaryJumpOut, null)
                },
                onSelected = {
                    settingsViewModel.iptvChannelFavoriteChangeBoundaryJumpOut =
                        !settingsViewModel.iptvChannelFavoriteChangeBoundaryJumpOut
                },
            )
        }
    }
}