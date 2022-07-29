package com.gulshansutey.composecamera.ui.screen

import android.content.res.Configuration
import android.graphics.Rect
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mlkit.vision.face.Face
import com.gulshansutey.composecamera.camera.GraphicOverlay
import com.gulshansutey.composecamera.camera.PreviewSize
import com.gulshansutey.composecamera.camera.analyzer.FaceDetectionAnalyzer
import com.gulshansutey.composecamera.camera.analyzer.OnFaceDetectedListener
import com.gulshansutey.composecamera.camera.getCameraProvider
import com.gulshansutey.composecamera.camera.rememberCameraManager

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    lensFacing: Int = CameraSelector.LENS_FACING_BACK
) {
    val context = LocalContext.current
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    LaunchedEffect(context) {
        cameraProvider = context.getCameraProvider()
    }

    var faceList by remember {
        mutableStateOf(listOf<Face>())
    }

    var faceImageRect by remember {
        mutableStateOf(Rect())
    }

    val previewView = remember {
        PreviewView(context)
    }

    cameraProvider?.let {

        val cameraManager = rememberCameraManager(previewView = previewView, cameraProvider = it)
        LaunchedEffect(it) {
            val imageAnalyser = FaceDetectionAnalyzer(object : OnFaceDetectedListener {
                override fun onSuccess(faces: List<Face>, rect: Rect) {
                    faceImageRect = if (faces.isEmpty()) {
                        Rect()
                    } else {
                        faceList = faces
                        rect
                    }
                }

                override fun onFailure(e: Exception) {
                    e.printStackTrace()
                }

            })
            cameraManager.setAnalyzer(imageAnalyser)
            cameraManager.startCamera()
        }

        LaunchedEffect(lensFacing) {
            cameraManager.switchCamera(lensFacing)
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            AndroidView(
                modifier = modifier.fillMaxSize(),
                factory = {
                    previewView
                }
            )

            GraphicOverlay(
                previewSize = PreviewSize(
                    previewView.width.toFloat(),
                    previewView.height.toFloat()
                ),
                faces = faceList,
                imageRect = faceImageRect,
                isLandScapeMode = context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE,
                isFrontCamera = lensFacing == CameraSelector.LENS_FACING_FRONT
            )

        }
    }


}

