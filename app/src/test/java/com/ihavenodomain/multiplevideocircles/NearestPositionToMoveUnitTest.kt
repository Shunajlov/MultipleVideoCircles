package com.ihavenodomain.multiplevideocircles

import com.ihavenodomain.multiplevideocircles.utils.PositionAndSize
import com.ihavenodomain.multiplevideocircles.utils.findNearestPositionForMove
import org.junit.Test
import org.junit.Assert.*
import kotlin.random.Random

class NearestPositionToMoveUnitTest {

    private val maxDisplayX = 500
    private val maxDisplayY = 500
    private val defaultSideSize = 100
    private val maxSafeX = maxDisplayX - defaultSideSize
    private val maxSafeY = maxDisplayY - defaultSideSize
    private val maxSize = maxDisplayX to maxDisplayY
    private val radius = defaultSideSize / 2

    /**
     * Should be less than [defaultSideSize]
     */
    private val nearBorder = 50F

    /**
     * Should be larger than [maxSafeX] - [nearBorder]
     */
    private val nearBottomBorder = maxDisplayY - nearBorder

    /**
     * Should be larger than [maxSafeX] - [nearBorder]
     */
    private val nearRightBorder = maxDisplayX - nearBorder

    private val randomY = Random.nextInt(defaultSideSize, maxSafeY).toFloat()
    private val randomX = Random.nextInt(defaultSideSize, maxSafeX).toFloat()

    @Test
    fun nearestPositionAtLeftBorder() {
        val y = randomY
        val clickedPos = PositionAndSize(nearBorder, y, defaultSideSize)

        val result = clickedPos.findNearestPositionForMove(maxSize)

        assertEquals(result.first, 0F)
        assertEquals(result.second, y - radius)
    }

    @Test
    fun nearestPositionAtRightBorder() {
        val y = randomY
        val clickedPos = PositionAndSize(nearRightBorder, y, defaultSideSize)

        val result = clickedPos.findNearestPositionForMove(maxSize)

        assertEquals(result.first, maxSafeX.toFloat())
        assertEquals(result.second, y - radius)
    }

    @Test
    fun nearestPositionAtTopBorder() {
        val x = randomX
        val clickedPos = PositionAndSize(x, nearBorder, defaultSideSize)

        val result = clickedPos.findNearestPositionForMove(maxSize)

        assertEquals(result.first, x - radius)
        assertEquals(result.second, 0F)
    }

    @Test
    fun nearestPositionAtBottomBorder() {
        val x = randomX
        val clickedPos = PositionAndSize(x, nearBottomBorder, defaultSideSize)

        val result = clickedPos.findNearestPositionForMove(maxSize)

        assertEquals(result.first, x - radius)
        assertEquals(result.second, maxSafeY.toFloat())
    }
}