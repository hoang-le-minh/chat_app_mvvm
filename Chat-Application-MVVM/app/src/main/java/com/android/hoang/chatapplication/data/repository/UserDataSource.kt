package com.android.hoang.chatapplication.data.repository

import com.android.hoang.chatapplication.data.remote.model.UserResponse
import retrofit2.Response

/**
 * Methods of User Data Source
 */
interface UserDataSource {

    suspend fun getUser(username: String): Response<UserResponse>
}