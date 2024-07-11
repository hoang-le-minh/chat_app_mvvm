package com.android.hoang.chatapplication.di

import com.android.hoang.chatapplication.data.remote.model.MessageResponse
import com.android.hoang.chatapplication.data.remote.service.GPTService
import com.android.hoang.chatapplication.data.remote.service.Llama2Service
import com.android.hoang.chatapplication.data.remote.service.NotificationApi
import com.android.hoang.chatapplication.data.remote.service.TranslateService
import com.android.hoang.chatapplication.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
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
                .connectTimeout(30, TimeUnit.SECONDS) // Connection timeout
                .readTimeout(30, TimeUnit.SECONDS)    // Read timeout
                .writeTimeout(30, TimeUnit.SECONDS)   // Write timeout
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

    @Singleton
    @Provides
    @Named("llama2")
    fun provideLlama2Retrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Constants.LLAMA2_URL)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideLlama2Api(@Named("llama2") retrofit: Retrofit): Llama2Service =
        retrofit.create(Llama2Service::class.java)

    @Singleton
    @Provides
    @Named("translate")
    fun provideTranslateRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://translation.googleapis.com/")
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideTranslateApi(@Named("translate") retrofit: Retrofit): TranslateService =
        retrofit.create(TranslateService::class.java)

//    @Provides
//    @Singleton
//    fun provideUserService(retrofit: Retrofit): UserService =
//        retrofit.create(UserService::class.java)
}