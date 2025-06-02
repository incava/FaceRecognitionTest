package com.iousoft.face.recognition.ui.screen

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.Bitmap
import com.iousoft.face.recognition.ui.component.CameraPreview
import com.iousoft.face.recognition.ui.component.RequestCameraPermission
import com.iousoft.face.recognition.viewmodel.FaceViewModel


@Composable
fun CameraScreen(
    viewModel: FaceViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    var showCamera by remember { mutableStateOf(false) }
    val faceBitmap by viewModel.processedFaceBitmap
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(true) }
    var isStopPreview by remember { mutableStateOf(false) }

    BackHandler(enabled = showCamera || faceBitmap != null) {
        when {
            faceBitmap != null -> {
                viewModel.reset()
                showCamera = false
            }

            showCamera -> {
                showCamera = false
            }

            else -> onBack()
        }
    }

    if (!hasCameraPermission) {
        RequestCameraPermission(
            onPermissionGranted = { hasCameraPermission = true },
            onPermissionDenied = {
                Toast.makeText(context, "권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
            }
        )
        return
    }

    when {
        faceBitmap != null -> {
            FaceResultScreen(
                faceBitmap = faceBitmap!!,
                onRetry = {
                    viewModel.reset()
                    showCamera = false
                },
                onSave = {
                    saveBitmapToGallery(context, faceBitmap!!)
                }
            )
        }

        showCamera -> {
            CameraPreview(
                onFaceDetected = { originalBitmap ->
                    viewModel.processFaceBitmap(originalBitmap, isChecked)
                    Toast.makeText(context, "사진 추적 완료", Toast.LENGTH_SHORT).show()
                },
                isStopPreview = isStopPreview
            )
        }

        else -> {
            CameraStartScreen(
                isChecked = isChecked,
                onStartCamera = { showCamera = true },
                onToggleSwitch = { isChecked = it }
            )
        }
    }
}


fun saveBitmapToGallery(
    context: Context,
    bitmap: Bitmap,
    fileName: String = "face_${System.currentTimeMillis()}.jpg"
) {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FaceRecognition")
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        resolver.openOutputStream(uri)?.use { outStream ->
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, outStream)
        }

        contentValues.clear()
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, contentValues, null, null)

        Toast.makeText(context, "이미지 저장 완료", Toast.LENGTH_SHORT).show()
    } ?: run {
        Toast.makeText(context, "이미지 저장 실패", Toast.LENGTH_SHORT).show()
    }
}