package top.yogiczy.mytv.tv.ui.utils

import android.os.Build
import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

fun Modifier.ifElse(
    condition: () -> Boolean, ifTrueModifier: Modifier, ifFalseModifier: Modifier = Modifier
): Modifier = then(if (condition()) ifTrueModifier else ifFalseModifier)

fun Modifier.ifElse(
    condition: Boolean, ifTrueModifier: Modifier, ifFalseModifier: Modifier = Modifier
): Modifier = ifElse({ condition }, ifTrueModifier, ifFalseModifier)

fun Modifier.focusOnLaunched(key: Any = Unit): Modifier = composed {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(key) { focusRequester.requestFocus() }
    focusRequester(focusRequester)
}

fun Modifier.focusOnLaunchedSaveable(key: Any = Unit): Modifier = composed {
    val focusRequester = remember { FocusRequester() }
    var hasFocused by rememberSaveable(key) { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!hasFocused) {
            focusRequester.requestFocus()
            hasFocused = true
        }
    }
    focusRequester(focusRequester)
}

fun Modifier.handleKeyEvents(
    onKeyTap: Map<Int, () -> Unit> = emptyMap(),
    onKeyLongTap: Map<Int, () -> Unit> = emptyMap(),
): Modifier {
    val keyDownMap = mutableMapOf<Int, Boolean>()

    return onPreviewKeyEvent {
        when (it.nativeKeyEvent.action) {
            KeyEvent.ACTION_DOWN -> {
                if (it.nativeKeyEvent.repeatCount == 0) {
                    keyDownMap[it.nativeKeyEvent.keyCode] = true
                } else if (it.nativeKeyEvent.repeatCount == 1) {
                    keyDownMap.remove(it.nativeKeyEvent.keyCode)
                    onKeyLongTap[it.nativeKeyEvent.keyCode]?.invoke()
                }
            }

            KeyEvent.ACTION_UP -> {
                if (keyDownMap[it.nativeKeyEvent.keyCode] == true) {
                    keyDownMap.remove(it.nativeKeyEvent.keyCode)
                    onKeyTap[it.nativeKeyEvent.keyCode]?.invoke()
                }
            }
        }

        false
    }
}

fun Modifier.handleDragGestures(
    onSwipeUp: () -> Unit = {},
    onSwipeDown: () -> Unit = {},
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
): Modifier {
    val speedThreshold = 100.dp
    val distanceThreshold = 10.dp

    val verticalTracker = VelocityTracker()
    var verticalDragOffset = 0f
    val horizontalTracker = VelocityTracker()
    var horizontalDragOffset = 0f


    return this
        .pointerInput(Unit) {
            detectVerticalDragGestures(
                onDragEnd = {
                    if (verticalDragOffset.absoluteValue > distanceThreshold.toPx()) {
                        if (verticalTracker.calculateVelocity().y > speedThreshold.toPx()) {
                            onSwipeDown()
                        } else if (verticalTracker.calculateVelocity().y < -speedThreshold.toPx()) {
                            onSwipeUp()
                        }
                    }
                },
            ) { change, dragAmount ->
                verticalDragOffset += dragAmount
                verticalTracker.addPosition(change.uptimeMillis, change.position)
            }
        }
        .pointerInput(Unit) {
            detectHorizontalDragGestures(
                onDragEnd = {
                    if (horizontalDragOffset.absoluteValue > distanceThreshold.toPx()) {
                        if (horizontalTracker.calculateVelocity().x > speedThreshold.toPx()) {
                            onSwipeRight()
                        } else if (horizontalTracker.calculateVelocity().x < -speedThreshold.toPx()) {
                            onSwipeLeft()
                        }
                    }
                },
            ) { change, dragAmount ->
                horizontalDragOffset += dragAmount
                horizontalTracker.addPosition(change.uptimeMillis, change.position)
            }
        }
}

fun Modifier.clickableNoIndication(
    onLongClick: () -> Unit = { },
    onDoubleClick: () -> Unit = { },
    onClick: () -> Unit,
) = composed {
    val currentOnClick by rememberUpdatedState(onClick)
    val currentOnLongClick by rememberUpdatedState(onLongClick)
    val currentOnDoubleClick by rememberUpdatedState(onDoubleClick)

    pointerInput(Unit) {
        detectTapGestures(
            onTap = { currentOnClick() },
            onDoubleTap = { currentOnDoubleClick() },
            onLongPress = { currentOnLongClick() }
        )
    }
}

