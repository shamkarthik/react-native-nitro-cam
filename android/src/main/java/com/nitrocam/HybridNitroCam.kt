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
import com.margelo.nitro.nitrocam.FlashMode
import com.margelo.nitro.nitrocam.HybridNitroCamSpec

@Keep
@DoNotStrip
class HybridNitroCam(context: ThemedReactContext) : HybridNitroCamSpec() {

    private val cameraView: CameraView = CameraView(context)
    override val view = cameraView

    // Property: isRed
    private var _isRed = false
    override var isRed: Boolean
        get() = _isRed
        set(value) {
            _isRed = value
            // Update view if necessary.
        }

    // Property: isFrontCamera (delegate to CameraView)
    override var isFrontCamera: Boolean
        get() = cameraView.isFrontCamera
        set(value) {
            if (value != cameraView.isFrontCamera) {
                cameraView.setCameraType(if (value) 1 else 0)
            }
        }

    // Property: flashMode with default value AUTO.
    private var _flash: FlashMode = FlashMode.AUTO
    override var flash: FlashMode
        get() = _flash
        set(value) {
            if (value != _flash) {
                _flash = value
                cameraView.setFlashMode(value)
            }
        }

    // Property: zoomLevel with default value 1.0 (no zoom).
    private var _zoom: Double = 1.0
    override var zoom: Double
        get() = _zoom
        set(value) {
            if (value != _zoom) {
                _zoom = value
                cameraView.setZoomLevel(value)
            }
        }

    // Exposed method to switch cameras.
    override fun switchCamera() {
        cameraView.switchCamera()
    }

    // Exposed method to set flash mode.
    override fun setFlashMode(mode: FlashMode) {
        cameraView.setFlashMode(mode)
    }

    // Exposed method to set zoom level.
    override fun setZoomLevel(level: Double) {
        cameraView.setZoomLevel(level)
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

    private var cameraId: String = ""
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private lateinit var previewRequestBuilder: CaptureRequest.Builder
    private lateinit var previewRequest: CaptureRequest
    private lateinit var backgroundThread: HandlerThread
    private lateinit var backgroundHandler: Handler

    // Indicates whether the front camera is active.
    var isFrontCamera: Boolean = false
        private set

    // Lazy-initialized surface texture listener ensures initialization before use.
    private val surfaceTextureListener: TextureView.SurfaceTextureListener by lazy {
        object : TextureView.SurfaceTextureListener {
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

    init {
        textureView.surfaceTextureListener = surfaceTextureListener
        addView(textureView)
    }

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
        cameraId = getCameraId(isFrontCamera)
        if (cameraId.isNotEmpty()) {
            try {
                if (!hasCameraPermission()) return
                cameraManager.openCamera(cameraId, stateCallback, backgroundHandler)
            } catch (e: CameraAccessException) {
                Log.e(TAG, "Error accessing camera", e)
            }
        }
    }

    private fun getCameraId(isFront: Boolean): String {
        return try {
            cameraManager.cameraIdList.firstOrNull { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (isFront) {
                    facing == CameraCharacteristics.LENS_FACING_FRONT
                } else {
                    facing == CameraCharacteristics.LENS_FACING_BACK
                }
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
                        Log.e(TAG, "Failed to configure camera preview session")
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
            textureView.surfaceTextureListener = surfaceTextureListener
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        captureSession?.close()
        cameraDevice?.close()
        stopBackgroundThread()
    }

    /**
     * Sets the camera type: 0 for back, 1 for front.
     */
    fun setCameraType(type: Int) {
        val shouldUseFront = type == 1
        if (shouldUseFront != isFrontCamera) {
            isFrontCamera = shouldUseFront
            restartCamera()
        }
    }

    /**
     * Switches between front and back cameras.
     */
    fun switchCamera() {
        isFrontCamera = !isFrontCamera
        restartCamera()
    }

    /**
     * Restarts the camera preview.
     */
    private fun restartCamera() {
        captureSession?.close()
        captureSession = null
        cameraDevice?.close()
        cameraDevice = null
        initializeCamera()
    }

    /**
     * Sets the flash mode by updating the preview request.
     */
    fun setFlashMode(mode: FlashMode) {
        if (!::previewRequestBuilder.isInitialized) return
        when (mode) {
            FlashMode.AUTO -> previewRequestBuilder.set(
                CaptureRequest.CONTROL_AE_MODE,
                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
            )
            FlashMode.ON -> previewRequestBuilder.set(
                CaptureRequest.FLASH_MODE,
                CaptureRequest.FLASH_MODE_SINGLE
            )
            FlashMode.OFF -> previewRequestBuilder.set(
                CaptureRequest.CONTROL_AE_MODE,
                CaptureRequest.CONTROL_AE_MODE_ON
            )
        }
        try {
            captureSession?.setRepeatingRequest(previewRequestBuilder.build(), null, backgroundHandler)
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Error setting flash mode", e)
        }
    }

    /**
     * Sets the zoom level.
     * (This is a placeholder; in production, compute a proper crop region.)
     */
    fun setZoomLevel(level: Double) {
        Log.d(TAG, "Setting zoom level: $level")
        // TODO: Compute and apply zoom crop region based on sensor capabilities.
    }

    companion object {
        private const val TAG = "CameraView"
        private const val REQUEST_CAMERA_PERMISSION = 100

        private fun Context.getActivity(): Activity? {
            var currentContext = this
            while (currentContext is ContextWrapper) {
                if (currentContext is Activity) return currentContext
                currentContext = currentContext.baseContext
            }
            return null
        }
    }
}
