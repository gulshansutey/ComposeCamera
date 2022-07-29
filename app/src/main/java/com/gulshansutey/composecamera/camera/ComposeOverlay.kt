package com.gulshansutey.composecamera.camera

import android.graphics.Rect
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.face.Face
import kotlin.math.ceil

@Composable
fun GraphicOverlay(
    previewSize: PreviewSize,
    faces: List<Face>,
    imageRect: Rect,
    isLandScapeMode: Boolean = false,
    isFrontCamera: Boolean = false
) {

    if (imageRect.isEmpty) {
        return
    }

    Canvas(
        modifier = Modifier
            .width(previewSize.previewWidth.dp)
            .height(previewSize.previewHeight.dp)

    ) {
        faces.forEach {
            calculateRect(
                imageRect.height().toFloat(),
                imageRect.width().toFloat(),
                previewSize.previewHeight,
                previewSize.previewWidth,
                isLandScapeMode,
                isFrontCamera,
                it.boundingBox
            ).apply {
                drawRect(
                    color = Color.Yellow,
                    topLeft = Offset(left, top),
                    size = Size(width(), height()),
                    style = Stroke(width = 5f)
                )
            }
        }
    }

}

data class PreviewSize(val previewWidth: Float, val previewHeight: Float)

fun calculateRect(
    faceHeight: Float,
    faceWidth: Float,
    overlayHeight: Float,
    overlayWidth: Float,
    isLandScapeMode: Boolean,
    isLensFacingFront: Boolean,
    boundingBoxT: Rect,
): RectF {

    val width = if (isLandScapeMode) {
        faceWidth
    } else {
        faceHeight
    }

    val height = if (isLandScapeMode) {
        faceHeight
    } else {
        faceWidth
    }

    val scaleX = overlayWidth / width
    val scaleY = overlayHeight / height
    val scale = scaleX.coerceAtLeast(scaleY)

    val offsetX = (overlayWidth - ceil(width * scale)) / 2.0f
    val offsetY = (overlayHeight - ceil(height * scale)) / 2.0f

    val mappedBox = RectF().apply {
        left = boundingBoxT.right * scale + offsetX
        top = boundingBoxT.top * scale + offsetY
        right = boundingBoxT.left * scale + offsetX
        bottom = boundingBoxT.bottom * scale + offsetY
    }

    if (isLensFacingFront) {
        val centerX = overlayWidth / 2
        mappedBox.apply {
            left = centerX + (centerX - left)
            right = centerX - (right - centerX)
        }
    }

    return mappedBox
}

@Composable
fun Float.dpValue() = LocalDensity.current.run {
    toDp()
}
