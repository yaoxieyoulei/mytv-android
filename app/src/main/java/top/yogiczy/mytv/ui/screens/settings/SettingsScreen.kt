package top.yogiczy.mytv.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.screens.settings.components.SettingsAppInfo
import top.yogiczy.mytv.ui.screens.settings.components.SettingsMain
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.SP

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
) {
    val childPadding = rememberChildPadding()

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .focusRequester(focusRequester)
            .pointerInput(Unit) { detectTapGestures(onTap = { onClose() }) },
    ) {
        Column(modifier = Modifier.align(Alignment.BottomStart)) {
            SettingsAppInfo(modifier = Modifier.padding(start = childPadding.start))

            Spacer(modifier = Modifier.height(20.dp))

            SettingsMain()
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsScreenPreview() {
    SP.init(LocalContext.current)
    MyTVTheme {
        SettingsScreen()
    }
}