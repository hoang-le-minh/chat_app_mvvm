package com.android.hoang.chatapplication.di.qualifier

import com.android.hoang.chatapplication.data.remote.source.UserDataSourceImpl
import com.android.hoang.chatapplication.data.repository.UserDataSource
import com.android.hoang.chatapplication.data.repository.UserRepositoryImpl
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
    @Provides
    @Singleton
    fun provideUserDataSourceImpl(userDataSource: UserDataSourceImpl): UserDataSource =
        userDataSource

    @Provides
    @Singleton
    fun provideUserRepository(userRepository: UserRepositoryImpl): UserRepository =
        userRepository
}