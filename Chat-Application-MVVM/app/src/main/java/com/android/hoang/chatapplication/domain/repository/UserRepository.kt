package com.android.hoang.chatapplication.domain.repository

import com.android.hoang.chatapplication.data.remote.model.UserResponse
import retrofit2.Response

/**
 * Methods of User Repository
 */
interface UserRepository {

    suspend fun getUser(username: String): Response<UserResponse>
    suspend fun signUp(email: String, password: String): String
    suspend fun signIn(email: String, password: String): String
}