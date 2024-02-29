package com.android.hoang.chatapplication.ui.main.home

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
import com.blankj.utilcode.util.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val messageUseCase: MessageUseCase
    //private val userRepository: UserRepository
) : BaseViewModel() {

    //region city info
    private val _latestMessage = MutableLiveData<Resource<Message>>()
    val latestMessage: LiveData<Resource<Message>>
        get() = _latestMessage
    //endregion

    private val _latestMessageList = MutableLiveData<Resource<MutableList<Message>>>()
    val latestMessageList: LiveData<Resource<MutableList<Message>>>
        get() = _latestMessageList

    private val _conversationList = MutableLiveData<Resource<MutableList<String>>>()
    val conversationList: LiveData<Resource<MutableList<String>>>
        get() = _conversationList
    /**
     * Send HTTP Request for get user info
     */

    fun getLatestMessageList(listId: List<String>){
        viewModelScope.launch(ioDispatcher) {
            messageUseCase.invokeLatestMessageList(listId).collect{
                when (it) {
                    is State.Loading -> {
                        _latestMessageList.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        val sortList = it.data?.sortedByDescending { it1 -> parseDateTime(it1.createAt) } as MutableList<Message>
                        _latestMessageList.postValue(Resource.success(sortList))
                    }
                    is State.Error -> {
                        _latestMessageList.postValue(
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

    private fun parseDateTime(dateTime: String): Long {
        // Chuyển đổi chuỗi ngày tháng thành timestamp để sắp xếp
        val pattern = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
        val date = pattern.parse(dateTime)
        return date?.time ?: 0L
    }

    fun getConversationList(){
        viewModelScope.launch(ioDispatcher) {
            messageUseCase.invokeConversationList().collect{
                when (it) {
                    is State.Loading -> {
                        _conversationList.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _conversationList.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _conversationList.postValue(
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

//    fun latestMessage(senderId: String, receiverId: String) {
//        viewModelScope.launch(ioDispatcher) {
//            messageUseCase.invokeLatestMessage(senderId, receiverId).collect{
//                when (it) {
//                    is State.Loading -> {
//                        _latestMessage.postValue(Resource.loading())
//                    }
//                    is State.Success -> {
//                        _latestMessage.postValue(Resource.success(it.data))
//                    }
//                    is State.Error -> {
//                        _latestMessage.postValue(
//                            Resource.error(
//                                message = it.message ?: StringUtils.getString(
//                                    R.string.something_went_wrong
//                                )
//                            )
//                        )
//                    }
//                }
//            }
//        }
//    }
}