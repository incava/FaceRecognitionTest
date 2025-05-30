package com.iousoft.face.recognition.di

import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceDetectorOptions.CONTOUR_MODE_NONE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 *  DI 주입을 위한 싱글톤 모듈
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // High-accuracy landmark detection and face classification
    // 얼굴 인식 옵션
    @Provides
    fun provideFaceDetectorOptions(): FaceDetectorOptions =
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE) // 정확도 우선
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL) // 랜드마크 구별
            .setContourMode(CONTOUR_MODE_NONE) // 윤곽선 인식
            .setMinFaceSize(0.1f) // 얼굴크기 인식
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE) // 웃는 표정등 구별 X
            // .enableTracking() // 기본값 false 얼굴 트래킹
            .build()

    // Real-time contour detection
    // 얼굴 인식
    @Provides
    fun provideFaceDetector(
        options: FaceDetectorOptions
    ): FaceDetector = FaceDetection.getClient(options)
}