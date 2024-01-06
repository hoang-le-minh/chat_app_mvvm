package com.android.hoang.chatapplication.ui.auth.signin

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseViewModel
import com.android.hoang.chatapplication.di.qualifier.IoDispatcher
import com.android.hoang.chatapplication.domain.usecase.AuthUseCase
import com.android.hoang.chatapplication.util.Resource
import com.android.hoang.chatapplication.util.State
import com.android.hoang.chatapplication.util.emailValidator
import com.blankj.utilcode.util.StringUtils
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInFragmentViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val authUseCase: AuthUseCase
): BaseViewModel() {

    private val _result = MutableLiveData<Resource<String>>()
    val result: LiveData<Resource<String>>
        get() = _result

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val isLoginButtonEnabled = MutableLiveData<Boolean>()

    init {
        // Khởi tạo giá trị mặc định
        email.value = ""
        password.value = ""
        isLoginButtonEnabled.value = false
    }

    fun updateLoginButtonState() {
        val emailValue = email.value ?: ""
        val passwordValue = password.value ?: ""
        isLoginButtonEnabled.postValue(emailValue != "" && passwordValue != "")
    }

    fun signIn(email: String, password: String){

        if (!emailValidator(email)){
            _result.postValue(Resource.error(message = "Email không đúng định dạng."))
            return
        } else if (password.length < 6){
            _result.postValue(Resource.error(message = "Mật khẩu phải có ít nhất 6 ký tự."))
            return
        }

        viewModelScope.launch {
            authUseCase.invokeSignIn(email, password).collect {
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