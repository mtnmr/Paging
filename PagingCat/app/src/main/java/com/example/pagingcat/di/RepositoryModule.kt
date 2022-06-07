package com.example.pagingcat.di

import com.example.pagingcat.data.CatRepository
import com.example.pagingcat.data.CatRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindCatRepository(impl: CatRepositoryImpl): CatRepository

}