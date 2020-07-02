package com.ihavenodomain.multiplevideocircles.ui.views

import android.content.Context
import android.widget.VideoView


/**
 * Simple VideoViews pool.
 *
 * Can be used for dynamically adding/removing views from View hierarchy
 */
class VideoViewsViewPool(
    private val context: Context,
    private val initialSize: Int = 9
) {
    private val videoViewList = mutableListOf<VideoView>()

    init {
        for (i in 0..initialSize) {
            videoViewList.add(createVideoView())
        }
    }

    fun getOrCreateVideoView() =
        if (videoViewList.isEmpty()) {
            createVideoView()
        } else {
            videoViewList.removeAt(0)
        }

    // This method could be used when we wish to remove a view
    fun recycleVideoView(view: VideoView) {
        videoViewList.add(view)
    }

    private fun createVideoView() = VideoView(context)
}