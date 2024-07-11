package com.android.hoang.chatapplication.domain.repository

import com.android.hoang.chatapplication.data.remote.model.*
import com.android.hoang.chatapplication.data.remote.service.PromptLlama2
import retrofit2.Response

interface ChatBotRepository {
    suspend fun getChatResponse(messageParamPost: MessageParamPost): Response<MessageResponse>
    suspend fun getChatResponse2(messageParamPost: MessageParamPost2): Response<MessageResponse2>

    suspend fun getGeminiResponse(prompt: String): GeminiChat

    suspend fun generativeText(prompt: PromptLlama2): Response<List<String>>
}