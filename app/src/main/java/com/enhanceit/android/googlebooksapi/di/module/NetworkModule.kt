package com.enhanceit.android.googlebooksapi.di.module

import com.enhanceit.android.googlebooksapi.model.remote.BookAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    @Provides
    fun provideBookAPI() = BookAPI()
}