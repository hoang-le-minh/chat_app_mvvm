package com.android.hoang.chatapplication.data.remote.service

import com.android.hoang.chatapplication.data.remote.model.PushNotification
import com.android.hoang.chatapplication.util.Constants.CONTENT_TYPE
import com.android.hoang.chatapplication.util.Constants.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {
    @Headers("Authorization: key=$SERVER_KEY,Content-type=$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}