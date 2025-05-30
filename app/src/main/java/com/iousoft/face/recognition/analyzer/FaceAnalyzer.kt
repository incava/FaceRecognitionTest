package com.iousoft.face.recognition.analyzer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark

class FaceAnalyzer(
    private val context: Context,
    private val onFaceDetected: (Bitmap) -> Unit
) : ImageAnalysis.Analyzer {

    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .build()
    )

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        detector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    for (face in faces) {

                        val boundingBox = face.boundingBox
                        val centerX = boundingBox.centerX()
                        val centerY = boundingBox.centerY()

                        // 중앙 영역 (예: 1080x1920 기준, 중앙 +- 15%)
                        val frameWidth = image.width
                        val frameHeight = image.height
                        val centerRegionWidth = frameWidth * 0.3
                        val centerRegionHeight = frameHeight * 0.3

                        val centerLeft = (frameWidth / 2 - centerRegionWidth / 2).toInt()
                        val centerRight = (frameWidth / 2 + centerRegionWidth / 2).toInt()
                        val centerTop = (frameHeight / 2 - centerRegionHeight / 2).toInt()
                        val centerBottom = (frameHeight / 2 + centerRegionHeight / 2).toInt()

                        val isFaceCentered = centerX in centerLeft..centerRight &&
                                centerY in centerTop..centerBottom

                        val hasAllLandmarks =
                                face.getContour(FaceContour.LEFT_EYE) != null &&
                                face.getContour(FaceContour.RIGHT_EYE) != null
                        if (isFaceCentered && hasAllLandmarks) {
                            val bitmap = imageProxy.toBitmap()
                            val rotatedBitmap = rotateBitmap(bitmap, -90f)
                            onFaceDetected(rotatedBitmap)
                            break // 하나만 처리할 경우 break
                        }
                    }
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
    fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }



}