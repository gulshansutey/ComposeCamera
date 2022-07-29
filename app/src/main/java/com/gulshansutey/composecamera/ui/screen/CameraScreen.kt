package com.gulshansutey.composecamera.ui.screen

import androidx.camera.core.CameraSelector
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gulshansutey.composecamera.R
import com.gulshansutey.composecamera.ui.theme.Camera_Preview_Option_Overlay

@Composable
fun CameraScreen() {

    var switchCamera by remember {
        mutableStateOf(true)
    }

    Box(contentAlignment = Alignment.BottomCenter) {
        CameraPreview(lensFacing = if (switchCamera) CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT)
        CameraScreenOptions {
            switchCamera = !switchCamera
        }
    }

}

@Composable
fun CameraScreenOptions(
    modifier: Modifier = Modifier,
    onCameraSwitchClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(Camera_Preview_Option_Overlay)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_cameraswitch_black_36dp),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(42.dp)
                    .clickable {
                        onCameraSwitchClick()
                    }
            )
        }
    }
}
