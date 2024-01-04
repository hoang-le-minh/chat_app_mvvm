package com.android.hoang.chatapplication.ui.auth.signin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.android.hoang.chatapplication.base.BaseViewModel
import com.android.hoang.chatapplication.di.qualifier.IoDispatcher
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class SignInFragmentViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): BaseViewModel() {
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
}