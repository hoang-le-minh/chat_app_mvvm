//package com.android.hoang.chatapplication.data.remote.source
//
//import com.android.hoang.chatapplication.data.remote.model.UserResponse
//import com.android.hoang.chatapplication.data.remote.service.UserService
//import com.android.hoang.chatapplication.data.repository.UserDataSource
//import retrofit2.Response
//import javax.inject.Inject
//
///**
// * Implementation of [UserDataSource] class
// */
//class UserDataSourceImpl @Inject constructor(private val userService: UserService) :
//    UserDataSource {
//
//    override suspend fun getUser(username: String): Response<UserResponse> =
//        userService.getUser(username = username)
//
//    override suspend fun signUp(email: String, password: String): String {
//        // call firebase
//        return "Success"
//    }
//}