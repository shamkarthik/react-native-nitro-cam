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
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.margelo.nitro.nitrocam.FlashMode
import java.io.File
import java.io.FileOutputStream

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
    private lateinit var backgroundThread: HandlerThread
    private lateinit var backgroundHandler: Handler
    private var imageReader: ImageReader? = null
    private var flashMode: FlashMode = FlashMode.AUTO
    private var photoPath: String = ""

    var isFrontCamera: Boolean = false
        private set

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            if (hasCameraPermission()) {
                initializeCamera()
            } else {
                requestCameraPermission()
            }
        }
        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture) = true
        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
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
            ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.CAMERA), 100)
        }
    }

    private fun initializeCamera() {
        startBackgroundThread()
        cameraId = getCameraId(isFrontCamera)
        if (cameraId.isEmpty()) return
        try {
            if (!hasCameraPermission()) return
            cameraManager.openCamera(cameraId, stateCallback, backgroundHandler)
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Error accessing camera", e)
        }
    }

    private fun getCameraId(isFront: Boolean): String {
        return try {
            cameraManager.cameraIdList.firstOrNull { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                facing == if (isFront) CameraCharacteristics.LENS_FACING_FRONT else CameraCharacteristics.LENS_FACING_BACK
            } ?: ""
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Error getting camera ID", e)
            ""
        }
    }

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            setupImageReader()
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

    private fun setupImageReader() {
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        val largest = map?.getOutputSizes(ImageFormat.JPEG)?.maxByOrNull { it.width * it.height }
        largest?.let {
            imageReader = ImageReader.newInstance(it.width, it.height, ImageFormat.JPEG, 1)
        }
    }

    private fun createCameraPreviewSession() {
        try {
            val texture = textureView.surfaceTexture ?: return
            texture.setDefaultBufferSize(textureView.width, textureView.height)
            val surface = Surface(texture)

            previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            previewRequestBuilder.addTarget(surface)

            cameraDevice?.createCaptureSession(
                listOf(surface, imageReader?.surface).filterNotNull(),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        if (cameraDevice == null) return
                        captureSession = session
                        previewRequestBuilder.set(
                            CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                        )
                        setFlashMode(flashMode)
                        captureSession?.setRepeatingRequest(
                            previewRequestBuilder.build(),
                            null,
                            backgroundHandler
                        )
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e(TAG, "Preview session configuration failed")
                    }
                },
                backgroundHandler
            )
        } catch (e: CameraAccessException) {
            Log.e(TAG, "createCameraPreviewSession error", e)
        }
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground").also { it.start() }
        backgroundHandler = Handler(backgroundThread.looper)
    }

    private fun stopBackgroundThread() {
        backgroundThread.quitSafely()
        backgroundThread.join()
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
        imageReader?.close()
        stopBackgroundThread()
    }

    fun setCameraType(type: Int) {
        val shouldUseFront = type == 1
        if (shouldUseFront != isFrontCamera) {
            isFrontCamera = shouldUseFront
            restartCamera()
        }
    }

    fun switchCamera() {
        isFrontCamera = !isFrontCamera
        restartCamera()
    }

    private fun restartCamera() {
        captureSession?.close()
        cameraDevice?.close()
        imageReader?.close()
        initializeCamera()
    }

    fun setFlashMode(mode: FlashMode) {
        flashMode = mode
        if (!::previewRequestBuilder.isInitialized) return
        when (mode) {
            FlashMode.AUTO -> previewRequestBuilder.set(
                CaptureRequest.CONTROL_AE_MODE,
                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
            )
            FlashMode.ON -> previewRequestBuilder.set(
                CaptureRequest.FLASH_MODE,
                CameraMetadata.FLASH_MODE_SINGLE
            )
            FlashMode.OFF -> previewRequestBuilder.set(
                CaptureRequest.CONTROL_AE_MODE,
                CaptureRequest.CONTROL_AE_MODE_ON
            )
        }
        captureSession?.setRepeatingRequest(previewRequestBuilder.build(), null, backgroundHandler)
    }

    fun setZoomLevel(level: Double) {
        Log.d(TAG, "setZoomLevel: $level $cameraId")
        if (cameraId.isEmpty()) {
            Log.e(TAG, "Invalid cameraId, cannot set zoom")
            return
        }
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        val maxZoom = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM) ?: 1f
        val activeArraySize = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE) ?: return

        val zoomFactor = level.toFloat().coerceIn(1f, maxZoom)
        val cropW = (activeArraySize.width() / zoomFactor).toInt()
        val cropH = (activeArraySize.height() / zoomFactor).toInt()
        val cropX = (activeArraySize.width() - cropW) / 2
        val cropY = (activeArraySize.height() - cropH) / 2

        val zoomRect = Rect(cropX, cropY, cropX + cropW, cropY + cropH)
        previewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoomRect)
        captureSession?.setRepeatingRequest(previewRequestBuilder.build(), null, backgroundHandler)
    }

    @SuppressLint("MissingPermission")
    fun takePhoto(): String {
        val reader = imageReader ?: return ""
        val file = getOutputMediaFileCache() ?: return ""
        photoPath = file.absolutePath

        reader.setOnImageAvailableListener({
            val image = it.acquireLatestImage() ?: return@setOnImageAvailableListener
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            FileOutputStream(file).use { output ->
                output.write(bytes)
            }
            image.close()
        }, backgroundHandler)

        try {
            val captureBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)?.apply {
                addTarget(reader.surface)
                set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
                set(CaptureRequest.JPEG_ORIENTATION, 90)
            }

            captureSession?.capture(captureBuilder!!.build(), object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
                    createCameraPreviewSession()
                }
            }, backgroundHandler)
        } catch (e: CameraAccessException) {
            Log.e(TAG, "takePhoto error", e)
        }

        return photoPath
    }

    private fun getOutputMediaFileCache(): File? {
        val dir = context.cacheDir
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "IMG_${System.currentTimeMillis()}.jpg")
    }

    companion object {
        private const val TAG = "CameraView"

        private fun Context.getActivity(): Activity? {
            var ctx = this
            while (ctx is ContextWrapper) {
                if (ctx is Activity) return ctx
                ctx = ctx.baseContext
            }
            return null
        }
    }
}
