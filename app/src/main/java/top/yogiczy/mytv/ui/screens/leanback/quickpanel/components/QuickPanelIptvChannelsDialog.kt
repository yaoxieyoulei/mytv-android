package top.yogiczy.mytv.ui.screens.leanback.quickpanel.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.itemsIndexed
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.ui.theme.LeanbackTheme
import top.yogiczy.mytv.ui.utils.handleLeanbackKeyEvents
import top.yogiczy.mytv.utils.isIPv6
import kotlin.math.max
import kotlin.system.measureTimeMillis

@Composable
fun LeanbackQuickPanelIptvChannelsDialog(
    modifier: Modifier = Modifier,
    showDialogProvider: () -> Boolean = { false },
    onDismissRequest: () -> Unit = {},
    iptvProvider: () -> Iptv = { Iptv() },
    iptvUrlIdxProvider: () -> Int = { 0 },
    onIptvUrlIdxChange: (Int) -> Unit = {},
    onUserAction: () -> Unit = {},
) {
    val iptv = iptvProvider()
    val iptvUrlIdx = iptvUrlIdxProvider()

    if (showDialogProvider()) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismissRequest,
            confirmButton = { Text(text = "短按切换线路") },
            title = { Text(text = iptv.name) },
            text = {
                var hasFocused by remember { mutableStateOf(false) }

                val listState = rememberTvLazyListState(max(0, iptvUrlIdx - 2))

                LaunchedEffect(listState) {
                    snapshotFlow { listState.isScrollInProgress }
                        .distinctUntilChanged()
                        .collect { _ -> onUserAction() }
                }

                TvLazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(iptv.urlList) { index, url ->
                        val focusRequester = remember { FocusRequester() }

                        LaunchedEffect(Unit) {
                            if (index == iptvUrlIdx && !hasFocused) {
                                hasFocused = true
                                focusRequester.requestFocus()
                            }
                        }

                        LeanbackQuickPanelIptvChannelItem(
                            url = url,
                            urlIndex = index,
                            isSelected = index == iptvUrlIdx,
                            focusRequester = focusRequester,
                            onSelect = {
                                onDismissRequest()
                                onIptvUrlIdxChange(index)
                            },
                        )
                    }
                }
            },
        )
    }
}

@Composable
private fun LeanbackQuickPanelIptvChannelItem(
    modifier: Modifier = Modifier,
    url: String,
    urlIndex: Int,
    isSelected: Boolean = false,
    focusRequester: FocusRequester = remember { FocusRequester() },
    onSelect: () -> Unit = {},
    urlDelay: Long = rememberIptvUrlDelay(url),
) {
    var isFocused by remember { mutableStateOf(false) }

    androidx.tv.material3.ListItem(
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
            .handleLeanbackKeyEvents(
                onSelect = {
                    if (isFocused) onSelect()
                    else focusRequester.requestFocus()
                },
            ),
        selected = isSelected,
        onClick = { },
        headlineContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                androidx.tv.material3.Text(text = "线路${urlIndex + 1}")

                Row(
                    modifier = Modifier.padding(bottom = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    CompositionLocalProvider(
                        androidx.tv.material3.LocalTextStyle provides MaterialTheme.typography.labelSmall,
                        androidx.tv.material3.LocalContentColor provides androidx.tv.material3.LocalContentColor.current.copy(
                            alpha = 0.8f
                        ),
                    ) {
                        val textModifier = Modifier
                            .background(
                                androidx.tv.material3.LocalContentColor.current.copy(alpha = 0.3f),
                                MaterialTheme.shapes.extraSmall,
                            )
                            .padding(vertical = 2.dp, horizontal = 4.dp)

                        androidx.tv.material3.Text(
                            text = if (url.isIPv6()) "IPV6" else "IPV4",
                            modifier = textModifier,
                        )

                        if (urlDelay != 0L) {
                            androidx.tv.material3.Text(
                                text = "$urlDelay ms",
                                modifier = textModifier,
                            )
                        }
                    }
                }
            }
        },
        supportingContent = {
            androidx.tv.material3.Text(
                text = url,
                maxLines = 1,
            )
        },
        trailingContent = {
            if (isSelected) {
                androidx.tv.material3.Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "checked",
                )
            }
        },
    )
}

@Composable
private fun rememberIptvUrlDelay(url: String): Long {
    var elapsedTime by remember { mutableLongStateOf(0) }
    var hasError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()

            elapsedTime = measureTimeMillis {
                try {
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    }
                } catch (_: IOException) {
                    hasError = true
                }
            }
        }
    }

    return if (hasError) 0 else elapsedTime
}

@Preview
@Composable
private fun LeanbackQuickPanelIptvChannelItemPreview() {
    LeanbackTheme {
        LeanbackQuickPanelIptvChannelItem(
            url = "http://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221226231/index.m3u8",
            urlIndex = 0,
            urlDelay = 123,
            isSelected = true,
        )
    }
}

@Preview
@Composable
private fun LeanbackQuickPanelIptvChannelsDialogPreview() {
    LeanbackTheme {
        LeanbackQuickPanelIptvChannelsDialog(
            showDialogProvider = { true },
            iptvProvider = { Iptv.EXAMPLE },
        )
    }
}