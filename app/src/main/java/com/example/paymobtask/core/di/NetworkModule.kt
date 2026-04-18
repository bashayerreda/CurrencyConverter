package com.example.paymobtask.core.di

import com.example.paymobtask.data.NetworkHelperImpl
import com.example.paymobtask.domain.NetworkHelper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {
    @Binds
    @Singleton
    abstract fun bindNetworkHelper(
        impl: NetworkHelperImpl
    ): NetworkHelper
}