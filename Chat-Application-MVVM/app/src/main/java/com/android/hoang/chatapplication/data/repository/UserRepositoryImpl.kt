package com.android.hoang.chatapplication.data.repository

import com.android.hoang.chatapplication.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Implementation of [UserRepository] class
 */
class UserRepositoryImpl @Inject constructor(private val userDataSource: UserDataSource) :
    UserRepository {

    override suspend fun getUser(username: String) = userDataSource.getUser(username = username)
}