package com.iousoft.face.recognition.ui.screen

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.iousoft.face.recognition.ui.component.CameraPreview
import com.iousoft.face.recognition.ui.component.RequestCameraPermission


@Composable
fun CameraScreen() {
    var showCamera by remember { mutableStateOf(false) }
    var faceBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(faceBitmap)
        .crossfade(true)
        .build()

    var hasCameraPermission by remember { mutableStateOf(false) }

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
            // 얼굴 인식 완료 -> 결과 이미지 표시
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AsyncImage(
                            model = imageRequest,
                            contentDescription = "Detected Face",
                            contentScale = ContentScale.Fit, // 또는 .Inside
                            modifier = Modifier.size(240.dp)
                        )
                        Button(
                            onClick = {
                                faceBitmap = null
                                showCamera = false
                            }) {
                            Text("다시 시도")
                        }

                    }

                }

            }
        }

        showCamera -> {
            // 카메라 프리뷰 실행
            CameraPreview(
                onFaceDetected = { bitmap ->
                    faceBitmap = bitmap
                    // viewModel.onFaceCaptured(bitmap) // 필요 시 사용
                }
            )
        }

        else -> {
            // 초기 버튼 화면
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = { showCamera = true }) {
                    Text("얼굴 인식 시작")
                }
            }

        }
    }
}