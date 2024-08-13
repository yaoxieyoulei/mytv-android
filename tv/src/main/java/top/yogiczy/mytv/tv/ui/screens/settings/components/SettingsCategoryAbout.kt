package top.yogiczy.mytv.tv.ui.screens.settings.components

import android.content.Context
import android.content.pm.PackageInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.sentry.Sentry
import top.yogiczy.mytv.core.data.utils.Constants
import top.yogiczy.mytv.tv.R
import top.yogiczy.mytv.tv.ui.material.LocalPopupManager
import top.yogiczy.mytv.tv.ui.material.SimplePopup
import top.yogiczy.mytv.tv.ui.screens.components.QrcodeDialog
import top.yogiczy.mytv.tv.ui.screens.guide.GuideScreen

@Composable
fun SettingsCategoryAbout(
    modifier: Modifier = Modifier,
    packageInfo: PackageInfo = rememberPackageInfo(),
) {
    SettingsContentList(modifier) {
        item {
            SettingsListItem(
                modifier = Modifier.focusRequester(it),
                headlineContent = "应用名称",
                trailingContent = Constants.APP_TITLE,
            )
        }

        item {
            SettingsListItem(
                headlineContent = "应用版本",
                trailingContent = packageInfo.versionName,
            )
        }

        item {
            val popupManager = LocalPopupManager.current
            val focusRequester = remember { FocusRequester() }
            var showDialog by remember { mutableStateOf(false) }

            SettingsListItem(
                modifier = Modifier.focusRequester(focusRequester),
                headlineContent = "代码仓库",
                trailingContent = Constants.APP_REPO,
                trailingIcon = Icons.AutoMirrored.Default.OpenInNew,
                onSelected = {
                    popupManager.push(focusRequester, true)
                    showDialog = true
                },
            )

            QrcodeDialog(
                textProvider = { Constants.APP_REPO },
                descriptionProvider = { "扫码前往代码仓库" },
                showDialogProvider = { showDialog },
                onDismissRequest = { showDialog = false },
            )
        }

        item {
            val popupManager = LocalPopupManager.current
            val focusRequester = remember { FocusRequester() }
            var isGuideScreenVisible by remember { mutableStateOf(false) }

            SettingsListItem(
                modifier = Modifier.focusRequester(focusRequester),
                headlineContent = "使用说明",
                trailingIcon = Icons.AutoMirrored.Filled.OpenInNew,
                onSelected = {
                    popupManager.push(focusRequester, true)
                    isGuideScreenVisible = true
                },
            )

            SimplePopup(
                visibleProvider = { isGuideScreenVisible },
                onDismissRequest = { isGuideScreenVisible = false },
            ) {
                GuideScreen(onClose = { isGuideScreenVisible = false })
            }
        }

        item {
            val popupManager = LocalPopupManager.current
            val focusRequester = remember { FocusRequester() }
            var visible by remember { mutableStateOf(false) }

            SettingsListItem(
                modifier = Modifier.focusRequester(focusRequester),
                headlineContent = "赞赏",
                trailingIcon = Icons.AutoMirrored.Filled.OpenInNew,
                onSelected = {
                    popupManager.push(focusRequester, true)
                    visible = true
                },
            )

            SimplePopup(
                visibleProvider = { visible },
                onDismissRequest = { visible = false },
                withBackground = true,
            ) {
                val painter = painterResource(R.drawable.mm_reward_qrcode)

                Image(
                    painter,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(300.dp),
                )
            }
        }

        Sentry.withScope {
            item {
                @Suppress("UnstableApiUsage")
                SettingsListItem(
                    headlineContent = "设备ID",
                    trailingContent = it.options.distinctId.toString(),
                )
            }
        }
    }
}

@Composable
private fun rememberPackageInfo(context: Context = LocalContext.current): PackageInfo =
    context.packageManager.getPackageInfo(context.packageName, 0)
