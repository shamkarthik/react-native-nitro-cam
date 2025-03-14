package com.nitrocam

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
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
import com.margelo.nitro.nitrocam.HybridNitroCamSpec

@Keep
@DoNotStrip
class HybridNitroCam(context: ThemedReactContext) : HybridNitroCamSpec() {
    private val cameraView: CameraView = CameraView(context)
    override val view = cameraView

    private var _isRed = false
    override var isRed: Boolean
        get() = _isRed
        set(value) {
            _isRed = value
            // Handle the isRed property as needed
        }
}

class CameraView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val textureView: TextureView = TextureView(context)
    private val cameraManager: CameraManager =
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private lateinit var cameraId: String
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private lateinit var previewRequestBuilder: CaptureRequest.Builder
    private lateinit var previewRequest: CaptureRequest
    private lateinit var backgroundThread: HandlerThread
    private lateinit var backgroundHandler: Handler

    init {
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                if (hasCameraPermission()) {
                    initializeCamera()
                } else {
                    requestCameraPermission()
                }
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = true
            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        }
        addView(textureView)
    }

    // Rest of your CameraView implementation...

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        val activity = context.getActivity()
        activity?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }
    }

    private fun initializeCamera() {
        startBackgroundThread()
        cameraId = getCameraId()
        if (cameraId.isNotEmpty()) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                cameraManager.openCamera(cameraId, stateCallback, backgroundHandler)
            } catch (e: CameraAccessException) {
                Log.e(TAG, "Error accessing camera", e)
            }
        }
    }

    private fun getCameraId(): String {
        return try {
            cameraManager.cameraIdList.firstOrNull { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                facing == CameraCharacteristics.LENS_FACING_BACK
            } ?: ""
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Error getting camera ID", e)
            ""
        }
    }

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            createCameraPreviewSession()
        }

        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
            cameraDevice = null
        }

        override fun onError(camera: CameraDevice, error: Int) {
            camera.close()
            cameraDevice = null
        }
    }

    private fun createCameraPreviewSession() {
        try {
            val texture = textureView.surfaceTexture ?: return
            texture.setDefaultBufferSize(textureView.width, textureView.height)
            val surface = Surface(texture)
            previewRequestBuilder =
                cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                    addTarget(surface)
                }
            cameraDevice?.createCaptureSession(
                listOf(surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        if (cameraDevice == null) return
                        captureSession = session
                        try {
                            previewRequestBuilder.set(
                                CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                            )
                            previewRequest = previewRequestBuilder.build()
                            captureSession?.setRepeatingRequest(
                                previewRequest,
                                null,
                                backgroundHandler
                            )
                        } catch (e: CameraAccessException) {
                            Log.e(TAG, "Error starting camera preview", e)
                        }
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e(TAG, "Failed to configure camera")
                    }
                },
                backgroundHandler
            )
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Error creating camera preview session", e)
        }
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground").also { it.start() }
        backgroundHandler = Handler(backgroundThread.looper)
    }

    private fun stopBackgroundThread() {
        backgroundThread.quitSafely()
        try {
            backgroundThread.join()
        } catch (e: InterruptedException) {
            Log.e(TAG, "Error stopping background thread", e)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (textureView.isAvailable) {
            initializeCamera()
        } else {
            textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                    if (hasCameraPermission()) {
                        initializeCamera()
                    } else {
                        requestCameraPermission()
                    }
                }

                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = true
                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        captureSession?.close()
        cameraDevice?.close()
        stopBackgroundThread()
    }

    companion object {
        private const val TAG = "CameraView"
        private const val REQUEST_CAMERA_PERMISSION = 100

        private fun Context.getActivity(): Activity? {
            var context = this
            while (context is ContextWrapper) {
                if (context is Activity) {
                    return context
                }
                context = context.baseContext
            }
            return null
        }
    }
}
