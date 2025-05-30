package com.iousoft.face.recognition.ui.component

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 *  카메라 권한 요청
 */

@Composable
fun RequestCameraPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit = {}
) {
    val context = LocalContext.current
    var isPermissionRequested by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) onPermissionGranted() else onPermissionDenied()
    }

    LaunchedEffect(Unit) {
        val permissionCheck = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted()
        } else if (!isPermissionRequested) {
            isPermissionRequested = true
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}