package com.android.hoang.chatapplication.domain.usecase

import com.android.hoang.chatapplication.data.remote.model.MessageParamPost
import com.android.hoang.chatapplication.data.remote.model.MessageParamPost2
import com.android.hoang.chatapplication.data.remote.model.MessageResponse
import com.android.hoang.chatapplication.data.remote.model.MessageResponse2
import com.android.hoang.chatapplication.domain.repository.ChatBotRepository
import com.android.hoang.chatapplication.util.State
import com.blankj.utilcode.util.LogUtils
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
}