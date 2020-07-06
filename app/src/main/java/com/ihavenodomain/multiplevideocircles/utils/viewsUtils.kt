package com.ihavenodomain.multiplevideocircles.utils

import com.ihavenodomain.multiplevideocircles.ui.views.TakenCoordinates


/**
 * [Triple.first] represents X
 *
 * [Triple.second] represents Y
 *
 * [Triple.third] represents Size (Width or height) of square object
 */
typealias PositionAndSize = Triple<Float, Float, Int>

/**
 * NOTE: For squares only!
 *
 * Shows if one square overlaps another.
 *
 * @param pos position and side sizes of a new not added view.
 * @return true if one square overlaps another in coordinate system.
 */
fun PositionAndSize.willBeIntersectedByPosition(pos: PositionAndSize): Boolean {
    val (x1, y1, size1) = this
    val (x2, y2, size2) = pos

    var collideX = false
    var collideY = false

    if ((x1 + size1 >= x2) && (x1 <= x2 + size2)) {
        collideX = true
    }

    if ((y1 + size1 >= y2) && (y1 <= y2 + size2)) {
        collideY = true
    }

    return collideX && collideY
}

/**
 * Calculates a new position for View.
 *
 * @param maxSize represents X and Y max values
 * @return An appropriate position for a view that's not crawl out of [maxSize] bounds.
 */
fun PositionAndSize.findNearestPositionForMove(
    maxSize: Pair<Int, Int>
): Pair<Float, Float> {
    val (x, y, size) = this
    val (maxX, maxY) = maxSize

    val radius = size / 2
    // As improvement: need to count appropriate nearest empty space for whole view to set view to it.
    // This method is currently doing a simpler job.
    val newX = when {
        x < radius -> {
            0F
        }
        x > maxX - radius -> {
            maxX.toFloat() - this.third
        }
        else -> {
            // We need to subtract radius from the value because
            // we want the view's center to be equal to clicked point
            x - radius
        }
    }

    val newY = when {
        y < radius -> {
            0F
        }
        y > maxY - radius -> {
            maxY.toFloat() - this.third
        }
        else -> {
            // We need to subtract radius from the value because
            // we want the view's center to be equal to clicked point
            y - radius
        }
    }

    return newX to newY
}

/**
 * Determines that a [desiredPos] is in bounds of any item of [TakenCoordinates] passed.
 *
 * [TakenCoordinates] imply that every item has size equal to [desiredPos.third].
 */
fun TakenCoordinates.isTouchOverStoredView(desiredPos: PositionAndSize): Boolean {
    for (item in this) {
        val tempPos = item.value

        val isBetweenX =
            desiredPos.first >= tempPos.first && desiredPos.first <= tempPos.first + desiredPos.third
        val isBetweenY =
            desiredPos.second >= tempPos.second && desiredPos.second <= tempPos.second + desiredPos.third

        if (isBetweenX && isBetweenY) return true
    }

    return false
}
