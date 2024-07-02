package top.yogiczy.mytv.ui.screens.leanback.settings.components

import android.content.Context
import android.content.pm.PackageInfo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.material3.Icon
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.ui.screens.leanback.components.LeanbackQrcodeDialog
import top.yogiczy.mytv.ui.theme.LeanbackTheme

@Composable
fun LeanbackSettingsCategoryAbout(
    modifier: Modifier = Modifier,
    packageInfo: PackageInfo = rememberPackageInfo(),
) {
    TvLazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 10.dp),
    ) {
        item {
            LeanbackSettingsCategoryListItem(
                headlineContent = "应用名称",
                trailingContent = Constants.APP_TITLE,
            )
        }

        item {
            LeanbackSettingsCategoryListItem(
                headlineContent = "应用版本",
                trailingContent = packageInfo.versionName,
            )
        }

        item {
            var showQrDialog by remember { mutableStateOf(false) }

            LeanbackSettingsCategoryListItem(
                headlineContent = "代码仓库",
                trailingContent = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    ) {
                        androidx.tv.material3.Text(Constants.APP_REPO)

                        Icon(
                            Icons.AutoMirrored.Default.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                },
                onSelected = { showQrDialog = true },
            )

            LeanbackQrcodeDialog(
                text = Constants.APP_REPO,
                description = "扫码前往代码仓库",
                showDialogProvider = { showQrDialog },
                onDismissRequest = { showQrDialog = false },
            )
        }
    }
}

@Composable
private fun rememberPackageInfo(context: Context = LocalContext.current): PackageInfo =
    context.packageManager.getPackageInfo(context.packageName, 0)

@Preview
@Composable
private fun LeanbackSettingsAboutPreview() {
    LeanbackTheme {
        LeanbackSettingsCategoryAbout(
            packageInfo = PackageInfo().apply {
                versionName = "1.0.0"
            }
        )
    }
}
