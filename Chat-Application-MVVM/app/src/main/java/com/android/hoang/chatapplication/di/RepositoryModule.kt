package com.android.hoang.chatapplication.di

import com.android.hoang.chatapplication.data.repository.FriendRepositoryImpl
import com.android.hoang.chatapplication.data.repository.MessageRepositoryImpl
import com.android.hoang.chatapplication.data.repository.NotificationRepositoryImpl
import com.android.hoang.chatapplication.data.repository.UserRepositoryImpl
import com.android.hoang.chatapplication.domain.repository.FriendRepository
import com.android.hoang.chatapplication.domain.repository.MessageRepository
import com.android.hoang.chatapplication.domain.repository.NotificationRepository
import com.android.hoang.chatapplication.domain.repository.UserRepository
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
}