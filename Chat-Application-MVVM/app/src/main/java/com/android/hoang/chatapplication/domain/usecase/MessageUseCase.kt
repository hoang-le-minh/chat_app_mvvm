package com.android.hoang.chatapplication.domain.usecase

import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.data.remote.model.Message
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.domain.repository.MessageRepository
import com.android.hoang.chatapplication.util.State
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MessageUseCase @Inject constructor(private val messageRepository: MessageRepository) {
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

    suspend fun invokeReadMessage(senderId: String, receiverId: String): Flow<State<MutableList<Message>>> = flow {
        try {
            emit(State.Loading())
            val messageList = messageRepository.readMessage(senderId, receiverId)
            if(messageList.isNotEmpty()){
                emit(State.Success(messageList))
            } else
                emit(State.Error(StringUtils.getString(R.string.cannot_read_message)))
        } catch (e: Exception){
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }

    suspend fun invokeCheckConversationCreated(senderId: String, receiverId: String): Flow<State<String>> = flow {
        try {
            emit(State.Loading())
            val result = messageRepository.checkConversationCreated(senderId, receiverId)
            if (result == StringUtils.getString(R.string.create_conversation)){
                emit(State.Success(result))
            } else
                emit(State.Error(StringUtils.getString(R.string.exist_conversation)))
        } catch (e: Exception){
            emit(State.Error(e.localizedMessage))
        }
    }

    fun invokeLatestMessage(senderId: String, receiverId: String): Flow<State<Message>> = flow {
        try {
            emit(State.Loading())
            val latestMessage = messageRepository.latestMessage(senderId, receiverId)
            if (latestMessage != null){
                emit(State.Success(latestMessage))
            } else
                emit(State.Error(StringUtils.getString(R.string.something_went_wrong)))
        } catch (e: Exception){
            emit(State.Error(e.localizedMessage))
        }
    }

    fun invokeLatestMessageList(listId: List<String>): Flow<State<MutableList<Message>>> = flow {

        try {
            emit(State.Loading())
            val latestMessageList = messageRepository.latestMessageList(listId)
            if (latestMessageList.isNotEmpty()){
                emit(State.Success(latestMessageList))
            } else
                emit(State.Error(StringUtils.getString(R.string.something_went_wrong)))
        } catch (e: Exception){
            emit(State.Error(e.localizedMessage))
        }
    }

    fun invokeConversationList(): Flow<State<MutableList<String>>> = flow {
        try {
            emit(State.Loading())
            val conversationList = messageRepository.conversationList()
            if (conversationList.isNotEmpty()){
                emit(State.Success(conversationList))
            } else
                emit(State.Error(StringUtils.getString(R.string.something_went_wrong)))
        } catch (e: Exception){
            emit(State.Error(e.localizedMessage))
        }
    }

    fun invokeDeleteConversation(userId: String): Flow<State<String>> = flow {
        try {
            emit(State.Loading())
            val result = messageRepository.deleteConversation(userId)
            if (result == StringUtils.getString(R.string.ok))
                emit(State.Success(result))
            else emit(State.Error(result))
        } catch (e: Exception){
            emit(State.Error(e.localizedMessage))
        }
    }
}