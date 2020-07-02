package com.ihavenodomain.multiplevideocircles.ui

import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ihavenodomain.multiplevideocircles.R
import com.ihavenodomain.multiplevideocircles.presentation.main.CirclesPartyViewModel
import com.ihavenodomain.multiplevideocircles.utils.CameraPermissionsUtils
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: CirclesPartyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(CirclesPartyViewModel::class.java)

        setDefaultThemeWithDelay()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pgMain.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewModel.getViewsForLayout(this@MainActivity).forEach {
                    pgMain.addViewAtRandomPosition(it)
                }

                pgMain.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        if (CameraPermissionsUtils.checkCameraPermission(this)) {
            pgMain.notifyCameraPermissionGranted()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveVideosPositions(pgMain.getAllVideoPositions())
    }

    override fun onResume() {
        super.onResume()
        pgMain.setAllVideoTimesAndPlay(viewModel.videoTimePositions)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (CameraPermissionsUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults)) {
            pgMain.notifyCameraPermissionGranted()
        }
    }

    /**
     * For splash screen support
     */
    private fun setDefaultThemeWithDelay() {
        try {
            // we need this code just to be sure that user will see our beautiful 'welcome-screen'
            Thread.sleep(1000)
        } catch (ex: InterruptedException) {
            // do nothing
        }

        setTheme(R.style.AppTheme)
    }
}