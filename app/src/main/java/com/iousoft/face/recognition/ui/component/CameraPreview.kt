package com.iousoft.face.recognition.ui.component

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.getMainExecutor
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.iousoft.face.recognition.analyzer.FaceAnalyzer
@Composable
fun CameraPreview(
    onFaceDetected: (Bitmap) -> Unit // ✅ 일반 함수로 변경
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    AndroidView(factory = { previewView }) { view ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(view.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(getMainExecutor(context), FaceAnalyzer(context) { bitmap ->
                    onFaceDetected(bitmap)
                })
            }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, imageAnalyzer
            )

        }, getMainExecutor(context))
    }
}