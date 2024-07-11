package com.android.hoang.chatapplication.di

import com.android.hoang.chatapplication.data.repository.*
import com.android.hoang.chatapplication.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * The Main [Module] that holds all repository classes and provides these instances
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
//    @Provides
//    @Singleton
//    fun provideUserDataSourceImpl(userDataSource: UserDataSourceImpl): UserDataSource =
//        userDataSource

    @Provides
    @Singleton
    fun provideUserRepository(userRepository: UserRepositoryImpl): UserRepository =
        userRepository


    @Provides
    @Singleton
    fun provideMessageRepository(messageRepository: MessageRepositoryImpl): MessageRepository =
        messageRepository

    @Provides
    @Singleton
    fun provideFriendRepository(friendRepository: FriendRepositoryImpl): FriendRepository =
        friendRepository

    @Provides
    @Singleton
    fun provideNotificationRepository(notificationRepository: NotificationRepositoryImpl): NotificationRepository =
        notificationRepository

    @Provides
    @Singleton
    fun provideChatBotRepository(chatBotRepository: ChatBotRepositoryImpl): ChatBotRepository =
        chatBotRepository

    @Provides
    @Singleton
    fun provideTranslateRepository(translateRepository: TranslateRepositoryImpl): TranslateRepository =
        translateRepository
}