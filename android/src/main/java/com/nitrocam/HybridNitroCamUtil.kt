package com.nitrocam

import android.content.Context.CAMERA_SERVICE
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.margelo.nitro.NitroModules
import com.margelo.nitro.nitrocam.CameraType
import com.margelo.nitro.nitrocam.FocalType
import com.margelo.nitro.nitrocam.HybridNitroCamUtilSpec



class HybridNitroCamUtil : HybridNitroCamUtilSpec() {
    private val applicationContext = NitroModules.applicationContext

    @RequiresApi(Build.VERSION_CODES.P)
    override fun getCameraDevices(): Array<CameraType> {
        Log.d("HybridNitroCamUtil", "Starting getCameraDevices")
        val cameraList = mutableListOf<CameraType>()
        val manager = applicationContext?.getSystemService(CAMERA_SERVICE) as? CameraManager
        if (manager == null) {
            Log.e("HybridNitroCamUtil", "CameraManager service not available")
        } else {
            try {
                Log.d("HybridNitroCamUtil", "Listing camera devices")
                for (cameraId in manager.cameraIdList) {
                    Log.d("HybridNitroCamUtil", "Processing logical camera ID: $cameraId")
                    val characteristics = manager.getCameraCharacteristics(cameraId)

                    // Determine base type based on lens facing.
                    val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
                    val baseType = when (lensFacing) {
                        CameraCharacteristics.LENS_FACING_BACK -> "Back"
                        CameraCharacteristics.LENS_FACING_FRONT -> "Front"
                        else -> "External"
                    }

                    // Retrieve focal lengths.
                    val focalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)
                    val focals = mutableListOf<FocalType>()
                    focalLengths?.forEachIndexed { index, focal ->
                        Log.d("HybridNitroCamUtil", "Processing focal length: $focal")
                        val type = when {
                            focal < 2.0 -> "Ultra Wide"
                            focal in 2.0..3.0 -> "Wide"
                            focal in 3.0..5.0 -> "Standard"
                            focal in 5.0..7.0 -> "Telephoto"
                            focal >= 7.0 -> "Super Telephoto"
                            else -> ""
                        }
                        focals.add(FocalType(id = "${cameraId}_focal_$index", name = type, focalLength = focal.toDouble()))
                    }
                    val focalTypeArray = focals.toTypedArray()

                    // Check for macro capability.
                    val minFocusDistance = characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE) ?: 0f
                    val macroTag = if (minFocusDistance < 0.1f) "Macro" else ""

                    // Build a description for the logical camera.
                    var logicalDescription = baseType
                    if (focals.isNotEmpty()) {
                        val focalNames = focals.map { it.name }.distinct().joinToString(separator = ",")
                        logicalDescription += " ($focalNames)"
                    }
                    if (macroTag.isNotEmpty()) {
                        logicalDescription += " ($macroTag)"
                    }

                    // Check if this logical camera has multiple physical cameras.
                    val physicalCameras = characteristics.physicalCameraIds
                    if (physicalCameras.isNotEmpty() && physicalCameras.size > 1) {
                        Log.d("HybridNitroCamUtil", "Logical camera $cameraId has multiple physical cameras: $physicalCameras")
                        for (physicalId in physicalCameras) {
                            try {
                                val physCharacteristics = manager.getCameraCharacteristics(physicalId)
                                val physFocalLengths = physCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)
                                val physFocalTypes = mutableListOf<String>()
                                physFocalLengths?.forEach { physFocal ->
                                    val physType = when {
                                        physFocal < 2.0 -> "Ultra Wide"
                                        physFocal in 2.0..3.0 -> "Wide"
                                        physFocal in 3.0..5.0 -> "Standard"
                                        physFocal in 5.0..7.0 -> "Telephoto"
                                        physFocal >= 7.0 -> "Super Telephoto"
                                        else -> ""
                                    }
                                    physFocalTypes.add(physType)
                                }
                                val physMinFocus = physCharacteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE) ?: 0f
                                val physMacro = if (physMinFocus < 0.1f) "Macro" else ""
                                var physDescription = baseType
                                if (physFocalTypes.isNotEmpty()) {
                                    physDescription += " (${physFocalTypes.distinct().joinToString(",")})"
                                }
                                if (physMacro.isNotEmpty()) {
                                    physDescription += " ($physMacro)"
                                }
                                cameraList.add(CameraType(id = physicalId, placement = physDescription, type = focalTypeArray))
                            } catch (e: Exception) {
                                Log.e("HybridNitroCamUtil", "Error accessing physical camera $physicalId", e)
                            }
                        }
                    } else {
                        // No multiple physical cameras, add the logical camera entry.
                        cameraList.add(CameraType(id = cameraId, placement = logicalDescription, type = focalTypeArray))
                    }
                }
            } catch (e: CameraAccessException) {
                Log.e("HybridNitroCamUtil", "CameraAccessException occurred", e)
                throw RuntimeException("Error accessing camera: ${e.message}")
            }
        }
        return cameraList.toTypedArray()
    }

}
