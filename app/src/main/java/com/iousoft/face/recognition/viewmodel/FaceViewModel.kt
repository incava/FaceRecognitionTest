package com.iousoft.face.recognition.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iousoft.face.recognition.repository.SegmentationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceViewModel @Inject constructor(
    private val segmentationRepository: SegmentationRepository
) : ViewModel() {

    private val _processedFaceBitmap = mutableStateOf<Bitmap?>(null)
    val processedFaceBitmap: State<Bitmap?> = _processedFaceBitmap

    fun processFaceBitmap(original: Bitmap, isRemoveBackground: Boolean) {
        viewModelScope.launch {
            if (isRemoveBackground && _processedFaceBitmap.value == null) {
                val segmented = segmentationRepository.removeBackground(original)
                _processedFaceBitmap.value = segmented
            } else {
                _processedFaceBitmap.value = original
            }

        }
    }

    fun reset() {
        _processedFaceBitmap.value = null
    }
}