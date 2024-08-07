package top.yogiczy.mytv.tv.ui.screens.guide.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Input
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.Text
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme

@Composable
fun GuideTvRemote(modifier: Modifier = Modifier) {
    GuideTvRemoteWrapper(modifier) {
        Column {
            GuideTvRemoteHeader()
            Spacer(Modifier.height(14.dp))

            GuideTvRemoteCenterTop()
            Spacer(Modifier.height(2.dp))
            GuideTvRemoteCenter()
            Spacer(Modifier.height(2.dp))
            GuideTvRemoteCenterBottom()

            Spacer(Modifier.height(8.dp))
            GuideTvRemoteOtherKeys()

            Spacer(Modifier.height(16.dp))
            GuideTvRemoteExtraKeys()
        }
    }
}

@Composable
private fun GuideTvRemoteWrapper(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colorScheme = LocalGuideTvRemoteColorScheme.current
    CompositionLocalProvider(
        LocalContentColor provides colorScheme.onSurface,
    ) {
        Box(
            modifier = modifier
                .size(116.dp, 400.dp)
                .border(1.dp, colorScheme.border, RoundedCornerShape(24.dp))
                .background(colorScheme.surface, RoundedCornerShape(24.dp))
                .padding(12.dp, 14.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun GuideTvRemoteKey(
    modifier: Modifier = Modifier,
    imageVector: ImageVector = Icons.Default.Circle,
    size: Dp = 20.dp,
    iconsSize: Dp = 12.dp,
    surfaceColor: Color = LocalGuideTvRemoteColorScheme.current.surface,
    iconColor: Color = LocalGuideTvRemoteColorScheme.current.onSurface,
    key: GuideTvRemoteKeys? = null,
    hidden: Boolean = false,
) {
    val activeKeys = LocalGuideTvRemoteActiveKeys.current
    val colorScheme = LocalGuideTvRemoteColorScheme.current

    if (key in activeKeys) {
        Box(
            modifier = modifier
                .size(size)
                .border(1.dp, colorScheme.onSurface, CircleShape)
                .background(colorScheme.activeKeySurface, CircleShape)
        ) {
            Icon(
                imageVector,
                contentDescription = null,
                tint = colorScheme.onSurface,
                modifier = Modifier
                    .size(iconsSize)
                    .align(Alignment.Center),
            )
        }
    } else if (!hidden) {
        Box(
            modifier = modifier
                .size(size)
                .border(1.dp, colorScheme.border, CircleShape)
                .background(surfaceColor, CircleShape)
        ) {
            Icon(
                imageVector,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier
                    .size(iconsSize)
                    .align(Alignment.Center),
            )
        }
    } else {
        Box(Modifier)
    }
}

@Composable
private fun GuideTvRemoteHeader(modifier: Modifier = Modifier) {
    val colorScheme = LocalGuideTvRemoteColorScheme.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GuideTvRemoteKey(
            imageVector = Icons.Default.PowerSettingsNew,
            size = 20.dp, iconsSize = 12.dp,
        )

        Box(
            modifier = Modifier
                .size(4.dp)
                .background(colorScheme.border, CircleShape)
        )

        GuideTvRemoteKey(
            imageVector = Icons.AutoMirrored.Default.Input,
            size = 20.dp, iconsSize = 12.dp,
        )
    }
}

@Composable
private fun GuideTvRemoteCenterTop(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        GuideTvRemoteKey(
            modifier = Modifier.padding(top = 8.dp),
            imageVector = Icons.Default.BookmarkBorder,
            size = 26.dp, iconsSize = 14.dp,
        )

        GuideTvRemoteKey(
            imageVector = Icons.Default.MicNone,
            size = 26.dp, iconsSize = 14.dp,
            surfaceColor = LocalGuideTvRemoteColorScheme.current.keySurface,
        )

        GuideTvRemoteKey(
            modifier = Modifier.padding(top = 8.dp),
            imageVector = Icons.Default.Settings,
            size = 26.dp, iconsSize = 14.dp,
            key = GuideTvRemoteKeys.Settings,
        )
    }
}

@Composable
private fun GuideTvRemoteCenterBottom(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        GuideTvRemoteKey(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            size = 26.dp, iconsSize = 14.dp,
            key = GuideTvRemoteKeys.Back,
        )

        GuideTvRemoteKey(
            modifier = Modifier.padding(top = 8.dp),
            imageVector = Icons.Default.Home,
            size = 26.dp, iconsSize = 14.dp,
            surfaceColor = LocalGuideTvRemoteColorScheme.current.keySurface,
        )

        GuideTvRemoteKey(
            imageVector = Icons.Default.Tv,
            size = 26.dp, iconsSize = 14.dp,
        )
    }
}

@Composable
private fun GuideTvRemoteDirectionKey(modifier: Modifier = Modifier) {
    val colorScheme = LocalGuideTvRemoteColorScheme.current

    Box(
        modifier = modifier
            .size(96.dp)
            .border(1.dp, colorScheme.border, CircleShape)
            .background(colorScheme.directionKeySurface, CircleShape)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                GuideTvRemoteKey(
                    key = GuideTvRemoteKeys.ArrowUp,
                    hidden = true,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                GuideTvRemoteKey(
                    key = GuideTvRemoteKeys.ArrowLeft,
                    hidden = true,
                )

                GuideTvRemoteKey(
                    key = GuideTvRemoteKeys.ArrowRight,
                    hidden = true,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                GuideTvRemoteKey(
                    key = GuideTvRemoteKeys.ArrowDown,
                    hidden = true,
                )
            }
        }
    }
}

@Composable
private fun GuideTvRemoteOkKey(modifier: Modifier = Modifier) {
    val colorScheme = LocalGuideTvRemoteColorScheme.current

    Box(
        modifier = modifier
            .size(40.dp)
            .border(1.dp, colorScheme.border, CircleShape)
            .background(colorScheme.okKeySurface, CircleShape)
    ) {
        GuideTvRemoteKey(
            modifier = Modifier.align(Alignment.Center),
            key = GuideTvRemoteKeys.OK,
            hidden = true,
        )
    }
}

@Composable
private fun GuideTvRemoteCenter(modifier: Modifier = Modifier) {
    Box(modifier.size(96.dp)) {
        GuideTvRemoteDirectionKey()
        GuideTvRemoteOkKey(Modifier.align(Alignment.Center))
    }
}

@Composable
private fun GuideTvRemoteOtherKeys(modifier: Modifier = Modifier) {
    val colorScheme = LocalGuideTvRemoteColorScheme.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(26.dp, 60.dp)
                .border(1.dp, colorScheme.border, RoundedCornerShape(100.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(8.dp),
                )
                Spacer(Modifier.height(8.dp))
                Text("VOL", fontSize = TextUnit(6f, TextUnitType.Sp))
                Spacer(Modifier.height(8.dp))
                Icon(
                    Icons.Default.Remove,
                    contentDescription = null,
                    modifier = Modifier.size(8.dp),
                )
            }
        }

        GuideTvRemoteKey(
            imageVector = Icons.AutoMirrored.Filled.VolumeMute,
            size = 20.dp,
            iconsSize = 12.dp,
            iconColor = LocalGuideTvRemoteColorScheme.current.border,
        )

        Box(
            modifier = Modifier
                .size(26.dp, 60.dp)
                .border(1.dp, colorScheme.border, RoundedCornerShape(100.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    modifier = Modifier.size(8.dp),
                )
                Spacer(Modifier.height(8.dp))
                Text("CH", fontSize = TextUnit(6f, TextUnitType.Sp))
                Spacer(Modifier.height(8.dp))
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(8.dp),
                )
            }
        }
    }
}

