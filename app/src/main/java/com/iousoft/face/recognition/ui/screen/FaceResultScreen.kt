package com.iousoft.face.recognition.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun FaceResultScreen(
    faceBitmap: Bitmap,
    onRetry: () -> Unit,
    onSave: () -> Unit,
) {
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
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(faceBitmap)
                        .crossfade(true)
                        .build(),
                    contentScale = ContentScale.Fit,
                    contentDescription = null,
                    modifier = Modifier.height(540.dp)
                )
                Button(onClick = onRetry) {
                    Text("다시 시도")
                }
                Button(onClick = onSave) {
                    Text("사진 저장")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFaceResultScreen() {
    // 임시 비트맵 생성
    val dummyBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    FaceResultScreen(
        faceBitmap = dummyBitmap,
        onRetry = {},
        onSave = {}
    )
}