package com.android.hoang.chatapplication.domain.usecase

import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.data.remote.model.*
import com.android.hoang.chatapplication.data.remote.service.PromptLlama2
import com.android.hoang.chatapplication.data.remote.service.TranslationResponse
import com.android.hoang.chatapplication.domain.repository.ChatBotRepository
import com.android.hoang.chatapplication.domain.repository.TranslateRepository
import com.android.hoang.chatapplication.util.State
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChatBotUseCase @Inject constructor(private val chatBotRepository: ChatBotRepository) {
    suspend fun invokeGetChatResponse(messageParamPost: MessageParamPost) = flow<State<MessageResponse>> {
        try {
            emit(State.Loading())
            val result = chatBotRepository.getChatResponse(messageParamPost)
            if (result.isSuccessful)
                emit(State.Success(result.body()))
            else
                emit(State.Error(result.message().orEmpty()))
        } catch (e: Exception) {
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }

    suspend fun invokeGetChatResponse2(messageParamPost: MessageParamPost2) = flow<State<MessageResponse2>> {
        try {
            emit(State.Loading())
            val result = chatBotRepository.getChatResponse2(messageParamPost)
            if (result.isSuccessful)
                emit(State.Success(result.body()))
            else
                emit(State.Error(result.message().orEmpty()))
        } catch (e: Exception) {
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }

    suspend fun invokeGetGeminiResponse(prompt: String) = flow<State<GeminiChat>> {
        try {
            emit(State.Loading())
            val result = chatBotRepository.getGeminiResponse(prompt)
            if (result.prompt != StringUtils.getString(R.string.error))
                emit(State.Success(result))
            else
                emit(State.Error(result.prompt))
        } catch (e: Exception) {
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }

    suspend fun invokeGetLlama2Response(prompt: PromptLlama2) = flow<State<List<String>>> {
        try {
            emit(State.Loading())
            val result = chatBotRepository.generativeText(prompt)
            if (result.isSuccessful) {
                emit(State.Success(result.body()))

            } else {
                emit(State.Error(result.message()))
            }
        } catch (e: Exception){
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }

}