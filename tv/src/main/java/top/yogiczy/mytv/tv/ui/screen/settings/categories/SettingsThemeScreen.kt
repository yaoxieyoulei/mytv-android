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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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

    val allAppThemeDefGroup = remember { mutableStateListOf<AppThemeDefGroup>() }

    LaunchedEffect(Unit) {
        allAppThemeDefGroup.clear()
        withContext(Dispatchers.IO) {
            allAppThemeDefGroup.addAll(
                resources.openRawResource(R.raw.app_themes).bufferedReader().use {
                    Json.decodeFromString<List<AppThemeDefGroup>>(it.readText())
                })
        }
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
            items(allAppThemeDefGroup) { group ->
                AppThemeDefList(
                    title = group.name,
                    description = group.description,
                    appThemeDefList = group.list,
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
            focusedBorder = Border(BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface))
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

@Serializable
private data class AppThemeDefGroup(
    val name: String,
    val description: String,
    val list: List<AppThemeDef>,
)

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsThemePreview() {
    MyTvTheme {
        SettingsThemeScreen()
    }
}