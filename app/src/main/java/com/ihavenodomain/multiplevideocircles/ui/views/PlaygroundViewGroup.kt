package com.ihavenodomain.multiplevideocircles.ui.views

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.view.children
import com.ihavenodomain.multiplevideocircles.utils.PositionAndSize
import com.ihavenodomain.multiplevideocircles.utils.findNearestPositionForMove
import com.ihavenodomain.multiplevideocircles.utils.isTouchOverStoredView
import com.ihavenodomain.multiplevideocircles.utils.willBeIntersectedByPosition
import java.util.*
import kotlin.collections.HashMap

/**
 * key - Int (View ID)
 *
 * value - Pair (X, Y)
 */
typealias TakenCoordinates = HashMap<Int, Pair<Float, Float>>

class PlaygroundViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(
    context,
    attrs,
    defStyleAttr
), View.OnTouchListener {
    private val takenCoordinates = TakenCoordinates()

    init {
        setOnTouchListener(this)
    }

    fun addViewAtRandomPosition(view: VideoItemView) {
        val maxWidth = this.width - view.defaultWH
        val maxHeight = this.height - view.defaultWH

        val r = Random()
        var intersects = true

        while (intersects) {
            val xPos = r.nextInt(maxWidth).toFloat()
            val yPos = r.nextInt(maxHeight).toFloat()

            view.x = xPos
            view.y = yPos

            intersects = false

            for (item in takenCoordinates) {
                val pos1 = PositionAndSize(view.x, view.y, view.defaultWH)
                val pos2 = PositionAndSize(item.value.first, item.value.second, view.defaultWH)

                if (pos1.willBeIntersectedByPosition(pos2)) {
                    intersects = true
                    break
                }
            }
        }

        // We should not save camera-moving view coordinates
        if (!view.isForCamera) {
            takenCoordinates[view.id] = view.x to view.y
        }
        addView(view)
    }

    /**
     * @param timesByIds videos time positions
     */
    fun setAllVideoTimesAndPlay(timesByIds: SparseArray<Int>) {
        // We trust that all children of this view are VideoItemViews.
        // Otherwise we can use findViewById
        this.children.forEach { view ->
            if (view is VideoItemView) {
                val videoPosition = timesByIds.get(view.id, 0)
                view.forceShowVideoFromPosition(videoPosition)
            }
        }
    }

    fun getAllVideoPositions(): SparseArray<Int> {
        val positionsByIds: SparseArray<Int> = SparseArray()
        this.children.forEach { view ->
            if (view is VideoItemView) {
                positionsByIds.put(view.id, view.getVideoPosition())
            }
        }
        return positionsByIds
    }

    fun notifyCameraPermissionGranted() {
        this.children.forEach { view ->
            if (view is VideoItemView) {
                view.notifyCameraPermissionGranted()
            }
        }
    }

    /**
     * Find a [VideoItemView] child that plays camera preview
     */
    private fun findCameraView(): VideoItemView? {
        for (i in this.children.count() - 1 downTo 0) {
            val view = this.children.elementAt(i)
            if (view is VideoItemView) {
                if (view.isForCamera) return view
            }
        }
        return null
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val cameraView = findCameraView() ?: return true

            val posAndSize = PositionAndSize(event.x, event.y, cameraView.width)

            if (takenCoordinates.isTouchOverStoredView(posAndSize)) return true

            val newPos = posAndSize.findNearestPositionForMove(this.width to this.height)

            val startX = cameraView.x
            val startY = cameraView.y
            val endX = newPos.first
            val endY = newPos.second

            val pvhX = PropertyValuesHolder.ofFloat("x", startX, endX)
            val pvhY = PropertyValuesHolder.ofFloat("y", startY, endY)

            ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)
                .apply {
                    addUpdateListener { valueAnimator ->
                        cameraView.x = (valueAnimator.getAnimatedValue("x") as Float)
                        cameraView.y = (valueAnimator.getAnimatedValue("y") as Float)
                    }

                    duration = 100

                    start()
                }
        }
        return true
    }
}