fun Modifier.handleKeyEvents(
    onLeft: () -> Unit = {},
    onLongLeft: () -> Unit = {},
    onRight: () -> Unit = {},
    onLongRight: () -> Unit = {},
    onUp: () -> Unit = {},
    onLongUp: () -> Unit = {},
    onDown: () -> Unit = {},
    onLongDown: () -> Unit = {},
    onSelect: () -> Unit = {},
    onLongSelect: () -> Unit = {},
    onSettings: () -> Unit = {},
    onNumber: (Int) -> Unit = {},
) = handleKeyEvents(
    onKeyTap = mapOf(
        KeyEvent.KEYCODE_DPAD_LEFT to onLeft,
        KeyEvent.KEYCODE_DPAD_RIGHT to onRight,
        KeyEvent.KEYCODE_DPAD_UP to onUp,
        KeyEvent.KEYCODE_CHANNEL_UP to onUp,
        KeyEvent.KEYCODE_DPAD_DOWN to onDown,
        KeyEvent.KEYCODE_CHANNEL_DOWN to onDown,

        KeyEvent.KEYCODE_DPAD_CENTER to onSelect,
        KeyEvent.KEYCODE_ENTER to onSelect,
        KeyEvent.KEYCODE_NUMPAD_ENTER to onSelect,

        KeyEvent.KEYCODE_MENU to onSettings,
        KeyEvent.KEYCODE_SETTINGS to onSettings,
        KeyEvent.KEYCODE_HELP to onSettings,
        KeyEvent.KEYCODE_H to onSettings,

        KeyEvent.KEYCODE_L to onLongSelect,
        KeyEvent.KEYCODE_W to onLongUp,
        KeyEvent.KEYCODE_S to onLongDown,
        KeyEvent.KEYCODE_A to onLongLeft,
        KeyEvent.KEYCODE_D to onLongRight,

        KeyEvent.KEYCODE_0 to { onNumber(0) },
        KeyEvent.KEYCODE_1 to { onNumber(1) },
        KeyEvent.KEYCODE_2 to { onNumber(2) },
        KeyEvent.KEYCODE_3 to { onNumber(3) },
        KeyEvent.KEYCODE_4 to { onNumber(4) },
        KeyEvent.KEYCODE_5 to { onNumber(5) },
        KeyEvent.KEYCODE_6 to { onNumber(6) },
        KeyEvent.KEYCODE_7 to { onNumber(7) },
        KeyEvent.KEYCODE_8 to { onNumber(8) },
        KeyEvent.KEYCODE_9 to { onNumber(9) },
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            KeyEvent.KEYCODE_SYSTEM_NAVIGATION_LEFT to onLeft
            KeyEvent.KEYCODE_SYSTEM_NAVIGATION_RIGHT to onRight
            KeyEvent.KEYCODE_SYSTEM_NAVIGATION_UP to onUp
            KeyEvent.KEYCODE_SYSTEM_NAVIGATION_DOWN to onDown
        }
    },
    onKeyLongTap = mapOf(
        KeyEvent.KEYCODE_DPAD_LEFT to onLongLeft,
        KeyEvent.KEYCODE_DPAD_RIGHT to onLongRight,
        KeyEvent.KEYCODE_DPAD_UP to onLongUp,
        KeyEvent.KEYCODE_CHANNEL_UP to onLongUp,
        KeyEvent.KEYCODE_DPAD_DOWN to onLongDown,
        KeyEvent.KEYCODE_CHANNEL_DOWN to onLongDown,

        KeyEvent.KEYCODE_ENTER to onLongSelect,
        KeyEvent.KEYCODE_NUMPAD_ENTER to onLongSelect,
        KeyEvent.KEYCODE_DPAD_CENTER to onLongSelect,
    ),
)
    .clickableNoIndication(
        onLongClick = onLongSelect,
        onDoubleClick = onSettings,
        onClick = onSelect,
    )

fun Modifier.handleKeyEvents(
    isFocused: () -> Boolean,
    focusRequester: FocusRequester,
    onLeft: () -> Unit = {},
    onLongLeft: () -> Unit = {},
    onRight: () -> Unit = {},
    onLongRight: () -> Unit = {},
    onUp: () -> Unit = {},
    onLongUp: () -> Unit = {},
    onDown: () -> Unit = {},
    onLongDown: () -> Unit = {},
    onSelect: () -> Unit = {},
    onLongSelect: () -> Unit = {},
    onSettings: () -> Unit = {},
    onNumber: (Int) -> Unit = {},
) = handleKeyEvents(
    onLeft = { if (isFocused()) onLeft() else focusRequester.requestFocus() },
    onLongLeft = { if (isFocused()) onLongLeft() else focusRequester.requestFocus() },
    onRight = { if (isFocused()) onRight() else focusRequester.requestFocus() },
    onLongRight = { if (isFocused()) onLongRight() else focusRequester.requestFocus() },
    onUp = { if (isFocused()) onUp() else focusRequester.requestFocus() },
    onLongUp = { if (isFocused()) onLongUp() else focusRequester.requestFocus() },
    onDown = { if (isFocused()) onDown() else focusRequester.requestFocus() },
    onLongDown = { if (isFocused()) onLongDown() else focusRequester.requestFocus() },
    onSelect = { if (isFocused()) onSelect() else focusRequester.requestFocus() },
    onLongSelect = { if (isFocused()) onLongSelect() else focusRequester.requestFocus() },
    onSettings = { if (isFocused()) onSettings() else focusRequester.requestFocus() },
    onNumber = { if (isFocused()) onNumber(it) else focusRequester.requestFocus() },
)

fun Modifier.captureBackKey(onBackPressed: () -> Unit) = this.onPreviewKeyEvent {
    if (it.key == Key.Back && it.type == KeyEventType.KeyUp) {
        onBackPressed()
        true
    } else {
        false
    }
}

fun Modifier.customBackground() = background(
    brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF141E30),
            Color(0xFF243B55),
            Color(0xFF141E30)
        ),
    )
)