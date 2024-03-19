package com.android.hoang.chatapplication.domain.repository

import com.android.hoang.chatapplication.data.remote.model.*
import retrofit2.Response

interface ChatBotRepository {
    suspend fun getChatResponse(messageParamPost: MessageParamPost): Response<MessageResponse>
    suspend fun getChatResponse2(messageParamPost: MessageParamPost2): Response<MessageResponse2>
}