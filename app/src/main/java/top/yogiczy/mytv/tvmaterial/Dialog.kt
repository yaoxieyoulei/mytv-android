package top.yogiczy.mytv.tvmaterial

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.tv.material3.ColorScheme
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ProvideTextStyle
import androidx.tv.material3.surfaceColorAtElevation
import kotlin.math.max

/**
 * Dialogs provide important prompts in a user flow. They can require an action, communicate
 * information, or help users accomplish a task.
 *
 * The dialog will position its buttons, typically FilledButtons, based on the available space.
 * By default it will try to place them horizontally next to each other and fallback to vertical
 * placement if not enough space is available.
 *
 * @param onDismissRequest called when the user tries to dismiss the Dialog by  pressing the back
 * button. This is not called when the dismiss button is clicked.
 * @param modifier the [Modifier] to be applied to this dialog
 * @param dismissButton button which is meant to dismiss the dialog. The dialog does not set up any
 * events for this button so they need to be set up by the caller.
 * @param confirmButton button which is meant to confirm a proposed action, thus resolving what
 * triggered the dialog. The dialog does not set up any events for this button so they need to be
 * set up by the caller.
 * @param icon optional icon that will appear above the [title] or above the [text], in case
 * a title was not provided.
 * @param title title which should specify the purpose of the dialog. The title is not mandatory,
 * because there may be sufficient information inside the [text].
 * @param text text which presents the details regarding the dialog's purpose.
 * @param shape defines the shape of this dialog's container
 * @param iconContentColor the content color used for the icon.
 * @param containerColor the color used for the background of this dialog. Use [Color.Transparent]
 * to have no color.
 * @param titleContentColor the content color used for the title.
 * @param textContentColor the content color used for the [text].
 * @param tonalElevation when [containerColor] is [ColorScheme.surface], a translucent primary color
 * overlay is applied on top of the container. A higher tonal elevation value will result in a
 * darker color in light theme and lighter color in dark theme..
 * @param properties typically platform specific properties to further configure the dialog.
 */
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalTvMaterial3Api
@Composable
fun StandardDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = StandardDialogDefaults.shape,
    containerColor: Color = StandardDialogDefaults.containerColor,
    iconContentColor: Color = StandardDialogDefaults.iconContentColor,
    titleContentColor: Color = StandardDialogDefaults.titleContentColor,
    textContentColor: Color = StandardDialogDefaults.textContentColor,
    tonalElevation: Dp = StandardDialogDefaults.TonalElevation,
    properties: DialogProperties = DialogProperties(),
    confirmButton: @Composable () -> Unit,
) {
    val elevatedContainerColor = MaterialTheme.colorScheme.applyTonalElevation(
        backgroundColor = containerColor,
        elevation = tonalElevation
    )

    Dialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        Column(
            modifier = Modifier
                .widthIn(
                    min = StandardDialogDefaults.DialogMinWidth,
                    max = StandardDialogDefaults.DialogMaxWidth
                )
                .dialogFocusable()
                .then(modifier)
                .graphicsLayer {
                    this.clip = true
                    this.shape = shape
                }
                .drawBehind { drawRect(color = elevatedContainerColor) }
                .padding(StandardDialogDefaults.DialogPadding)
        ) {
            icon?.let { nnIcon ->
                CompositionLocalProvider(
                    LocalContentColor provides iconContentColor,
                    content = {
                        nnIcon()
                        Spacer(
                            modifier = Modifier.padding(StandardDialogDefaults.IconBottomSpacing)
                        )
                    }
                )
            }
            title?.let { nnTitle ->
                CompositionLocalProvider(LocalContentColor provides titleContentColor) {
                    ProvideTextStyle(
                        value = StandardDialogDefaults.titleTextStyle,
                        content = {
                            Box(
                                modifier = Modifier.heightIn(
                                    max = StandardDialogDefaults.TitleMaxHeight
                                )
                            ) { nnTitle() }
                        }
                    )
                }
            }
            text?.let { nnText ->
                CompositionLocalProvider(LocalContentColor provides textContentColor) {
                    ProvideTextStyle(
                        value = StandardDialogDefaults.textStyle,
                        content = {
                            Spacer(modifier = Modifier.padding(StandardDialogDefaults.TextPadding))
                            Box(modifier = Modifier.weight(weight = 1f, fill = false)) {
                                nnText()
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.padding(StandardDialogDefaults.ButtonsFlowRowPadding))
            ProvideTextStyle(
                value = StandardDialogDefaults.buttonsTextStyle,
                content = {
                    DialogFlowRow(
                        mainAxisSpacing = StandardDialogDefaults.ButtonsMainAxisSpacing,
                        crossAxisSpacing = StandardDialogDefaults.ButtonsCrossAxisSpacing
                    ) {
                        confirmButton()
                        dismissButton?.invoke()
                    }
                }
            )
        }
    }
}

/**
 * Full-screen dialogs fill the entire screen, containing actions that require a series of tasks to
 * complete. Because they take up the entire screen, full-screen dialogs are the only dialogs over
 * which other dialogs can appear.
 *
 * @param onDismissRequest called when the user tries to dismiss the Dialog by pressing the back
 * button. This is not called when the dismiss button is clicked.
 * @param modifier the [Modifier] to be applied to this dialog
 * @param dismissButton button which is meant to dismiss the dialog. The dialog does not set up any
 * events for this button so they need to be set up by the caller.
 * @param confirmButton button which is meant to confirm a proposed action, thus resolving what
 * triggered the dialog. The dialog does not set up any events for this button so they need to be
 * set up by the caller.
 * @param icon optional icon that will appear above the [title] or above the [text], in case
 * a title was not provided.
 * @param title title which should specify the purpose of the dialog. The title is not mandatory,
 * because there may be sufficient information inside the [text].
 * @param text text which presents the details regarding the dialog's purpose.
 * @param backgroundColor the color used for the background of this dialog. Use [Color.Transparent]
 * to have no color.
 * @param iconContentColor the content color used for the icon.
 * @param titleContentColor the content color used for the title.
 * @param textContentColor the content color used for the text.
 * @param properties typically platform specific properties to further configure the dialog.
 */
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalTvMaterial3Api
@Composable
fun FullScreenDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    backgroundColor: Color = FullScreenDialogDefaults.backgroundColor,
    iconContentColor: Color = FullScreenDialogDefaults.iconContentColor,
    titleContentColor: Color = FullScreenDialogDefaults.titleContentColor,
    textContentColor: Color = FullScreenDialogDefaults.descriptionContentColor,
    properties: DialogProperties = DialogProperties(),
    confirmButton: @Composable () -> Unit,
) {
    Dialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind { drawRect(color = backgroundColor) }
                .dialogFocusable(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(FullScreenDialogDefaults.DialogMaxWidth)
                    .then(modifier),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides iconContentColor,
                    content = {
                        icon?.let { nnIcon ->
                            nnIcon()
                            Spacer(
                                modifier = Modifier.padding(FullScreenDialogDefaults.IconPadding)
                            )
                        }
                    }
                )

                CompositionLocalProvider(
                    LocalContentColor provides titleContentColor,
                    content = {
                        title?.let { nnTitle ->
                            ProvideTextStyle(
                                value = FullScreenDialogDefaults.titleTextStyle
                            ) {
                                nnTitle()
                                Spacer(
                                    modifier = Modifier.padding(
                                        FullScreenDialogDefaults.TitlePadding
                                    )
                                )
                            }
                        }
                    }
                )

                CompositionLocalProvider(
                    LocalContentColor provides textContentColor,
                    content = {
                        text?.let { nnText ->
                            ProvideTextStyle(FullScreenDialogDefaults.descriptionTextStyle) {
                                Box(
                                    modifier = Modifier.weight(weight = 1f, fill = false)
                                ) { nnText() }
                                Spacer(
                                    modifier = Modifier.padding(
                                        FullScreenDialogDefaults.DescriptionPadding
                                    )
                                )
                            }
                        }
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = FullScreenDialogDefaults.ButtonSpacing,
                        alignment = Alignment.CenterHorizontally
                    )
                ) {
                    ProvideTextStyle(value = FullScreenDialogDefaults.buttonsTextStyle) {
                        confirmButton()
                        dismissButton?.invoke()
                    }
                }
            }
        }
    }
}

