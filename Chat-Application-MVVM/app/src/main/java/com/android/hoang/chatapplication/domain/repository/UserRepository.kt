package com.android.hoang.chatapplication.domain.repository

import com.android.hoang.chatapplication.data.remote.model.UserFirebase

/**
 * Methods of User Repository
 */
interface UserRepository {

    suspend fun getCurrentUser(): UserFirebase?
    suspend fun signUp(email: String, password: String): String
    suspend fun signIn(email: String, password: String): String
    suspend fun getAllUser(): List<UserFirebase>
    suspend fun updateUser(name: String, imageUrl: String, phoneNumber: String, dateOfBirth: String): UserFirebase?
    suspend fun getUserListByListId(list: MutableList<String>): List<UserFirebase>
    suspend fun filterUser(query: String): List<UserFirebase>
}