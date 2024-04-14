package top.yogiczy.mytv.ui.utils

import android.os.Build
import android.view.KeyEvent
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged

/**
 * 监听短按、长按按键事件
 */
fun Modifier.handleKeyEvents(
    onKeyTap: Map<Int, () -> Unit> = emptyMap(),
    onKeyLongTap: Map<Int, () -> Unit> = emptyMap(),
): Modifier {
    val keyDownMap = mutableMapOf<Int, Boolean>()

    return onKeyEvent {
        when (it.nativeKeyEvent.action) {
            KeyEvent.ACTION_DOWN -> {
                if (it.nativeKeyEvent.repeatCount == 0) {
                    keyDownMap[it.nativeKeyEvent.keyCode] = true
                } else if (it.nativeKeyEvent.repeatCount == 1) {
                    onKeyLongTap[it.nativeKeyEvent.keyCode]?.invoke()
                    keyDownMap.remove(it.nativeKeyEvent.keyCode)
                }
            }

            KeyEvent.ACTION_UP -> {
                if (keyDownMap[it.nativeKeyEvent.keyCode] != true) return@onKeyEvent true
                keyDownMap.remove(it.nativeKeyEvent.keyCode)

                onKeyTap[it.nativeKeyEvent.keyCode]?.invoke()
            }
        }

        true
    }
}

/**
 * 监听全方位的DPad按键事件
 */
fun Modifier.handleDPadKeyEvents(
    onLeft: (() -> Unit) = {},
    onRight: (() -> Unit) = {},
    onUp: (() -> Unit) = {},
    onDown: (() -> Unit) = {},
    onEnter: (() -> Unit) = {},
    onLongEnter: (() -> Unit) = {},
    onSettings: (() -> Unit) = {},
    onNumber: ((Int) -> Unit) = {},
) = handleKeyEvents(
    onKeyTap = mapOf(
        KeyEvent.KEYCODE_DPAD_LEFT to onLeft,
        KeyEvent.KEYCODE_DPAD_RIGHT to onRight,
        KeyEvent.KEYCODE_DPAD_UP to onUp,
        KeyEvent.KEYCODE_CHANNEL_UP to onUp,
        KeyEvent.KEYCODE_DPAD_DOWN to onDown,
        KeyEvent.KEYCODE_CHANNEL_DOWN to onDown,

        KeyEvent.KEYCODE_DPAD_CENTER to onEnter,
        KeyEvent.KEYCODE_ENTER to onEnter,
        KeyEvent.KEYCODE_NUMPAD_ENTER to onEnter,

        KeyEvent.KEYCODE_MENU to onSettings,
        KeyEvent.KEYCODE_SETTINGS to onSettings,
        KeyEvent.KEYCODE_HELP to onSettings,
        KeyEvent.KEYCODE_H to onSettings,
        KeyEvent.KEYCODE_UNKNOWN to onSettings,

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
        KeyEvent.KEYCODE_ENTER to onLongEnter,
        KeyEvent.KEYCODE_NUMPAD_ENTER to onLongEnter,
        KeyEvent.KEYCODE_DPAD_CENTER to onLongEnter,
    ),
)

/**
 * 监听手势上下滑动事件
 */
fun Modifier.handleVerticalDragGestures(
    onSwipeUp: () -> Unit = {},
    onSwipeDown: () -> Unit = {},
): Modifier {
    var startY = 0f

    return this then pointerInput(Unit) {
        detectVerticalDragGestures { change, _ ->
            when {
                change.positionChanged() -> {
                    if (startY == 0f) {
                        startY = change.position.y
                    }
                }

                change.position.y - startY > 0 -> {
                    onSwipeDown()
                    startY = 0f
                }

                change.position.y - startY < 0 -> {
                    onSwipeUp()
                    startY = 0f
                }
            }
        }
    }
}