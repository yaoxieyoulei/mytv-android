package top.yogiczy.mytv.tv.ui.screen.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.LocalTextStyle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.tv.ui.rememberChildPadding
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme
import top.yogiczy.mytv.tv.ui.utils.focusOnLaunched
import top.yogiczy.mytv.tv.ui.utils.handleKeyEventsOnFocused
import kotlin.math.roundToInt

@Composable
fun AppScreen(
    modifier: Modifier = Modifier,
    header: @Composable (() -> Unit)? = null,
    headerExtra: @Composable (() -> Unit)? = null,
    canBack: Boolean = false,
    enableTopBarHidden: Boolean = false,
    onBackPressed: () -> Unit = {},
    content: @Composable BoxScope.(updateTopBarVisibility: (Boolean) -> Unit) -> Unit,
) {
    val density = LocalDensity.current
    var isTopBarVisible by remember { mutableStateOf(true) }
    var topBarHeightPx by rememberSaveable {
        mutableIntStateOf(with(density) { 64.dp.toPx().roundToInt() })
    }
    val topBarYOffsetPx by animateIntAsState(
        targetValue = if (isTopBarVisible) 0 else -topBarHeightPx,
        animationSpec = tween(),
        label = "",
    )
    val contentPaddingPx by animateIntAsState(
        targetValue = if (isTopBarVisible) topBarHeightPx else 0,
        animationSpec = tween(),
        label = "",
    )

    BackHandler { onBackPressed() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF141E30),
                        Color(0xFF243B55),
                        Color(0xFF141E30)
                    ),
                )
            )
    ) {
        if (enableTopBarHidden) {
            AppScaffoldTopBar(
                modifier = Modifier
                    .height(64.dp)
                    .offset { IntOffset(x = 0, y = topBarYOffsetPx) }
                    .onSizeChanged { topBarHeightPx = it.height },
                header = header,
                headerExtra = headerExtra,
                canBack = canBack,
                onBackPressed = onBackPressed,
            )

            Box(
                modifier = modifier.offset { IntOffset(x = 0, y = contentPaddingPx) },
            ) {
                content { isTopBarVisible = it }
            }
        } else {
            Column {
                AppScaffoldTopBar(
                    modifier = Modifier.height(64.dp),
                    header = header,
                    headerExtra = headerExtra,
                    canBack = canBack,
                    onBackPressed = onBackPressed,
                )

                Box(modifier = modifier) {
                    content { }
                }
            }
        }
    }
}

@Composable
private fun AppScaffoldTopBar(
    modifier: Modifier = Modifier,
    header: @Composable (() -> Unit)? = null,
    headerExtra: @Composable (() -> Unit)? = null,
    canBack: Boolean = false,
    onBackPressed: () -> Unit = {},
) {
    val childPadding = rememberChildPadding()

    if (header != null || headerExtra != null) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(childPadding.copy(bottom = 0.dp).paddingValues),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (canBack) AppScaffoldBack(onBack = onBackPressed)
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.titleLarge
                ) { header?.let { nnHeader -> nnHeader() } }
            }
            Box { headerExtra?.let { nnHeaderExtra -> nnHeaderExtra() } }
        }
    }
}

@Composable
private fun AppScaffoldBack(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
) {
    Button(
        modifier = modifier
            .focusOnLaunched()
            .handleKeyEventsOnFocused(onSelect = onBack),
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
                Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Text("返回")
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun AppScaffoldPreview() {
    MyTvTheme {
        AppScreen(
            header = { Text("Header头部") },
            headerExtra = { Text("Header Extra") },
            canBack = true,
        ) {
            Text("Content".repeat(10))
        }
//        PreviewWithLayoutGrids { }
    }
}