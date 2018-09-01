package com.example.ddvoice.action

import android.content.Context
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.os.Build
import com.example.ddvoice.gApplicationContext


/**
 * Created by Lyn on 18-8-23.
 */
//@SuppressLint("NewApi")
fun trunOnFlash() {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager = gApplicationContext?.getSystemService(Context.CAMERA_SERVICE) as
                    CameraManager
            if (manager != null) {
                manager!!.setTorchMode("0", true)
            }
        } else {
            camera = Camera.open()
            val parameters = camera?.getParameters()
            parameters?.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
            camera?.setParameters(parameters)
            camera?.startPreview()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    
}

var camera: Camera? = null

var manager: CameraManager? = null

//@SuppressLint("NewApi")
fun turnOffFlash() {
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        try {
            if (manager == null) {
                return
            }
            manager?.setTorchMode("0", false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
    } else {
        if (camera == null) {
            return
        }
        camera?.stopPreview()
        camera?.release()
    }
}