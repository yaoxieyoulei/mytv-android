package top.yogiczy.mytv.tv.ui.screen.settings.categories

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import top.yogiczy.mytv.tv.R
import top.yogiczy.mytv.tv.ui.screen.components.AppScreen
import top.yogiczy.mytv.tv.ui.screen.components.AppThemeDef
import top.yogiczy.mytv.tv.ui.screen.components.AppThemeWrapper
import top.yogiczy.mytv.tv.ui.screensold.settings.SettingsViewModel
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme
import top.yogiczy.mytv.tv.ui.utils.gridColumns
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents

@Composable
fun SettingsThemeScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(),
    onBackPressed: () -> Unit = {},
) {
    val context = LocalContext.current
    val resources = context.resources
    val nameList = resources.getStringArray(R.array.app_theme_name)
    val base64List = resources.getStringArray(R.array.app_theme_base64)
    val colorList = resources.getStringArray(R.array.app_theme_color)
    val imageList = resources.getStringArray(R.array.app_theme_image)

    val allAppThemeDef = nameList.indices.map { index ->
        AppThemeDef(
            name = nameList[index],
            base64 = base64List[index],
            color = colorList[index].removePrefix("0x").toLong(16),
            image = imageList[index]
        )
    }

    AppScreen(
        modifier = modifier.padding(top = 10.dp),
        header = { Text("设置 / 主题") },
        headerExtra = {
            Button(
                modifier = modifier
                    .handleKeyEvents(onSelect = { settingsViewModel.themeAppCurrent = null }),
                colors = ButtonDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                ),
                onClick = {},
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Default.DoNotDisturb,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Text("恢复默认")
                }
            }
        },
        canBack = true,
        onBackPressed = onBackPressed,
    ) {
        LazyColumn(
            contentPadding = PaddingValues(38.dp, 10.dp, 38.dp, 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                AppThemeDefList(
                    title = "基础色彩",
                    description = "感受色彩活力，定制你的主题",
                    appThemeDefList = allAppThemeDef.subList(0, 5),
                    onSelected = { settingsViewModel.themeAppCurrent = it },
                )
            }

            item {
                AppThemeDefList(
                    title = "故宫美学",
                    description = "处处座之旁，率陈如意常",
                    appThemeDefList = allAppThemeDef.subList(5, 10),
                    onSelected = { settingsViewModel.themeAppCurrent = it },
                )
            }

            item {
                AppThemeDefList(
                    title = "敦煌美学",
                    description = "沉睡千年，一醒惊天下",
                    appThemeDefList = allAppThemeDef.subList(10, 15),
                    onSelected = { settingsViewModel.themeAppCurrent = it },
                )
            }

            item {
                AppThemeDefList(
                    title = "水墨国风",
                    description = "秋水共长天一色",
                    appThemeDefList = allAppThemeDef.subList(15, 19),
                    onSelected = { settingsViewModel.themeAppCurrent = it },
                )
            }

            item {
                AppThemeDefList(
                    title = "神秘美学",
                    description = "每天一点甜，生活好运连连",
                    appThemeDefList = allAppThemeDef.subList(19, 23),
                    onSelected = { settingsViewModel.themeAppCurrent = it },
                )
            }

            item {
                AppThemeDefList(
                    title = "缤纷时刻",
                    description = "小小画板上映射生活的美好",
                    appThemeDefList = allAppThemeDef.subList(23, 27),
                    onSelected = { settingsViewModel.themeAppCurrent = it },
                )
            }

            item {
                AppThemeDefList(
                    title = "幻彩机核",
                    description = "永不止境的黑夜和炽热不羁的心跳",
                    appThemeDefList = allAppThemeDef.subList(27, 31),
                    onSelected = { settingsViewModel.themeAppCurrent = it },
                )
            }
        }
    }
}

@Composable
private fun AppThemeDefItem(
    modifier: Modifier = Modifier,
    appThemeDef: AppThemeDef,
    onSelected: () -> Unit = {},
) {
    Surface(
        modifier = modifier
            .size(2.4f.gridColumns(), 100.dp)
            .handleKeyEvents(onSelect = onSelected),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(
                BorderStroke(2.dp, Color(appThemeDef.color)),
                4.dp,
            )
        ),
        onClick = {},
    ) {
        AppThemeWrapper(appThemeDef) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    appThemeDef.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp),
                )
            }
        }
    }
}

@Composable
private fun AppThemeDefList(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    appThemeDefList: List<AppThemeDef>,
    onSelected: (AppThemeDef) -> Unit = {},
) {
    Surface(
        modifier = modifier,
        colors = SurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.onSurface.copy(0.1f)
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(start = 20.dp, top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.alpha(0.8f),
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(20.dp),
            ) {
                items(appThemeDefList) { def ->
                    AppThemeDefItem(
                        appThemeDef = def,
                        onSelected = { onSelected(def) },
                    )
                }
            }
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsThemePreview() {
    MyTvTheme {
        SettingsThemeScreen()
    }
}