package top.yogiczy.mytv.ui.screens.panel.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.data.entities.EpgProgrammeCurrent
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.ui.theme.MyTVTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PanelIptvItem(
    modifier: Modifier = Modifier,
    iptv: Iptv = Iptv.EMPTY,
    onIptvSelected: () -> Unit = {},
    currentProgrammes: EpgProgrammeCurrent? = null,
    initialFocused: Boolean = false,
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (initialFocused) {
            focusRequester.requestFocus()
        }
    }

    Card(
        modifier = modifier
            .width(130.dp)
            .height(54.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        isFocused = true
                        onIptvSelected()
                    },
                )
            },
        scale = CardDefaults.scale(focusedScale = 1.1f),
        colors = CardDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            contentColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = MaterialTheme.colorScheme.onSurface,
            focusedContentColor = MaterialTheme.colorScheme.surface,
        ),
        onClick = { onIptvSelected() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Start)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = iptv.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
            )

            Text(
                text = currentProgrammes?.now?.title ?: "",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                modifier = Modifier.alpha(0.8f),
            )
        }
    }
}

@Preview
@Composable
private fun PanelIptvItemPreview() {
    MyTVTheme {
        PanelIptvItem(
            iptv = Iptv.EXAMPLE,
            currentProgrammes = EpgProgrammeCurrent.EXAMPLE,
        )
    }
}