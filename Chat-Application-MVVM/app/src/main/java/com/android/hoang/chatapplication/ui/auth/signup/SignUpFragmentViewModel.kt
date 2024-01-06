package com.android.hoang.chatapplication.ui.auth.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseViewModel
import com.android.hoang.chatapplication.di.qualifier.IoDispatcher
import com.android.hoang.chatapplication.domain.usecase.AuthUseCase
import com.android.hoang.chatapplication.util.Resource
import com.android.hoang.chatapplication.util.State
import com.android.hoang.chatapplication.util.emailValidator
import com.blankj.utilcode.util.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpFragmentViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val authUseCase: AuthUseCase
) : BaseViewModel(){

    private val _result = MutableLiveData<Resource<String>>()
    val result: LiveData<Resource<String>>
        get() = _result

    val name = MutableLiveData("")
    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val isSignUpButtonEnabled = MutableLiveData(false)
    val isAcceptChecked = MutableLiveData(false)

    init {
        // Khởi tạo giá trị mặc định

    }

    fun updateSignUpButtonState() {
        val nameValue = name.value ?: ""
        val emailValue = email.value ?: ""
        val passwordValue = password.value ?: ""
        val isAccept = isAcceptChecked.value ?: false
        isSignUpButtonEnabled.postValue(nameValue != "" && emailValue != "" && passwordValue != "" && isAccept)
    }

    fun signUp(email: String, password: String) {

        if (name.value?.length!! > 25){
            _result.postValue(Resource.error(message = "Tên quá dài, vui lòng đặt lại tên khác.(${name.value?.length} > 25)"))
            return
        } else if (!emailValidator(email)){
            _result.postValue(Resource.error(message = "Email không đúng định dạng."))
            return
        } else if (password.length < 6){
            _result.postValue(Resource.error(message = "Mật khẩu phải có ít nhất 6 ký tự."))
            return
        }

        viewModelScope.launch {
            authUseCase.invokeSignUp(email, password).collect {
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