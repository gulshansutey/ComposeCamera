package com.gulshansutey.composecamera.camera.analyzer

import android.annotation.SuppressLint
import android.graphics.Rect
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face

abstract class BaseImageAnalyzer : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(proxy: ImageProxy) {
        proxy.image?.let { image ->
            onAnalyze(InputImage.fromMediaImage(image, proxy.imageInfo.rotationDegrees))
                .addOnSuccessListener { result ->
                    onSuccess(result, image.cropRect)
                    proxy.close()
                }.addOnFailureListener(::onFailure)
        }
    }

    protected abstract fun onAnalyze(image: InputImage): Task<List<Face>>

    protected abstract fun onSuccess(
        results: List<Face>,
        rect: Rect
    )

    protected abstract fun onFailure(e: Exception)

}
