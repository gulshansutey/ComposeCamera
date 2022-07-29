package com.gulshansutey.composecamera

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@ExperimentalPermissionsApi
@Composable
fun RequireCameraPermission(
    rationale: String = "Please grant camera permission",
    content: @Composable () -> Unit = { }
) {
    val permissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    when (permissionState.status) {
        is PermissionStatus.Denied -> {
            if ((permissionState.status as PermissionStatus.Denied).shouldShowRationale) {
                Rationale(
                    text = rationale,
                    onRequestPermission = { permissionState.launchPermissionRequest() }
                )
            } else {
                LaunchedEffect(Unit) {
                    permissionState.launchPermissionRequest()
                }
            }
        }

        PermissionStatus.Granted -> {
            content()
        }
    }
}

@Composable
private fun Rationale(
    text: String,
    onRequestPermission: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /*no-op*/ },
        title = {
            Text(text = "Permission request")
        },
        text = {
            Text(text)
        },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text("Ok")
            }
        }
    )
}
