package com.iousoft.face.recognition.analyzer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceAnalyzer(
    private val context: Context,
    private val onFaceDetected: (Bitmap) -> Unit
) : ImageAnalysis.Analyzer {

    private var isFindImage = false

    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setMinFaceSize(0.1f)
            .build()
    )

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)

        detector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    val rotatedBitmap =
                        rotateBitmap(imageProxy.toBitmap(), rotationDegrees.toFloat())
                    for (face in faces) {
                        val boundingBox = face.boundingBox
                        val centerX = boundingBox.centerX()
                        val centerY = boundingBox.centerY()

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

                        if (isFaceCentered && hasAllLandmarks && !isFindImage) {
                            // val faceBitmap = cropFaceByContourOrBoundingBox(face, imageProxy.toBitmap())
                            val resultBitmap =
                                rotateBitmap(imageProxy.toBitmap(), rotationDegrees.toFloat())
                            onFaceDetected(resultBitmap)
                            isFindImage = true
                            break
                        }
                    }
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        if (degrees == 0f) return bitmap
        val matrix = Matrix().apply {
            postRotate(degrees)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun cropFaceByContourOrBoundingBox(face: Face, bitmap: Bitmap): Bitmap {
        val contour = face.getContour(FaceContour.FACE)?.points

        // 얼굴 윤곽선이 존재하면 윤곽선 기준으로 자름
        val faceRect: Rect = if (!contour.isNullOrEmpty()) {
            val xs = contour.map { it.x }
            val ys = contour.map { it.y }

            val left = xs.min().toInt()
            val top = ys.min().toInt()
            val right = xs.max().toInt()
            val bottom = ys.max().toInt()

            // padding 값 설정 (예: 10% 여유)
            val paddingX = ((right - left) * 0.4f).toInt()
            val paddingY = ((bottom - top) * 0.6f).toInt()

            Rect(
                (left - paddingX).coerceAtLeast(0),
                (top - paddingY).coerceAtLeast(0),
                (right + paddingX).coerceAtMost(bitmap.width),
                (bottom + paddingY).coerceAtMost(bitmap.height)
            )
        } else {
            // fallback: boundingBox 사용
            val bounds = face.boundingBox

            val paddingX = (bounds.width() * 0.2f).toInt()
            val paddingY = (bounds.height() * 0.3f).toInt()

            Rect(
                (bounds.left - paddingX).coerceAtLeast(0),
                (bounds.top - paddingY).coerceAtLeast(0),
                (bounds.right + paddingX).coerceAtMost(bitmap.width),
                (bounds.bottom + paddingY).coerceAtMost(bitmap.height)
            )
        }

        // 크롭 영역의 최소 폭/높이 보장
        val width = faceRect.width().coerceAtLeast(1)
        val height = faceRect.height().coerceAtLeast(1)

        val sideInversion = Matrix().apply {
            postScale(1f, -1f)
        }
        val cropBitmap = Bitmap.createBitmap(bitmap, faceRect.left, faceRect.top, width, height)
        return Bitmap.createBitmap(
            cropBitmap,
            0,
            0,
            cropBitmap.getWidth(),
            cropBitmap.getHeight(),
            sideInversion,
            false
        )
    }
}