/**
 * A state object that can be hoisted to control and observe a [Dialog]'s animation progress.
 */
@ExperimentalTvMaterial3Api
class DialogState {
    /**
     * Current animation progress of the [Dialog]. This value will range between 0f and 1f. This
     * progress is generally linked with the dialog's alpha progress as it is the first element to
     * be displayed on the view and the last element to the removed from the view.
     */
    var animationProgress by mutableStateOf(0f)
        private set

    internal fun updateProgress(currentProgress: Float) {
        animationProgress = currentProgress
    }
}

/**
 * [Dialog] displays a full-screen dialog, layered over any other content. It takes a single
 * composable slot, which is expected to be an opinionated TV dialog content, such as
 * [StandardDialog], [FullScreenDialog], etc.

 * @param showDialog Controls whether to display the [Dialog]. Set to true initially to trigger
 * an 'intro' animation and display the [Dialog]. Subsequently, setting to false triggers
 * an 'outro' animation, then [Dialog] calls [onDismissRequest] and hides itself.
 * @param onDismissRequest Executes when the user dismisses the dialog.
 * Must remove the dialog from the composition.
 * @param modifier Modifier to be applied to the dialog.
 * @param properties Typically platform specific properties to further configure the dialog.
 * @param content Slot for dialog content such as [StandardDialog], [FullScreenDialog], etc.
 */
