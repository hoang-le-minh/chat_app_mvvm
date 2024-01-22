package com.android.hoang.chatapplication.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseViewModel
import com.android.hoang.chatapplication.data.remote.model.Message
import com.android.hoang.chatapplication.di.qualifier.IoDispatcher
import com.android.hoang.chatapplication.domain.usecase.MessageUseCase
import com.android.hoang.chatapplication.util.Resource
import com.android.hoang.chatapplication.util.State
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatActivityViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val messageUseCase: MessageUseCase
) : BaseViewModel() {
    // result send message
    private val _result = MutableLiveData<Resource<String>>()
    val result: LiveData<Resource<String>>
        get() = _result

    private val _messageList = MutableLiveData<Resource<MutableList<Message>>>()
    val messageList: LiveData<Resource<MutableList<Message>>>
        get() = _messageList

    private val _latestMessage = MutableLiveData<Resource<Message>>()
    val latestMessage: LiveData<Resource<Message>>
        get() = _latestMessage

    init {
        LogUtils.d("$this initialized")
    }

//    fun addMessage(message: Message){
//        _messageList.value?.data?.add(message)
//    }

    fun sendMessage(senderId: String, receiverId: String, message: String, type: Int){
        viewModelScope.launch(ioDispatcher) {
            messageUseCase.invokeSendMessage(senderId, receiverId, message, type).collect{
                when (it) {
                    is State.Loading -> {
                        _result.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _result.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _result.postValue(
                            Resource.error(
                                message = it.message ?: StringUtils.getString(
                                    R.string.something_went_wrong
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    fun readMessage(senderId: String, receiverId: String){
        viewModelScope.launch(ioDispatcher) {
            messageUseCase.invokeReadMessage(senderId, receiverId).collect{
                when (it) {
                    is State.Loading -> {
                        _messageList.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _messageList.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _messageList.postValue(
                            Resource.error(
                                message = it.message ?: StringUtils.getString(
                                    R.string.something_went_wrong
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    fun checkConversationCreated(senderId: String, receiverId: String){
        viewModelScope.launch(ioDispatcher) {
            messageUseCase.invokeCheckConversationCreated(senderId, receiverId).collect{
                when (it) {
                    is State.Loading -> {

                    }
                    is State.Success -> {

                    }
                    is State.Error -> {

                    }
                }
            }
        }
    }

    private var seenListener: ValueEventListener? = null
    private val messageRef = FirebaseDatabase.getInstance().getReference("messages")
    fun seenMessage(senderId: String, receiverId: String){
        seenListener = messageRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    val message = dataSnapshot.getValue(Message::class.java) ?: continue
                    if (message.senderId == receiverId && message.receiverId == senderId){
                        val map = HashMap<String, Any>()
                        map["isSeen"] = "true"
                        dataSnapshot.ref.updateChildren(map)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    fun removeSeenListener(){
        seenListener?.let { messageRef.removeEventListener(it)}
    }


}