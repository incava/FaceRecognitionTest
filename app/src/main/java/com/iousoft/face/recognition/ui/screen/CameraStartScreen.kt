package com.iousoft.face.recognition.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CameraStartScreen(
    isChecked: Boolean,
    onStartCamera: () -> Unit,
    onToggleSwitch: (Boolean) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = onStartCamera) {
                Text("얼굴 인식 시작")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "누끼")
                Text(text = if (isChecked) "ON" else "OFF")
                Switch(
                    checked = isChecked,
                    onCheckedChange = onToggleSwitch
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCameraStartScreen() {
    CameraStartScreen(
        isChecked = true,
        onStartCamera = {},
        onToggleSwitch = {}
    )
}