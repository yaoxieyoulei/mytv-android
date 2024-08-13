package top.yogiczy.mytv.tv.ui.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList
import top.yogiczy.mytv.tv.ui.rememberChildPadding
import top.yogiczy.mytv.tv.ui.screens.settings.SettingsCategories

@Composable
fun SettingsCategoryContent(
    modifier: Modifier = Modifier,
    currentCategoryProvider: () -> SettingsCategories = { SettingsCategories.entries.first() },
    channelGroupListProvider: () -> ChannelGroupList = { ChannelGroupList() },
) {
    val childPadding = rememberChildPadding()
    val currentCategory = currentCategoryProvider()

    Column(
        modifier = modifier.padding(top = childPadding.top),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = currentCategory.title, style = MaterialTheme.typography.headlineSmall)

        when (currentCategory) {
            SettingsCategories.ABOUT -> SettingsCategoryAbout()
            SettingsCategories.APP -> SettingsCategoryApp()
            SettingsCategories.IPTV -> SettingsCategoryIptv(
                channelGroupListProvider = channelGroupListProvider,
            )

            SettingsCategories.EPG -> SettingsCategoryEpg()
            SettingsCategories.EPG_RESERVE -> SettingsCategoryEpgReserve()
            SettingsCategories.UI -> SettingsCategoryUI()
            SettingsCategories.FAVORITE -> SettingsCategoryFavorite()
            SettingsCategories.UPDATE -> SettingsCategoryUpdate()
            SettingsCategories.VIDEO_PLAYER -> SettingsCategoryVideoPlayer()
            SettingsCategories.HTTP -> SettingsCategoryHttp()
            SettingsCategories.DEBUG -> SettingsCategoryDebug()
            SettingsCategories.LOG -> SettingsCategoryLog()
            SettingsCategories.MORE -> SettingsCategoryPush()
        }
    }
}