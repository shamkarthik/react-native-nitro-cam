package com.nitrocam

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.widget.FrameLayout
import androidx.annotation.Keep
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.uimanager.ThemedReactContext
import com.margelo.nitro.nitrocam.FlashMode
import com.margelo.nitro.nitrocam.HybridNitroCamSpec
import java.io.File
import java.io.FileOutputStream

@Keep
@DoNotStrip
class HybridNitroCam(context: ThemedReactContext) : HybridNitroCamSpec() {

    private val cameraView: CameraView = CameraView(context)
    override val view = cameraView

    override var isFrontCamera: Boolean
        get() = cameraView.isFrontCamera
        set(value) {
            if (value != cameraView.isFrontCamera) {
                cameraView.setCameraType(if (value) 1 else 0)
            }
        }

    private var _flash: FlashMode = FlashMode.AUTO
    override var flash: FlashMode
        get() = _flash
        set(value) {
            if (value != _flash) {
                _flash = value
                cameraView.setFlashMode(value)
            }
        }

    private var _zoom: Double = 1.0
    override var zoom: Double
        get() = _zoom
        set(value) {
            if (value != _zoom) {
                _zoom = value
                cameraView.setZoomLevel(value)
            }
        }

    override fun switchCamera() {
        cameraView.switchCamera()
    }

    override fun setFlashMode(mode: FlashMode) {
        cameraView.setFlashMode(mode)
    }

    override fun setZoomLevel(level: Double) {
        cameraView.setZoomLevel(level)
    }

    override fun takePhoto(): String {
        return cameraView.takePhoto()
    }
}
