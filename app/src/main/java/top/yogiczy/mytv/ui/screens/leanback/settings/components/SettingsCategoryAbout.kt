package top.yogiczy.mytv.ui.screens.leanback.settings.components

import android.content.Context
import android.content.pm.PackageInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.tv.foundation.lazy.list.TvLazyColumn
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.QrShapes
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import top.yogiczy.mytv.data.utils.Constants
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
                trailingContent = Constants.APP_REPO,
                onSelected = { showQrDialog = true },
            )

            if (showQrDialog) {
                AlertDialog(
                    properties = DialogProperties(usePlatformDefaultWidth = false),
                    onDismissRequest = { showQrDialog = false },
                    confirmButton = { Text(text = "扫码前往代码仓库") },
                    text = {
                        Box(
                            modifier = Modifier
                                .width(240.dp)
                                .height(240.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    shape = MaterialTheme.shapes.medium,
                                )
                        ) {
                            Image(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .align(Alignment.Center)
                                    .padding(10.dp),
                                painter = rememberQrCodePainter(
                                    data = Constants.APP_REPO,
                                    shapes = QrShapes(
                                        ball = QrBallShape.circle(),
                                        darkPixel = QrPixelShape.roundCorners(),
                                        frame = QrFrameShape.roundCorners(.25f),
                                    ),
                                ),
                                contentDescription = Constants.APP_REPO,
                            )
                        }
                    },
                )
            }
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
