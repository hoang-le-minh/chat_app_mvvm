package com.android.hoang.chatapplication.ui.auth.signup

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseViewModel
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.di.qualifier.IoDispatcher
import com.android.hoang.chatapplication.domain.usecase.AuthUseCase
import com.android.hoang.chatapplication.util.Resource
import com.android.hoang.chatapplication.util.State
import com.android.hoang.chatapplication.util.emailValidator
import com.blankj.utilcode.util.StringUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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

    fun checkBoxChecked(){
        isAcceptChecked.postValue(isAcceptChecked.value?.not())
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
            _result.postValue(Resource.error(message = StringUtils.getString(R.string.name_too_long)))
            return
        } else if (!emailValidator(email)){
            _result.postValue(Resource.error(message = StringUtils.getString(R.string.email_invalid)))
            return
        } else if (password.length < 6){
            _result.postValue(Resource.error(message = StringUtils.getString(R.string.password_at_least)))
            return
        }

        viewModelScope.launch {
            authUseCase.invokeSignUp(email, password).collect {
                when (it) {
                    is State.Loading -> {
                        _result.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val userId: String = currentUser!!.uid
                        val myRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
                        val map = HashMap<String, String>()
                        map["id"] = userId
                        map["email"] = email.trim().lowercase()
                        map["username"] = name.value!!
                        map["imageUrl"] = ""
                        myRef.setValue(map)
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