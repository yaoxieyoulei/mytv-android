package top.yogiczy.mytv.ui.screens.settings.components

import android.content.pm.PackageInfo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.screens.settings.SettingsState
import top.yogiczy.mytv.ui.screens.settings.rememberSettingsState
import top.yogiczy.mytv.ui.theme.MyTVTheme

@Composable
fun SettingsMain(
    modifier: Modifier = Modifier,
    settingsState: SettingsState = rememberSettingsState(),
    updateState: UpdateState = rememberUpdateState(),
) {
    val childPadding = rememberChildPadding()

    Column(modifier = modifier) {
        Text(
            text = "设置（修改后需要重启生效）",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = childPadding.start),
        )
        Spacer(modifier = Modifier.height(6.dp))
        SettingsList(
            settingsState = settingsState,
            updateState = updateState,
        )
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsMainPreview() {
    MyTVTheme {
        SettingsMain(
            settingsState = SettingsState(),
            updateState = UpdateState(
                context = LocalContext.current,
                packageInfo = PackageInfo(),
                coroutineScope = rememberCoroutineScope(),
            ),
        )
    }
}