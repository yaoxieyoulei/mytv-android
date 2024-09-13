package top.yogiczy.mytv.tv.ui.screen.about

import android.content.Context
import android.content.pm.PackageInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.Text
import io.sentry.Sentry
import top.yogiczy.mytv.core.data.utils.Constants
import top.yogiczy.mytv.core.util.utils.compareVersion
import top.yogiczy.mytv.tv.R
import top.yogiczy.mytv.tv.ui.material.SimplePopup
import top.yogiczy.mytv.tv.ui.rememberChildPadding
import top.yogiczy.mytv.tv.ui.screen.components.AppScreen
import top.yogiczy.mytv.tv.ui.screen.components.QrcodePopup
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    packageInfo: PackageInfo = rememberPackageInfo(),
    latestVersionProvider: () -> String = { "" },
    toUpdateScreen: () -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    val childPadding = rememberChildPadding()

    AppScreen(
        modifier = modifier,
        header = { Text("关于") },
        canBack = true,
        onBackPressed = onBackPressed,
    ) {
        LazyColumn(
            modifier = Modifier.padding(top = 10.dp),
            contentPadding = childPadding.copy(top = 10.dp).paddingValues,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                ListItem(
                    headlineContent = { Text("应用名称") },
                    trailingContent = { Text(Constants.APP_TITLE) },
                    selected = false,
                    onClick = {},
                )
            }

            item {
                ListItem(
                    headlineContent = { Text("应用版本") },
                    trailingContent = { Text(packageInfo.versionName) },
                    selected = false,
                    onClick = {},
                )
            }

            item {
                var visible by remember { mutableStateOf(false) }

                ListItem(
                    modifier = Modifier.handleKeyEvents(onSelect = { visible = true }),
                    headlineContent = { Text("代码仓库") },
                    trailingContent = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(Constants.APP_REPO)

                            Icon(
                                Icons.AutoMirrored.Default.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    },
                    selected = false,
                    onClick = {},
                )

                QrcodePopup(
                    visibleProvider = { visible },
                    onDismissRequest = { visible = false },
                    text = Constants.APP_REPO,
                    description = "扫码前往代码仓库",
                )
            }

            item {
                var visible by remember { mutableStateOf(false) }

                ListItem(
                    modifier = Modifier.handleKeyEvents(onSelect = { visible = true }),
                    headlineContent = { Text("技术交流 Telegram") },
                    trailingContent = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(Constants.GROUP_TELEGRAM)

                            Icon(
                                Icons.AutoMirrored.Default.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    },
                    selected = false,
                    onClick = {},
                )

                QrcodePopup(
                    visibleProvider = { visible },
                    onDismissRequest = { visible = false },
                    text = Constants.GROUP_TELEGRAM,
                )
            }

            item {
                ListItem(
                    headlineContent = { Text("技术交流 QQ") },
                    trailingContent = { Text(Constants.GROUP_QQ) },
                    selected = false,
                    onClick = {},
                )
            }

            item {
                var visible by remember { mutableStateOf(false) }

                ListItem(
                    modifier = Modifier.handleKeyEvents(onSelect = { visible = true }),
                    headlineContent = { Text("赞赏") },
                    supportingContent = { Text("仅支持微信赞赏码") },
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Default.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                    },
                    selected = false,
                    onClick = {},
                )

                SimplePopup(
                    visibleProvider = { visible },
                    onDismissRequest = { visible = false },
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

            @Suppress("UnstableApiUsage")
            Sentry.withScope {
                it.options.distinctId?.let { distinctId ->
                    item {
                        var visible by remember { mutableStateOf(false) }

                        ListItem(
                            modifier = Modifier.handleKeyEvents(onSelect = {
                                visible = true
                            }),
                            headlineContent = { Text("设备ID") },
                            trailingContent = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(distinctId)

                                    Icon(
                                        Icons.AutoMirrored.Default.OpenInNew,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            },
                            selected = false,
                            onClick = {},
                        )

                        QrcodePopup(
                            visibleProvider = { visible },
                            onDismissRequest = { visible = false },
                            text = distinctId,
                        )
                    }
                }
            }

            item {
                ListItem(
                    modifier = Modifier.handleKeyEvents(onSelect = toUpdateScreen),
                    headlineContent = { Text("检查更新") },
                    trailingContent = {
                        val currentVersion = packageInfo.versionName
                        val latestVersion = latestVersionProvider().ifBlank { currentVersion }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (latestVersion.compareVersion(currentVersion) > 0) {
                                Text("新版本: $latestVersion")
                            } else {
                                Text("无更新")
                            }

                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    },
                    selected = false,
                    onClick = {},
                )
            }
        }
    }
}

@Composable
private fun rememberPackageInfo(context: Context = LocalContext.current): PackageInfo =
    context.packageManager.getPackageInfo(context.packageName, 0)

@Preview(device = "id:Android TV (720p)")
@Composable
private fun AboutScreenPreview() {
    MyTvTheme {
        AboutScreen(
            packageInfo = PackageInfo().apply { versionName = "1.2.3" },
            latestVersionProvider = { "9.0.0" },
        )
    }
}