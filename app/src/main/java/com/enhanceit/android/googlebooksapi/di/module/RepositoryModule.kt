package com.enhanceit.android.googlebooksapi.di.module

import com.enhanceit.android.googlebooksapi.model.Repository
import com.enhanceit.android.googlebooksapi.model.RepositoryImpl
import com.enhanceit.android.googlebooksapi.model.remote.BookAPI
import com.enhanceit.android.googlebooksapi.model.remote.BookService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@InstallIn(ViewModelComponent::class)
@Module
class RepositoryModule {
    @Provides
    fun provideRepository(network: BookAPI):Repository =
        RepositoryImpl(network)
}