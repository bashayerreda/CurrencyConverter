package com.example.paymobtask.core.di

import android.content.Context
import androidx.room.Room
import com.example.paymobtask.BuildConfig
import com.example.paymobtask.data.repository.CurrencyConversionHistoryRepositoryImpl
import com.example.paymobtask.data.repository.CurrencyRepositoryImpl
import com.example.paymobtask.data.local.dao.ConversionHistoryDao
import com.example.paymobtask.data.local.db.AppDatabase
import com.example.paymobtask.data.remote.api.FixerApiService
import com.example.paymobtask.data.remote.interceptors.ApiKeyInterceptor
import com.example.paymobtask.domain.NetworkHelper
import com.example.paymobtask.domain.repository.CurrencyConversionHistoryRepository
import com.example.paymobtask.domain.repository.CurrencyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Dependency injection provider class
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        apiKeyInterceptor: ApiKeyInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(apiKeyInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideFixerApiService(retrofit: Retrofit): FixerApiService =
        retrofit.create(FixerApiService::class.java)

    @Provides
    @Singleton
    fun provideCurrencyRepository(
        apiService: FixerApiService,
        networkHelper: NetworkHelper
    ): CurrencyRepository = CurrencyRepositoryImpl(apiService, networkHelper)

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "currency_database"
    ).build()

    @Provides
    fun provideConversionHistoryDao(
        appDatabase: AppDatabase
    ): ConversionHistoryDao = appDatabase.conversionHistoryDao()

    @Provides
    @Singleton
    fun provideCurrencyHistoryRepository(
        dao: ConversionHistoryDao
    ): CurrencyConversionHistoryRepository = CurrencyConversionHistoryRepositoryImpl(dao)
}