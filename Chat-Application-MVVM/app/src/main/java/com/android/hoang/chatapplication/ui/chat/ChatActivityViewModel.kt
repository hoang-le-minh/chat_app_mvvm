package com.android.hoang.chatapplication.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseViewModel
import com.android.hoang.chatapplication.di.qualifier.IoDispatcher
import com.android.hoang.chatapplication.domain.usecase.SendMessageUseCase
import com.android.hoang.chatapplication.util.Resource
import com.android.hoang.chatapplication.util.State
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatActivityViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val sendMessageUseCase: SendMessageUseCase
) : BaseViewModel() {

    private val _result = MutableLiveData<Resource<String>>()
    val result: LiveData<Resource<String>>
        get() = _result

    init {
        LogUtils.d("$this initialized")
    }

    fun sendMessage(senderId: String, receiverId: String, message: String, type: Int){
        viewModelScope.launch(ioDispatcher) {
            sendMessageUseCase.invokeSendMessage(senderId, receiverId, message, type).collect{
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

}