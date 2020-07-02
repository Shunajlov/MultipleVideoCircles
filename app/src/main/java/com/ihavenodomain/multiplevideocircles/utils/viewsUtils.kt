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
    var collideX = false
    var collideY = false

    if ((this.first + this.third >= pos.first) && (this.first <= pos.first + pos.third)) {
        collideX = true
    }

    if ((this.second + this.third >= pos.second) && (this.second <= pos.second + pos.third)) {
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
    val radius = this.third / 2
    // As improvement: need to count appropriate nearest empty space for whole view to set view to it.
    // This method is currently doing a simpler job.
    val newX = when {
        this.first < radius -> {
            0F
        }
        this.first > maxSize.first - radius -> {
            maxSize.first.toFloat() - this.third
        }
        else -> {
            this.first - radius
        }
    }

    val newY = when {
        this.second < radius -> {
            0F
        }
        this.second > maxSize.second - radius -> {
            maxSize.second.toFloat() - this.third
        }
        else -> {
            this.second - radius
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
