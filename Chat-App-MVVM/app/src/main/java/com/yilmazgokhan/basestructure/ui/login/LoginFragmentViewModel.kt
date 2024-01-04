package com.yilmazgokhan.basestructure.ui.login

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.yilmazgokhan.basestructure.base.BaseViewModel
import com.yilmazgokhan.basestructure.di.qualifier.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.math.log

@ExperimentalCoroutinesApi
class LoginFragmentViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
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