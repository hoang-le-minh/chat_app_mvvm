package com.android.hoang.chatapplication.data.repository

import com.android.hoang.chatapplication.data.remote.model.*
import com.android.hoang.chatapplication.data.remote.service.GPTService
import com.android.hoang.chatapplication.domain.repository.ChatBotRepository
import retrofit2.Response
import javax.inject.Inject

class ChatBotRepositoryImpl @Inject constructor(private val gptService: GPTService): ChatBotRepository {
    override suspend fun getChatResponse(messageParamPost: MessageParamPost): Response<MessageResponse> = gptService.getResponseChat(messageParamPost)

    override suspend fun getChatResponse2(messageParamPost: MessageParamPost2): Response<MessageResponse2> = gptService.getResponseChat2(messageParamPost)
}