package com.tripaty.portfolio.data.di

import com.tripaty.portfolio.data.repository.PortfolioRepositoryImpl
import com.tripaty.portfolio.data.repository.PreferencesRepositoryImpl
import com.tripaty.portfolio.data.repository.TiruChatRepositoryImpl
import com.tripaty.portfolio.domain.repository.PortfolioRepository
import com.tripaty.portfolio.domain.repository.PreferencesRepository
import com.tripaty.portfolio.domain.repository.TiruChatRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindPortfolio(repo: PortfolioRepositoryImpl): PortfolioRepository
    @Binds @Singleton abstract fun bindPrefs(repo: PreferencesRepositoryImpl): PreferencesRepository
    @Binds @Singleton abstract fun bindTiru(repo: TiruChatRepositoryImpl): TiruChatRepository
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides @Singleton @Named("portfolioBaseUrl")
    fun provideBaseUrl(): String = "https://tripati.github.io/Portfolio/"

    @Provides @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }
}