@Composable
private fun GuideTvRemoteExtraKeys(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            GuideTvRemoteExtraKey()
            GuideTvRemoteExtraKey()
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            GuideTvRemoteExtraKey()
            GuideTvRemoteExtraKey()
        }
    }
}

@Composable
private fun GuideTvRemoteExtraKey(modifier: Modifier = Modifier) {
    val colorScheme = LocalGuideTvRemoteColorScheme.current

    Box(
        modifier = modifier
            .size(40.dp, 20.dp)
            .border(1.dp, colorScheme.border, RoundedCornerShape(100.dp))
    )
}

private data class GuideTvRemoteColorScheme(
    val surface: Color,
    val onSurface: Color,
    val border: Color,
    val keySurface: Color,
    val directionKeySurface: Color,
    val okKeySurface: Color,
    val activeKeySurface: Color,
)

private val colorSchemeDark = GuideTvRemoteColorScheme(
    surface = Color(0xFF131314),
    onSurface = Color(0xFFC7C7C7),
    border = Color(0xFF474747),
    keySurface = Color(0xFF1F2020),
    directionKeySurface = Color(0xFF1F1F1F),
    okKeySurface = Color(0xFF2A2A2A),
    activeKeySurface = Color(0xFF0842A0),
)

private val LocalGuideTvRemoteColorScheme = compositionLocalOf {
    colorSchemeDark
}

enum class GuideTvRemoteKeys(
    val label: String,
    val onTap: String? = null,
    val onDoubleTap: String? = null,
    val onLongPress: String? = null,
) {
    OK(
        label = "确认键",
        onTap = "打开选台界面；选择频道",
        onLongPress = "打开设置界面；收藏/取消收藏频道"
    ),
    Settings(label = "设置键", onTap = "打开设置界面"),
    ArrowUp(label = "方向键上", onTap = "上一个频道"),
    ArrowDown(label = "方向键下", onTap = "下一个频道", onLongPress = "打开播放控制"),
    ArrowLeft(label = "方向键左", onTap = "上一个线路", onLongPress = "打开节目单"),
    ArrowRight(label = "方向键右", onTap = "下一个线路", onLongPress = "打开多线路"),
    Back(label = "返回键", onTap = "返回", onDoubleTap = "退出应用");
}

val LocalGuideTvRemoteActiveKeys = compositionLocalOf { emptyList<GuideTvRemoteKeys>() }

@Preview
@Composable
private fun GuideTvRemotePreview() {
    MyTVTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GuideTvRemote()
            CompositionLocalProvider(
                LocalGuideTvRemoteActiveKeys provides GuideTvRemoteKeys.entries
            ) {
                GuideTvRemote()
            }
        }
    }
}