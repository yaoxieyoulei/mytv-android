package top.yogiczy.mytv.ui.screens.leanback.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import kotlinx.coroutines.delay
import top.yogiczy.mytv.ui.theme.LeanbackTheme

@Composable
fun LeanbackToastScreen(
    modifier: Modifier = Modifier,
    state: LeanbackToastState = rememberLeanbackToastState(),
) {
    Popup(
        alignment = Alignment.BottomCenter,
        offset = IntOffset(0, with(LocalDensity.current) { -28.dp.toPx().toInt() }),
    ) {
        AnimatedVisibility(
            visible = state.visible,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = modifier,
        ) {
            LeanbackToastItem(property = state.current)
        }
    }
}

@Composable
fun LeanbackToastItem(
    modifier: Modifier = Modifier,
    property: LeanbackToastProperty = LeanbackToastProperty(),
) {
    Box(
        modifier = modifier
            .sizeIn(maxWidth = 556.dp)
            .background(MaterialTheme.colorScheme.inverseSurface, MaterialTheme.shapes.medium)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LeanbackToastContentIcon(
                showIcon = true,
                icon = Icons.Outlined.Info,
                iconColor = MaterialTheme.colorScheme.inverseOnSurface,
                iconContainerColors = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            androidx.tv.material3.Text(
                property.message,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )
        }
    }
}

@Composable
private fun LeanbackToastContentIcon(
    modifier: Modifier = Modifier,
    showIcon: Boolean,
    icon: ImageVector,
    iconColor: Color,
    iconContainerColors: Color,
) {
    if (showIcon) {
        Box(
            modifier = modifier
                .background(iconContainerColors, CircleShape)
                .padding(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = iconColor,
            )
        }
    }
}

@Preview(device = "id:Android TV (720p)", showBackground = true)
@Composable
private fun LeanbackToastScreenAnimationPreview() {
    LeanbackTheme {
        val state = rememberLeanbackToastState()

        LaunchedEffect(Unit) {
            while (true) {
                state.showToast("新版本: v1.2.2")
                delay(1000)
                state.showToast("新版本: v9.9.9")
                delay(5000)
            }
        }

        LeanbackToastScreen(state = state)
    }
}

@Preview(showBackground = true)
@Composable
private fun LeanbackToastScreenPreview() {
    LeanbackTheme {
        LeanbackToastItem(
            modifier = Modifier.padding(16.dp),
            property = LeanbackToastProperty(message = "新版本: v1.2.2"),
        )
    }
}
