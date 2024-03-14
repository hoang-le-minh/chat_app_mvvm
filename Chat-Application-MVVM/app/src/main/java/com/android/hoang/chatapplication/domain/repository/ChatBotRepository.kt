package com.android.hoang.chatapplication.domain.repository

import com.android.hoang.chatapplication.data.remote.model.MessageParam
import com.android.hoang.chatapplication.data.remote.model.MessageParamPost
import com.android.hoang.chatapplication.data.remote.model.MessageResponse
import retrofit2.Response

interface ChatBotRepository {
    suspend fun getChatResponse(messageParamPost: MessageParamPost): Response<MessageResponse>
}