@ExperimentalComposeUiApi
@ExperimentalTvMaterial3Api
@Composable
fun Dialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    state: DialogState = remember { DialogState() },
    content: @Composable BoxScope.() -> Unit,
) {
    // Transitions for background and 'dialog content' alpha.
    var alphaTransitionState by remember {
        mutableStateOf(MutableTransitionState(AnimationStage.Intro))
    }
    val alphaTransition = updateTransition(alphaTransitionState, label = "alphaTransition")

    // Transitions for dialog content scaling.
    var scaleTransitionState by remember {
        mutableStateOf(MutableTransitionState(AnimationStage.Intro))
    }
    val scaleTransition = updateTransition(scaleTransitionState, label = "scaleTransition")

    if (showDialog || alphaTransitionState.targetState != AnimationStage.Intro ||
        scaleTransitionState.targetState != AnimationStage.Intro
    ) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = onDismissRequest,
            properties = properties,
        ) {
            val alpha by animateDialogAlpha(alphaTransition, alphaTransitionState)
            val scale by animateDialogScale(scaleTransition, scaleTransitionState)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        this.scaleX = scale
                        this.scaleY = scale
                        this.alpha = alpha
                    }
                    .semantics {
                        dismiss {
                            onDismissRequest()
                            true
                        }
                    }
                    .then(modifier),
                contentAlignment = Alignment.Center,
                content = content
            )

            LaunchedEffect(alpha) {
                state.updateProgress(currentProgress = alpha)
            }

            // Trigger Outro animation when the caller updates showDialog to false.
            LaunchedEffect(showDialog) {
                if (!showDialog) {
                    // a) Fade out dialog contents b) Scale down dialog contents.
                    alphaTransitionState.targetState = AnimationStage.Outro
                    scaleTransitionState.targetState = AnimationStage.Outro
                }
            }

            LaunchedEffect(alphaTransitionState.currentState) {
                when (alphaTransitionState.currentState) {
                    AnimationStage.Intro -> {
                        // a) Fade in dialog background b) Scale up dialog contents.
                        alphaTransitionState.targetState = AnimationStage.Display
                        scaleTransitionState.targetState = AnimationStage.Display
                    }

                    AnimationStage.Outro -> {
                        // After the outro animation, leave the dialog & reset alpha/scale
                        // transitions.
                        onDismissRequest()
                        alphaTransitionState = MutableTransitionState(AnimationStage.Intro)
                        scaleTransitionState = MutableTransitionState(AnimationStage.Intro)
                    }

                    else -> Unit
                }
            }
        }
    }
}

/**
 * Simple clone of FlowRow that arranges its children in a horizontal flow with limited
 * customization.
 */
