package com.android.hoang.chatapplication.domain.repository

import com.android.hoang.chatapplication.data.remote.model.UserFirebase

interface FriendRepository {
    suspend fun addFriend(userId: String): String
    suspend fun acceptFriend(userId: String): String
    suspend fun refuseFriend(user: UserFirebase): String
    suspend fun cancelFriendRequest(userId: String): String
    suspend fun getListSentFriend(): MutableList<String>
    suspend fun getListRequestFriend(): MutableList<String>
    suspend fun getListFriend(): MutableList<String>
    suspend fun checkExistFriend(userId: String): String
    suspend fun checkRelationship(userId: String): Int
    suspend fun unfriend(userId: String): String
}