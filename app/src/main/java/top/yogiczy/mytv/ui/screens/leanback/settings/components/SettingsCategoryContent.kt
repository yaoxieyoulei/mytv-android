package top.yogiczy.mytv.ui.screens.leanback.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yogiczy.mytv.ui.screens.leanback.settings.LeanbackSettingsCategories
import top.yogiczy.mytv.utils.Logger

@Composable
fun LeanbackSettingsCategoryContent(
    modifier: Modifier = Modifier,
    focusedCategoryProvider: () -> LeanbackSettingsCategories = { LeanbackSettingsCategories.entries.first() },
) {
    val focusedCategory = focusedCategoryProvider()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = focusedCategory.title, style = MaterialTheme.typography.headlineSmall)

        when (focusedCategory) {
            LeanbackSettingsCategories.ABOUT -> LeanbackSettingsCategoryAbout()
            LeanbackSettingsCategories.APP -> LeanbackSettingsCategoryApp()
            LeanbackSettingsCategories.IPTV -> LeanbackSettingsCategoryIptv()
            LeanbackSettingsCategories.EPG -> LeanbackSettingsCategoryEpg()
            LeanbackSettingsCategories.UI -> LeanbackSettingsCategoryUI()
            LeanbackSettingsCategories.FAVORITE -> LeanbackSettingsCategoryFavorite()
            LeanbackSettingsCategories.UPDATE -> LeanbackSettingsCategoryUpdate()
            LeanbackSettingsCategories.VIDEO_PLAYER -> LeanbackSettingsCategoryVideoPlayer()
            LeanbackSettingsCategories.HTTP -> LeanbackSettingsCategoryHttp()
            LeanbackSettingsCategories.DEBUG -> LeanbackSettingsCategoryDebug()
            LeanbackSettingsCategories.LOG -> LeanbackSettingsCategoryLog(
                history = Logger.history,
            )
            LeanbackSettingsCategories.MORE -> LeanbackSettingsCategoryMore()
        }
    }
}