@Composable
internal fun DialogFlowRow(
    mainAxisSpacing: Dp,
    crossAxisSpacing: Dp,
    content: @Composable () -> Unit,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Layout(content) { measurables, constraints ->
        val sequences = mutableListOf<List<Placeable>>()
        val crossAxisSizes = mutableListOf<Int>()
        val crossAxisPositions = mutableListOf<Int>()

        var mainAxisSpace = 0
        var crossAxisSpace = 0

        val currentSequence = mutableListOf<Placeable>()
        var currentMainAxisSize = 0
        var currentCrossAxisSize = 0

        // Return whether the placeable can be added to the current sequence.
        fun canAddToCurrentSequence(placeable: Placeable) =
            currentSequence.isEmpty() || currentMainAxisSize + mainAxisSpacing.roundToPx() +
                    placeable.width <= constraints.maxWidth

        // Store current sequence information and start a new sequence.
        fun startNewSequence() {
            if (sequences.isNotEmpty()) {
                crossAxisSpace += crossAxisSpacing.roundToPx()
            }
            sequences += currentSequence.toList()
            crossAxisSizes += currentCrossAxisSize
            crossAxisPositions += crossAxisSpace

            crossAxisSpace += currentCrossAxisSize
            mainAxisSpace = max(mainAxisSpace, currentMainAxisSize)

            currentSequence.clear()
            currentMainAxisSize = 0
            currentCrossAxisSize = 0
        }

        val measurablesList = if (isRtl) measurables.reversed() else measurables

        for (measurable in measurablesList) {
            // Ask the child for its preferred size.
            val placeable = measurable.measure(constraints)

            // Start a new sequence if there is not enough space.
            if (!canAddToCurrentSequence(placeable)) startNewSequence()

            // Add the child to the current sequence.
            if (currentSequence.isNotEmpty()) {
                currentMainAxisSize += mainAxisSpacing.roundToPx()
            }
            currentSequence.add(placeable)
            currentMainAxisSize += placeable.width
            currentCrossAxisSize = max(currentCrossAxisSize, placeable.height)
        }

        if (currentSequence.isNotEmpty()) startNewSequence()

        val mainAxisLayoutSize = max(mainAxisSpace, constraints.minWidth)

        val crossAxisLayoutSize = max(crossAxisSpace, constraints.minHeight)

        layout(mainAxisLayoutSize, crossAxisLayoutSize) {
            sequences.forEachIndexed { i, placeables ->
                val childrenMainAxisSizes = IntArray(placeables.size) { j ->
                    placeables[j].width +
                            if (j < placeables.lastIndex) mainAxisSpacing.roundToPx() else 0
                }
                val arrangement = Arrangement.Bottom
                // Handle vertical direction
                val mainAxisPositions = IntArray(childrenMainAxisSizes.size) { 0 }
                with(arrangement) {
                    arrange(mainAxisLayoutSize, childrenMainAxisSizes, mainAxisPositions)
                }
                placeables.forEachIndexed { j, placeable ->
                    placeable.place(
                        x = mainAxisPositions[j],
                        y = crossAxisPositions[i]
                    )
                }
            }
        }
    }
}

/**
 * Makes the current dialog a focus group with a [FocusRequester] and restricts the focus from
 * exiting its bounds while it's visible.
 */
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
private fun Modifier.dialogFocusable() = composed {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        focusManager.moveFocus(FocusDirection.Enter)
    }
    this.then(
        Modifier
            .focusRequester(focusRequester)
            .focusProperties { exit = { FocusRequester.Cancel } }
            .focusGroup()
    )
}

@ExperimentalTvMaterial3Api
object StandardDialogDefaults {
    internal val DialogMinWidth = 280.dp
    internal val DialogMaxWidth = 560.dp

    internal val TitleMaxHeight = 56.dp
    internal val ButtonsMainAxisSpacing = 16.dp
    internal val ButtonsCrossAxisSpacing = 16.dp

    internal val DialogPadding = PaddingValues(all = 24.dp)
    internal val IconBottomSpacing = PaddingValues(top = 32.dp)
    internal val TextPadding = PaddingValues(top = 20.dp)
    internal val ButtonsFlowRowPadding = PaddingValues(top = 24.dp)
    private const val TextColorOpacity = 0.8f

    /** The default shape for StandardDialogs */
    val shape: Shape = RoundedCornerShape(28.0.dp)

    /** The default container color for StandardDialogs */
    val containerColor: Color
        @ReadOnlyComposable
        @Composable get() = MaterialTheme.colorScheme.inverseOnSurface

    /** The default icon color for StandardDialogs */
    val iconContentColor: Color
        @ReadOnlyComposable
        @Composable get() = MaterialTheme.colorScheme.secondary

    /** The default title color for StandardDialogs */
    val titleContentColor: Color
        @ReadOnlyComposable
        @Composable get() = MaterialTheme.colorScheme.onSurface

    /** The default [TextStyle] for StandardDialogs' title */
    val titleTextStyle: TextStyle
        @ReadOnlyComposable
        @Composable get() = MaterialTheme.typography.headlineMedium

    /** The default [TextStyle] for StandardDialogs' buttons */
    val buttonsTextStyle
        @ReadOnlyComposable
        @Composable get() = MaterialTheme.typography.labelLarge

    /** The default text color for StandardDialogs */
    val textContentColor: Color
        @ReadOnlyComposable

        @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

    /** The default text style for StandardDialogs */
    val textStyle
        @ReadOnlyComposable
        @Composable get() = MaterialTheme.typography.bodyLarge
            .copy(color = LocalContentColor.current.copy(alpha = TextColorOpacity))

    /** The default tonal elevation for StandardDialogs */
    val TonalElevation: Dp = Elevation.Level2
}

