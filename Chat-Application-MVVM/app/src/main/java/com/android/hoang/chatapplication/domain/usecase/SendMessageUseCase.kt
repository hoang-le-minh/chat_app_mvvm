package com.android.hoang.chatapplication.domain.usecase

import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.domain.repository.MessageRepository
import com.android.hoang.chatapplication.util.State
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(private val messageRepository: MessageRepository) {
    suspend fun invokeSendMessage(senderId: String, receiverId: String, message: String, type: Int): Flow<State<String>> = flow {
        try {
            emit(State.Loading())
            val result = messageRepository.sendMessage(senderId, receiverId, message, type)
            if (result == StringUtils.getString(R.string.send_message_success))
                emit(State.Success(result))
            else
                emit(State.Error(StringUtils.getString(R.string.send_message_failed)))
        } catch (e: Exception) {
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }
}