package com.ihavenodomain.multiplevideocircles.ui.views

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.annotation.DrawableRes
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.ihavenodomain.multiplevideocircles.R
import com.ihavenodomain.multiplevideocircles.utils.lifecycleOwner
import timber.log.Timber
import kotlin.math.max


class VideoItemView @JvmOverloads constructor(
    context: Context,
    private val videoSource: Uri? = null,
    videoViewPool: VideoViewsViewPool? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(
    context,
    attrs,
    defStyleAttr
) {

    /**
     * Width / Height
     */
    val defaultWH = context.resources.getDimension(R.dimen.default_video_item_side).toInt()

    /**
     * To make view circled
     */
    private val defaultRadius = context.resources.getDimension(R.dimen.default_video_radius)

    /**
     * If there's no video source URI, then the view should show an image from camera
     */
    val isForCamera = videoSource == null

    private var videoContentPreview: View

    private var preview: Preview? = null
    private var camera: Camera? = null
    private var hasPermission: Boolean = false

    init {
        id = View.generateViewId()

        /**
         * Border around view
         */
        @DrawableRes
        val defaultForegroundDrawableRes = if (isForCamera) {
            R.drawable.background_camera_overlay_circle
        } else {
            R.drawable.background_video_overlay_circle
        }

        val layoutParams = LayoutParams(defaultWH, defaultWH)
        this.layoutParams = layoutParams
        this.radius = defaultRadius
        this.setCardBackgroundColor(ContextCompat.getColor(this.context, android.R.color.black))
        this.cardElevation = 0F
        this.foreground =
            ContextCompat.getDrawable(context, defaultForegroundDrawableRes)

        // region Video/camera view child
        val layoutParamsForPreview = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT,
            Gravity.CENTER
        )
        videoContentPreview = if (isForCamera) {
            PreviewView(context).apply {
                preferredImplementationMode = PreviewView.ImplementationMode.TEXTURE_VIEW
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        } else {
            videoViewPool?.getOrCreateVideoView() ?: VideoView(context)
        }

        videoContentPreview.layoutParams = layoutParamsForPreview
        this.addView(videoContentPreview)
        // endregion

        startPlaying()
    }

    fun notifyCameraPermissionGranted() {
        hasPermission = true
        if (!isForCamera) return
        playCameraVideo()
    }

    fun forceShowVideoFromPosition(position: Int) {
        if (isForCamera) return
        playLocalUriVideo(position)
    }

    fun getVideoPosition() : Int {
        if (isForCamera) return 0
        val videoView = videoContentPreview as VideoView
        return videoView.currentPosition
    }

    private fun startPlaying() {
        if (!isForCamera) {
            playLocalUriVideo()
        }
        // camera preview will start after permission checks passed
    }

    private fun playCameraVideo() {
        if (!hasPermission) return

        if (videoContentPreview !is PreviewView) return

        val videoView = videoContentPreview as PreviewView

        startCamera(videoView)
    }

    private fun startCamera(viewFinder: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this.context)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            preview = Preview.Builder()
                .build()

            // Select front camera
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this.context.lifecycleOwner()!!, cameraSelector, preview
                )
                preview?.setSurfaceProvider(viewFinder.createSurfaceProvider())
            } catch (exc: Exception) {
                Timber.e("Use case binding failed ${exc.message}")
            }

        }, ContextCompat.getMainExecutor(this.context))
    }

    private fun playLocalUriVideo(position: Int = 0) {
        if (videoContentPreview !is VideoView) return

        val videoView = videoContentPreview as VideoView

        videoView.setOnCompletionListener(getOnCompletionListener(videoView))
        videoView.setOnPreparedListener(getOnPreparedListener(videoView))
        videoView.setOnErrorListener { mp, what, extra ->
            Timber.e("error trying playing video: $mp \n $what, $extra")
            true
        }
        videoView.setVideoURI(videoSource)
        videoView.seekTo(position)
        videoView.start()
    }

    /**
     * Used to scale local video
     * @see VideoView
     * @see MediaPlayer.OnPreparedListener
     */
    private fun getOnPreparedListener(videoView: VideoView) = MediaPlayer.OnPreparedListener { mp ->
        // Get video's width and height
        val videoWidth = mp.videoWidth
        val videoHeight = mp.videoHeight

        // Get VideoView's current width and height
        val videoViewWidth: Int = videoView.width
        val videoViewHeight: Int = videoView.height
        val xScale = videoViewWidth.toFloat() / videoWidth
        val yScale = videoViewHeight.toFloat() / videoHeight

        // For Center Crop we use the Math.max to calculate the scale
        val scale = max(xScale, yScale)
        val scaledWidth = scale * videoWidth
        val scaledHeight = scale * videoHeight

        // Set the new size for the VideoView based on the dimensions of the video
        val layoutParams: ViewGroup.LayoutParams = videoView.layoutParams
        layoutParams.width = scaledWidth.toInt()
        layoutParams.height = scaledHeight.toInt()
        videoView.layoutParams = layoutParams
    }

    /**
     * Used to replay local video after it's finish
     */
    private fun getOnCompletionListener(videoView: VideoView) = MediaPlayer.OnCompletionListener { mp ->
        mp.reset()
        videoView.setVideoURI(videoSource)
        videoView.start();
    }
}