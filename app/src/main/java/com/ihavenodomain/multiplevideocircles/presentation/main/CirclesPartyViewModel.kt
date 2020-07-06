package com.ihavenodomain.multiplevideocircles.presentation.main

import android.content.Context
import android.net.Uri
import android.util.SparseArray
import androidx.core.util.putAll
import androidx.lifecycle.ViewModel
import com.ihavenodomain.multiplevideocircles.R
import com.ihavenodomain.multiplevideocircles.ui.views.VideoItemView
import com.ihavenodomain.multiplevideocircles.ui.views.VideoViewsViewPool

class CirclesPartyViewModel : ViewModel() {
    // We save views Set in ViewModel to be sure that it'll not be lost if an activity'll be recreated.
    private val videoItemViewsSet = mutableSetOf<VideoItemView>()
    // A simple viewPool for VideoViews
    private var videoViewsViewPool: VideoViewsViewPool? = null

    val videoTimePositions: SparseArray<Int> = SparseArray()

    fun getViewsForLayout(context: Context): Set<VideoItemView> {
        if (videoItemViewsSet.size > 0) return videoItemViewsSet
        // NOTE: there could be a problem while trying to play one file in a big number of VideoViews.
        // Probably it depends on disk access speed.
        // Anyway this should not lower a whole app performance.
        val path = "android.resource://" + context.packageName + "/" + R.raw.tears_of_steel_low
        val videoPath = Uri.parse(path)

        videoViewsViewPool = VideoViewsViewPool(context)
        videoViewsViewPool?.let { videoViewsViewPool ->
            // add views with local video
            for (i in 0 until VIDEO_VIEWS_COUNT) {
                videoItemViewsSet.add(VideoItemView(context, videoPath, videoViewsViewPool))
            }
            // add camera view
            videoItemViewsSet.add(VideoItemView(context))
        }

        return videoItemViewsSet
    }

    // Saving videoViews time positions is needed for restoring their state after onPause/onResume stuff.
    fun saveVideosPositions(videoPositions: SparseArray<Int>) {
        // remember all videos positions
        this.videoTimePositions.putAll(videoPositions)
    }

    companion object {
        const val VIDEO_VIEWS_COUNT = 10
    }
}