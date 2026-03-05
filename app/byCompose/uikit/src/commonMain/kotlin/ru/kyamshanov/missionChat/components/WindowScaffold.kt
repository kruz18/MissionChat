package ru.kyamshanov.missionChat.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

interface WindowScaffoldScope {
    fun Modifier.relative(align: WindowScaffoldRelative): Modifier
}

enum class WindowScaffoldRelative {
    TOOLBAR,
    SLIDEBAR,
    CONTENT,
}

private data class WindowScaffoldAlignmentModifier(
    val alignment: WindowScaffoldRelative
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = alignment
}

internal object WindowScaffoldScopeInstance : WindowScaffoldScope {
    override fun Modifier.relative(align: WindowScaffoldRelative): Modifier {
        return this.then(WindowScaffoldAlignmentModifier(align))
    }
}

@Composable
fun WindowScaffold(
    modifier: Modifier = Modifier,
    minSlidebarWidth: Dp = 150.dp,
    maxSlidebarWidth: Dp = 300.dp,
    maxContentWidth: Dp = 1000.dp,
    toolbarHeight: Dp = 200.dp,
    innerPaddings: Dp = 16.dp,
    content: @Composable WindowScaffoldScope.() -> Unit
) {
    Layout(
        content = { WindowScaffoldScopeInstance.content() },
        modifier = modifier
    ) { measurables, constraints ->
        val innerPaddingsPx = innerPaddings.roundToPx()

        var toolbarMeasurable: Measurable? = null
        var slidebarMeasurable: Measurable? = null
        val contentMeasurables = ArrayList<Measurable>(measurables.size)

        measurables.forEach { measurable ->
            when (measurable.parentData as? WindowScaffoldRelative) {
                WindowScaffoldRelative.TOOLBAR -> toolbarMeasurable = measurable
                WindowScaffoldRelative.SLIDEBAR -> slidebarMeasurable = measurable
                else -> contentMeasurables.add(measurable)
            }
        }

        val maxContentWidthPx = maxContentWidth.roundToPx().coerceAtMost(constraints.maxWidth)
        val minSlidebarWidthPx = minSlidebarWidth.roundToPx()

        val availableSlidebarWidth = (constraints.maxWidth - maxContentWidthPx).coerceAtLeast(0)
        val maxSlidebarWidthPx = maxSlidebarWidth.roundToPx().coerceAtMost(availableSlidebarWidth)

        val isSlidebarVisible = slidebarMeasurable != null && maxSlidebarWidthPx >= minSlidebarWidthPx

        val toolbarPlaceable = toolbarMeasurable?.measure(
            constraints.copy(
                minWidth = 0,
                maxWidth = (maxContentWidthPx - 2 * innerPaddingsPx).coerceAtLeast(0),
                minHeight = 0,
                maxHeight = toolbarHeight.roundToPx()
            )
        )
        val toolbarHeightPx = toolbarPlaceable?.height ?: 0

        val contentConstraint = constraints.copy(
            minWidth = 0,
            maxWidth = (maxContentWidthPx - 2 * innerPaddingsPx).coerceAtLeast(0),
            minHeight = 0,
            maxHeight = (constraints.maxHeight - toolbarHeightPx - 3 * innerPaddingsPx).coerceAtLeast(0)
        )
        val contentPlaceables = contentMeasurables.map { it.measure(contentConstraint) }

        val contentWidthPx = (contentPlaceables.maxOfOrNull { it.width } ?: 0)
            .let { if (toolbarPlaceable != null) it.coerceAtLeast(toolbarPlaceable.width) else it }

        val slidebarPlaceable = if (isSlidebarVisible && slidebarMeasurable != null) {
            slidebarMeasurable.measure(
                constraints.copy(
                    minWidth = (minSlidebarWidthPx - innerPaddingsPx).coerceAtLeast(0),
                    maxWidth = (maxSlidebarWidthPx - innerPaddingsPx).coerceAtLeast(0),
                    minHeight = 0,
                    maxHeight = (constraints.maxHeight - 2 * innerPaddingsPx).coerceAtLeast(0)
                )
            )
        } else {
            null
        }

        val contentX = calculateContentX(
            isSlidebarVisible = isSlidebarVisible,
            slidebarPlaceableWidth = slidebarPlaceable?.width ?: 0,
            contentWidthPx = contentWidthPx,
            constraintsMaxWidth = constraints.maxWidth,
            minSlidebarWidthPx = minSlidebarWidthPx,
            maxContentWidthPx = maxContentWidthPx,
            paddingPx = innerPaddingsPx,
        )

        layout(constraints.maxWidth, constraints.maxHeight) {
            contentPlaceables.forEach { it.placeRelative(contentX, toolbarHeightPx + innerPaddingsPx) }
            toolbarPlaceable?.placeRelative(contentX, innerPaddingsPx)
            slidebarPlaceable?.placeRelative(innerPaddingsPx, innerPaddingsPx)
        }
    }
}

private fun calculateContentX(
    isSlidebarVisible: Boolean,
    slidebarPlaceableWidth: Int,
    contentWidthPx: Int,
    constraintsMaxWidth: Int,
    minSlidebarWidthPx: Int,
    maxContentWidthPx: Int,
    paddingPx: Int,
): Int {
    return paddingPx + if (isSlidebarVisible) {
        val slidebarWidthWithPadding = slidebarPlaceableWidth + paddingPx
        val contentWidthWithPadding = contentWidthPx + 2 * paddingPx
        if (contentWidthWithPadding + 2 * slidebarWidthWithPadding < constraintsMaxWidth) {
            constraintsMaxWidth / 2 - contentWidthWithPadding / 2
        } else {
            slidebarWidthWithPadding
        }
    } else {
        val contentWidthWithMargins = 2 * paddingPx + contentWidthPx
        val minSlidebarWidthWithPadding = minSlidebarWidthPx + paddingPx
        if (contentWidthWithMargins + minSlidebarWidthWithPadding >= constraintsMaxWidth) {
            minSlidebarWidthWithPadding - ((maxContentWidthPx + minSlidebarWidthWithPadding) - constraintsMaxWidth)
        } else {
            0
        }
    }
}
