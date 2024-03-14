package com.android.hoang.chatapplication.di

import com.android.hoang.chatapplication.data.remote.model.MessageResponse
import com.android.hoang.chatapplication.data.remote.service.GPTService
import com.android.hoang.chatapplication.data.remote.service.NotificationApi
import com.android.hoang.chatapplication.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * The Main [Module] that holds all network classes and provides these instances
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideBaseUrl() = Constants.BASE_URL

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
    }

    @Singleton
    @Provides
    @Named("fcm")
    fun provideFcmRetrofit(okHttpClient: OkHttpClient, BASE_URL: String): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideNotificationApi(@Named("fcm") retrofit: Retrofit): NotificationApi =
        retrofit.create(NotificationApi::class.java)

    @Singleton
    @Provides
    @Named("chat-gpt")
    fun provideChatGPTRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.openai.com/v1/chat/")
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideChatGPTApi(@Named("chat-gpt") retrofit: Retrofit): GPTService =
        retrofit.create(GPTService::class.java)

//    @Provides
//    @Singleton
//    fun provideUserService(retrofit: Retrofit): UserService =
//        retrofit.create(UserService::class.java)
}