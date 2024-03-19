package com.android.hoang.chatapplication.data.remote.service

import com.android.hoang.chatapplication.data.remote.model.MessageParamPost
import com.android.hoang.chatapplication.data.remote.model.MessageParamPost2
import com.android.hoang.chatapplication.data.remote.model.MessageResponse
import com.android.hoang.chatapplication.data.remote.model.MessageResponse2
import com.android.hoang.chatapplication.util.Constants.CONTENT_TYPE
import com.android.hoang.chatapplication.util.Constants.OPENAI_API_KEY
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GPTService {
    @Headers(
        "Content-Type: $CONTENT_TYPE",
        "Authorization: Bearer $OPENAI_API_KEY"
    )
    @POST("completions")
    suspend fun getResponseChat(@Body messageParamPost: MessageParamPost): Response<MessageResponse>

    @Headers(
        "Content-Type: $CONTENT_TYPE",
        "Authorization: Bearer $OPENAI_API_KEY"
    )
    @POST("completions")
    suspend fun getResponseChat2(@Body messageParamPost: MessageParamPost2): Response<MessageResponse2>
}