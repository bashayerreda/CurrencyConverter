package com.example.paymobtask.di

import com.example.paymobtask.fakerepos.FakeCurrencyConversionHistoryRepository
import com.example.paymobtask.domain.repository.CurrencyRepository
import com.example.paymobtask.fakerepos.FakeCurrencyRepository
import com.example.paymobtask.core.di.AppModule
import com.example.paymobtask.domain.repository.CurrencyConversionHistoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import jakarta.inject.Singleton
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestRepositoryModule {
    @Provides
    @Singleton
    fun provideCurrencyRepository(): CurrencyRepository {
        return FakeCurrencyRepository()
    }
    @Provides
    @Singleton
    fun provideCurrencyConversionHistoryRepository(): CurrencyConversionHistoryRepository {
        return FakeCurrencyConversionHistoryRepository()
    }
}