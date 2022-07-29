package com.gulshansutey.composecamera

import android.content.Context
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CameraManager(
    private val cameraProvider: ProcessCameraProvider,
    private val lifecycleOwner: LifecycleOwner,
    private val finderView: PreviewView,
    private var cameraFace: Int = CameraSelector.LENS_FACING_BACK
) : ICameraManager {
    private var camera: Camera? = null
    private var preview: Preview? = null
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private lateinit var imageAnalyzer: ImageAnalysis

    override fun setAnalyzer(analyzer: ImageAnalysis.Analyzer) {
        imageAnalyzer = ImageAnalysis
            .Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, analyzer)
            }
    }

    override fun startCamera() {
        preview = Preview.Builder().build()
        try {
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.Builder()
                    .requireLensFacing(cameraFace)
                    .build(),
                preview,
                imageAnalyzer
            )
            preview?.setSurfaceProvider(finderView.surfaceProvider)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun stopCamera() {
        cameraProvider.unbindAll()
    }

    override fun switchCamera(lensFacing: Int) {
        this.cameraFace = lensFacing
        startCamera()
    }

}

interface ICameraManager {
    fun startCamera()
    fun stopCamera()
    fun switchCamera(lensFacing: Int)
    fun setAnalyzer(analyzer: ImageAnalysis.Analyzer)
}

@Composable
fun rememberCameraManager(
    previewView: PreviewView,
    cameraProvider: ProcessCameraProvider
): ICameraManager {
    println("rememberCameraManager")
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraManager = remember {
        CameraManager(
            cameraProvider,
            lifecycleOwner,
            previewView
        )
    }
    return cameraManager
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener({
            continuation.resume(future.get())
        }, ContextCompat.getMainExecutor(this))
    }
}
