package com.gulshansutey.composecamera

import android.graphics.Rect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceDetectionAnalyzer(private val listener : OnFaceDetectedListener) : BaseImageAnalyzer() {

    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
        .build()

    private val faceDetector = FaceDetection.getClient(options)

    override fun onAnalyze(image: InputImage) = faceDetector.process(image)

    override fun onSuccess(results: List<Face>, rect: Rect) {
        listener.onSuccess(results, rect)
    }

    override fun onFailure(e: Exception) {
        listener.onFailure(e)
    }

}

interface OnFaceDetectedListener {
    fun onSuccess(faces: List<Face>, rect: Rect)
    fun onFailure(e: Exception)
}
