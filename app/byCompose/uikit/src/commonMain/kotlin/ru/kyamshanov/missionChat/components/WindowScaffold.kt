package ru.kyamshanov.missionChat.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

interface WindowScaffoldScope {
    fun Modifier.relative(align: WindowScaffoldRelative): Modifier
}

enum class WindowScaffoldRelative {
    TOOLBAR,
    CONTENT,
}

@Composable
fun WindowScaffold(
    modifier: Modifier = Modifier,
    minSlidebarWidth: Dp = 150.dp,
    maxSlidebarWidth: Dp = 300.dp,
    maxContentWidth: Dp = 1000.dp,
    toolbarHeight: Dp = 200.dp,
    innerPaddings: Dp = 16.dp,
    slidebarContent: @Composable (isVisible: Boolean) -> Unit,
    content: @Composable WindowScaffoldScope.() -> Unit
) {
    SubcomposeLayout(
        modifier = modifier,
    ) { constraints ->
        val innerPaddingsPx = innerPaddings.roundToPx()
        val measurables = subcompose("content") { content(WindowScaffoldScopeInstance) }

        var toolbarMeasurable: Measurable? = null
        val contentMeasurables = ArrayList<Measurable>(measurables.size)

        measurables.forEach { measurable ->
            when (measurable.parentData as? WindowScaffoldRelative) {
                WindowScaffoldRelative.TOOLBAR -> toolbarMeasurable = measurable
                else -> contentMeasurables.add(measurable)
            }
        }

        val maxContentWidthPx = maxContentWidth.roundToPx().coerceAtMost(constraints.maxWidth)
        val minSlidebarWidthPx = minSlidebarWidth.roundToPx()

        val availableSlidebarWidth = constraints.maxWidth - maxContentWidthPx
        val maxSlidebarWidthPx = maxSlidebarWidth.roundToPx().coerceAtMost(availableSlidebarWidth)
        val isSlidebarVisible = maxSlidebarWidthPx >= minSlidebarWidthPx

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

        val maxContentPlaceableWidth = contentPlaceables.maxOfOrNull { it.width } ?: 0
        val contentWidthPx = if (toolbarPlaceable != null) {
            maxContentPlaceableWidth.coerceAtLeast(toolbarPlaceable.width)
        } else {
            maxContentPlaceableWidth
        }

        val minWidth = minSlidebarWidthPx - innerPaddingsPx
        val slidebarConstraints = constraints.copy(
            minWidth = minWidth,
            maxWidth = (maxSlidebarWidthPx - innerPaddingsPx).coerceAtLeast(minWidth),
            minHeight = 0,
            maxHeight = (constraints.maxHeight - 2 * innerPaddingsPx)
        )
        val slidebarPlaceable =
            subcompose("slidebar") { slidebarContent(isSlidebarVisible) }.firstOrNull()?.measure(
                slidebarConstraints
            )

        val contentX = calculateContentX(
            isSlidebarVisible = isSlidebarVisible,
            slidebarWidth = slidebarPlaceable?.width ?: 0,
            contentWidth = contentWidthPx,
            maxWidth = constraints.maxWidth,
            minSlidebarWidth = minSlidebarWidthPx,
            maxContentWidth = maxContentWidthPx,
            padding = innerPaddingsPx,
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
    slidebarWidth: Int,
    contentWidth: Int,
    maxWidth: Int,
    minSlidebarWidth: Int,
    maxContentWidth: Int,
    padding: Int,
): Int {
    return padding + if (isSlidebarVisible) {
        val slidebarWidthWithPadding = slidebarWidth + padding
        val contentWidthWithPadding = contentWidth + 2 * padding
        val canCenterContent = contentWidthWithPadding + 2 * slidebarWidthWithPadding < maxWidth

        if (canCenterContent) {
            (maxWidth - contentWidthWithPadding) / 2
        } else {
            slidebarWidthWithPadding
        }
    } else {
        val contentWidthWithMargins = 2 * padding + contentWidth
        val minSlidebarWidthWithPadding = minSlidebarWidth + padding
        val isContentOverflowing = contentWidthWithMargins + minSlidebarWidthWithPadding >= maxWidth

        if (isContentOverflowing) {
            maxWidth - maxContentWidth
        } else {
            0
        }
    }
}


private data class WindowScaffoldAlignmentModifier(
    val alignment: WindowScaffoldRelative
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = alignment
}

private object WindowScaffoldScopeInstance : WindowScaffoldScope {
    override fun Modifier.relative(align: WindowScaffoldRelative): Modifier {
        return this.then(WindowScaffoldAlignmentModifier(align))
    }
}