@ExperimentalTvMaterial3Api
object FullScreenDialogDefaults {
    internal val ButtonSpacing = 16.dp
    internal val DescriptionPadding = PaddingValues(top = 48.dp)
    internal val TitlePadding = PaddingValues(top = 20.dp)
    internal val IconPadding = PaddingValues(top = 32.dp)
    internal const val DialogMaxWidth = .5f
    private const val DescriptionColorOpacity = 0.8f

    /** The default background color for FullScreenDialogs */
    val backgroundColor: Color
        @ReadOnlyComposable
        @Composable get() = MaterialTheme.colorScheme.background

    /** The default icon color for FullScreenDialogs */
    val iconContentColor: Color
        @ReadOnlyComposable
        @Composable get() = MaterialTheme.colorScheme.onSurface

    /** The default title color for FullScreenDialogs */
    val titleContentColor: Color
        @ReadOnlyComposable
        @Composable get() = MaterialTheme.colorScheme.onSurface

    /** The default title text style for FullScreenDialogs */
    val titleTextStyle: TextStyle
        @ReadOnlyComposable
        @Composable get() = MaterialTheme.typography.headlineMedium
            .copy(textAlign = TextAlign.Center)

    /** The default buttons text style for FullScreenDialogs */
    val buttonsTextStyle: TextStyle
        @ReadOnlyComposable
        @Composable get() = MaterialTheme.typography.labelLarge

    /** The default description text color for FullScreenDialogs */
    val descriptionContentColor: Color
        @ReadOnlyComposable
        @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

    /** The default description text style for FullScreenDialogs */
    val descriptionTextStyle: TextStyle
        @ReadOnlyComposable
        @Composable get() = MaterialTheme.typography.bodyLarge.copy(
            textAlign = TextAlign.Center,
            color = LocalContentColor.current.copy(alpha = DescriptionColorOpacity)
        )
}

@Composable
private fun animateDialogAlpha(
    alphaTransition: Transition<AnimationStage>,
    alphaTransitionState: MutableTransitionState<AnimationStage>,
) = alphaTransition.animateFloat(
    transitionSpec = {
        if (alphaTransitionState.currentState == AnimationStage.Intro)
            tween(
                durationMillis = ENTER_DURATION,
                easing = MotionTokens.EnterEasing,
                delayMillis = ENTER_DELAY
            )
        else if (alphaTransitionState.targetState == AnimationStage.Outro)
            tween(
                durationMillis = EXIT_DURATION,
                easing = MotionTokens.ExitEasing,
                delayMillis = EXIT_DELAY
            )
        else
            tween(durationMillis = 0)
    },
    label = "alpha"
) { stage ->
    when (stage) {
        AnimationStage.Intro -> 0.0f
        AnimationStage.Display -> 1.0f
        AnimationStage.Outro -> 0.0f
    }
}

@Composable
private fun animateDialogScale(
    scaleTransition: Transition<AnimationStage>,
    scaleTransitionState: MutableTransitionState<AnimationStage>,
) = scaleTransition.animateFloat(
    transitionSpec = {
        if (scaleTransitionState.currentState == AnimationStage.Intro)
            tween(
                durationMillis = ENTER_DURATION,
                easing = MotionTokens.EnterEasing,
                delayMillis = ENTER_DELAY
            )
        else
            tween(
                durationMillis = EXIT_DURATION,
                easing = MotionTokens.ExitEasing,
                delayMillis = EXIT_DELAY
            )
    },
    label = "scale"
) { stage ->
    when (stage) {
        AnimationStage.Intro -> 0.97f
        AnimationStage.Display -> 1.0f
        AnimationStage.Outro -> 0.97f
    }
}

// Transition stages - scaling and alpha is applied as single Intro/Outro animations.
private enum class AnimationStage {
    Intro, Display, Outro;
}

private const val ENTER_DURATION = 500
private const val EXIT_DURATION = 250

private const val ENTER_DELAY = 250
private const val EXIT_DELAY = 150

object MotionTokens {
    val EnterEasing = CubicBezierEasing(0.12f, 1f, 0.4f, 1f)
    val ExitEasing = CubicBezierEasing(0.4f, 1f, 0.12f, 1f)
}

private object Elevation {
    val Level0 = 0.0.dp
    val Level1 = 1.0.dp
    val Level2 = 3.0.dp
    val Level3 = 6.0.dp
    val Level4 = 8.0.dp
    val Level5 = 12.0.dp
}

@OptIn(ExperimentalTvMaterial3Api::class)
private fun ColorScheme.applyTonalElevation(backgroundColor: Color, elevation: Dp): Color {
    return if (backgroundColor == surface) {
        surfaceColorAtElevation(elevation)
    } else {
        backgroundColor
    }
}