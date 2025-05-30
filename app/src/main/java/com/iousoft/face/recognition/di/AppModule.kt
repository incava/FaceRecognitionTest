package com.iousoft.face.recognition.di

import android.content.Context
import com.iousoft.face.recognition.repository.SegmentationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 *  DI 주입을 위한 모듈
 */
//@Module
//@InstallIn(ActivityComponent::class)
//object AppModule {
//
//    @Binds
//    fun provideSegmentationRepository(context: Context): SegmentationRepository {
//        return SegmentationRepository(context)
//    }
//
//}