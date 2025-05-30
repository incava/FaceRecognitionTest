package com.iousoft.face.recognition.ui.component

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalMirrorMode
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.MirrorMode.MIRROR_MODE_OFF
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.getMainExecutor
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.iousoft.face.recognition.analyzer.FaceAnalyzer

@OptIn(ExperimentalMirrorMode::class)
@Composable
fun CameraPreview(
    onFaceDetected: (Bitmap) -> Unit,
    isStopPreview: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    LaunchedEffect(previewView) {
        previewView.scaleType = PreviewView.ScaleType.FIT_CENTER
    }

    AndroidView(factory = { previewView }) { view ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            if (!isStopPreview) {
                val preview = Preview.Builder()
                    .setMirrorMode(MIRROR_MODE_OFF)
                    .build().also {
                        it.surfaceProvider = view.surfaceProvider
                    }

                val resolutionSelector = ResolutionSelector.Builder()
                    .setAspectRatioStrategy(
                        AspectRatioStrategy(
                            AspectRatio.RATIO_4_3,
                            AspectRatioStrategy.FALLBACK_RULE_AUTO
                        )
                    )
                    .build()

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setResolutionSelector(resolutionSelector)
                    .build()
                    .also {
                        it.setAnalyzer(getMainExecutor(context), FaceAnalyzer(context) { bitmap ->
                            onFaceDetected(bitmap)
                        })
                    }

                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageAnalyzer
                )
            } else {
                cameraProvider?.unbindAll()
            }

        }, getMainExecutor(context))
    }
}