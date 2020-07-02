package com.ihavenodomain.multiplevideocircles

import com.ihavenodomain.multiplevideocircles.ui.views.TakenCoordinates
import com.ihavenodomain.multiplevideocircles.utils.PositionAndSize
import com.ihavenodomain.multiplevideocircles.utils.isTouchOverStoredView
import com.ihavenodomain.multiplevideocircles.utils.willBeIntersectedByPosition
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before


class ViewsIntersectionUnitTest {

    private val takenCoordinates: TakenCoordinates = TakenCoordinates()

    @Before
    fun createCoordinates() {
        takenCoordinates.clear()

        takenCoordinates[0] = 0F to 0F
        takenCoordinates[1] = 101F to 101F
        takenCoordinates[2] = 101F to 202F
    }

    @Test
    fun touchOverStoredView() {
        val pos1 = PositionAndSize(50F, 0F, 100)

        assertEquals(takenCoordinates.isTouchOverStoredView(pos1), true)
    }

    @Test
    fun touchNotOverStoredView() {
        val pos1 = PositionAndSize(250F, 250F, 100)

        assertEquals(takenCoordinates.isTouchOverStoredView(pos1), false)
    }

    @Test
    fun positionsShouldNotIntersect() {
        val pos1 = PositionAndSize(0F, 0F, 100)
        val pos2 = PositionAndSize(101F, 101F, 100)

        assertEquals(pos1.willBeIntersectedByPosition(pos2), false)
    }

    @Test
    fun positionsShouldIntersect() {
        val pos1 = PositionAndSize(0F, 0F, 100)
        val pos2 = PositionAndSize(50F, 50F, 100)

        assertEquals(pos1.willBeIntersectedByPosition(pos2), true)
    }

    @Test
    fun positionsShouldIntersectLeftBorder() {
        val pos1 = PositionAndSize(100F, 50F, 100)
        val pos2 = PositionAndSize(0F, 50F, 100)

        assertEquals(pos1.willBeIntersectedByPosition(pos2), true)
    }

    @Test
    fun positionsShouldIntersectRightBorder() {
        val pos1 = PositionAndSize(0F, 0F, 100)
        val pos2 = PositionAndSize(100F, 50F, 100)

        assertEquals(pos1.willBeIntersectedByPosition(pos2), true)
    }

    @Test
    fun positionsShouldIntersectTopBorder() {
        val pos1 = PositionAndSize(0F, 0F, 100)
        val pos2 = PositionAndSize(100F, 50F, 100)

        assertEquals(pos1.willBeIntersectedByPosition(pos2), true)
    }

    @Test
    fun positionsShouldIntersectBottomBorder() {
        val pos1 = PositionAndSize(0F, 0F, 100)
        val pos2 = PositionAndSize(50F, 100F, 100)

        assertEquals(pos1.willBeIntersectedByPosition(pos2), true)
    }
}