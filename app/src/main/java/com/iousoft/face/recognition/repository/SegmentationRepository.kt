package com.iousoft.face.recognition.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import androidx.core.graphics.scale
import androidx.core.graphics.set
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SegmentationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val options by lazy {
        SelfieSegmenterOptions.Builder()
            .setDetectorMode(SelfieSegmenterOptions.STREAM_MODE)
            .enableRawSizeMask()
            .build()
    }

    private val segmenter by lazy {
        Segmentation.getClient(options)
    }


    suspend fun removeBackground(originalBitmap: Bitmap): Bitmap =
        withContext(Dispatchers.Default) {
            val image = InputImage.fromBitmap(originalBitmap, 0)

            val result = segmenter.process(image).await()

            val mask = result.buffer
            val width = result.width
            val height = result.height

            val newBitmap = createBitmap(width, height)
            val originalScaled = originalBitmap.scale(width, height)

            mask.rewind()
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val foregroundConfidence = mask.float
                    val pixel = if (foregroundConfidence > 0.6) {
                        originalScaled[x, y]
                    } else {
                        Color.WHITE
                    }
                    newBitmap[x, y] = pixel
                }
            }

            newBitmap
        }
}