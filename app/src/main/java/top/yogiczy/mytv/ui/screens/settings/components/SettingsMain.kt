package top.yogiczy.mytv.ui.screens.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.SP

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsMain(
    modifier: Modifier = Modifier,
) {
    val childPadding = rememberChildPadding()

    Column(modifier = modifier) {
        Text(
            text = "设置（修改后都需要重启生效）",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = childPadding.start),
        )
        Spacer(modifier = Modifier.height(6.dp))
        SettingsList()
    }
}

@Preview
@Composable
private fun SettingsMainPreview() {
    SP.init(LocalContext.current)
    MyTVTheme {
        SettingsMain()
    }
}