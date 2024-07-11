package com.android.hoang.chatapplication.data.remote.service

import com.android.hoang.chatapplication.data.remote.model.PushNotification
import com.android.hoang.chatapplication.util.Constants
import com.android.hoang.chatapplication.util.Constants.CONTENT_TYPE
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface Llama2Service {
    @POST("chatbot")
    suspend fun generativeText(
        @Body prompt: PromptLlama2
    ): Response<List<String>>
}

data class PromptLlama2(val prompt: String)