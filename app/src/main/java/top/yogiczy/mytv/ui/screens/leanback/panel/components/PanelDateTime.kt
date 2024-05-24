package top.yogiczy.mytv.ui.screens.leanback.panel.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import top.yogiczy.mytv.ui.theme.LeanbackTheme
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun LeanbackPanelDateTime(
    modifier: Modifier = Modifier,
    timestamp: Long = rememberTimestamp(),
) {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val dateFormat = SimpleDateFormat("MM/dd EEE", Locale.getDefault())

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

@Preview
@Composable
private fun LeanbackPanelDateTimePreview() {
    LeanbackTheme {
        LeanbackPanelDateTime()
    }
}

@Composable
private fun rememberTimestamp(): Long {
    var timestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            timestamp = System.currentTimeMillis()
        }
    }

    return timestamp
}