package top.yogiczy.mytv.ui.screens.panel.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import kotlinx.coroutines.delay
import top.yogiczy.mytv.ui.theme.MyTVTheme
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PanelDateTime(
    modifier: Modifier = Modifier,
) {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val dateFormat = SimpleDateFormat("MM/dd EEE", Locale.getDefault())

    val timestamp = rememberTimestamp()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = dateFormat.format(timestamp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = timeFormat.format(timestamp),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
fun rememberTimestamp(): Long {
    var timestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            timestamp = System.currentTimeMillis()
        }
    }

    return timestamp
}

@Preview
@Composable
private fun PanelDateTimePreview() {
    MyTVTheme {
        PanelDateTime()
    }
}