package com.example.pagingcat.di

import com.example.pagingcat.data.ApiClient
import com.example.pagingcat.data.CatSearchApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideApiClient(): ApiClient = ApiClient()

    @Provides
    @Singleton
    fun provideCatSearchApi(apiClient: ApiClient):CatSearchApi{
        return apiClient.provideRetrofit().create(CatSearchApi::class.java)
    }
}