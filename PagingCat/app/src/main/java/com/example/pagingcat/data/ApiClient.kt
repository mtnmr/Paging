package com.example.pagingcat.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {

    companion object {
        private const val BASE_URL = "https://api.thecatapi.com/v1/"
        private const val TIME_OUT_MILLIS = 2000L
    }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    fun provideRetrofit():Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideHttpClient())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    private fun provideHttpClient() =
        OkHttpClient.Builder()
            .callTimeout(TIME_OUT_MILLIS, TimeUnit.MILLISECONDS)
            .readTimeout(TIME_OUT_MILLIS, TimeUnit.MILLISECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
}