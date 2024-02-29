//package com.android.hoang.chatapplication.data.remote.service
//
//import com.android.hoang.chatapplication.data.remote.model.UserResponse
//import retrofit2.Response
//import retrofit2.http.GET
//import retrofit2.http.Path
//
///**
// * The main services that handle all user endpoint processes
// */
//interface UserService {
//
//    @GET("users/{username}")
//    suspend fun getUser(@Path("username") username: String): Response<UserResponse>
//}