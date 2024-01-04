package com.yilmazgokhan.basestructure.data.repository

import com.yilmazgokhan.basestructure.data.remote.model.UserResponse
import com.yilmazgokhan.basestructure.domain.repository.UserRepository
import retrofit2.Response
import javax.inject.Inject

/**
 * Implementation of [UserRepository] class
 */
class UserRepositoryImpl @Inject constructor(private val userDataSource: UserDataSource) :
    UserRepository {

    override suspend fun getUser(username: String) = userDataSource.getUser(username = username)
}