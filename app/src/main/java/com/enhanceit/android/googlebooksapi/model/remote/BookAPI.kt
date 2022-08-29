package com.enhanceit.android.googlebooksapi.model.remote

import com.enhanceit.android.googlebooksapi.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class BookAPI {
    val api: BookService by lazy{
        initRetrofit()
    }

    private fun initRetrofit(): BookService{
        return Retrofit.Builder()
            .client(getOkHttpClient())
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(BookService::class.java)
    }

    private fun getOkHttpClient(): OkHttpClient {

        val okHttpLogging = HttpLoggingInterceptor()
            if(BuildConfig.DEBUG)
                okHttpLogging.level = HttpLoggingInterceptor.Level.BASIC
            else
                okHttpLogging.level = HttpLoggingInterceptor.Level.NONE
        val client = OkHttpClient.Builder()
            .addInterceptor(okHttpLogging)
            .build()
        return client
    }
}