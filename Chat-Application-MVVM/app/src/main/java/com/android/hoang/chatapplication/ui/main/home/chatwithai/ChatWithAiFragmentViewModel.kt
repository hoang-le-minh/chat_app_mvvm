package com.android.hoang.chatapplication.ui.main.home.chatwithai

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseViewModel
import com.android.hoang.chatapplication.data.remote.model.*
import com.android.hoang.chatapplication.data.remote.service.PromptLlama2
import com.android.hoang.chatapplication.di.qualifier.IoDispatcher
import com.android.hoang.chatapplication.domain.usecase.ChatBotUseCase
import com.android.hoang.chatapplication.domain.usecase.TranslateUseCase
import com.android.hoang.chatapplication.util.Constants
import com.android.hoang.chatapplication.util.Resource
import com.android.hoang.chatapplication.util.State
import com.blankj.utilcode.util.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatWithAiFragmentViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val chatBotUseCase: ChatBotUseCase,
    private val translateUseCase: TranslateUseCase
) : BaseViewModel() {
    //region city info
    private val _message = MutableLiveData<Resource<MessageResponse>>()
    val message: LiveData<Resource<MessageResponse>>
        get() = _message

    private val _message2 = MutableLiveData<Resource<MessageResponse2>>()
    val message2: LiveData<Resource<MessageResponse2>>
        get() = _message2

    private val _messageGemini = MutableLiveData<Resource<GeminiChat>>()
    val messageGemini: LiveData<Resource<GeminiChat>>
        get() = _messageGemini

    private val _messageLlama2 = MutableLiveData<Resource<List<String>>>()
    val messageLlama2: LiveData<Resource<List<String>>>
        get() = _messageLlama2

    private val _translateText = MutableLiveData<Resource<String>>()
    val translateText: LiveData<Resource<String>>
        get() = _translateText

    val question = MutableLiveData<String>()
    val isSendButtonEnabled = MutableLiveData<Boolean>()
    //endregion

    init {
        question.value = ""
        isSendButtonEnabled.value = false
    }

    fun updateSendButtonState() {
        val question = question.value ?: ""
        isSendButtonEnabled.postValue(question != "")
    }

    fun getChatResponse(prompt: String){
        val messageParam = MessageParam("user", prompt)
        val messageParamPost = MessageParamPost("gpt-3.5-turbo", listOf(messageParam))
        viewModelScope.launch {
            chatBotUseCase.invokeGetChatResponse(messageParamPost).collect {
                when (it) {
                    is State.Loading -> {
                        _message.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _message.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _message.postValue(
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

    fun getChatResponse2(prompt: String){
        val messageParamPost2 = MessageParamPost2("gpt-3.5-turbo-instruct", prompt, 1000, 0)
        viewModelScope.launch {
            chatBotUseCase.invokeGetChatResponse2(messageParamPost2).collect {
                when (it) {
                    is State.Loading -> {
                        _message2.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _message2.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _message2.postValue(
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

    fun getGeminiResponse(prompt: String){

        viewModelScope.launch {
            chatBotUseCase.invokeGetGeminiResponse(prompt).collect {
                when (it) {
                    is State.Loading -> {
                        _messageGemini.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _messageGemini.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _messageGemini.postValue(
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

    fun getLlama2Response(prompt: String){
        val promptLlama2 = PromptLlama2(prompt = prompt)
        viewModelScope.launch {
            chatBotUseCase.invokeGetLlama2Response(promptLlama2).collect {
                when (it) {
                    is State.Loading -> {
                        _messageLlama2.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _messageLlama2.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _messageLlama2.postValue(
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

    fun translateText(text: String, target: String){
        viewModelScope.launch {
            translateUseCase.invokeTranslate(text, target).collect {
                when (it) {
                    is State.Loading -> {
                        _translateText.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _translateText.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _translateText.postValue(
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