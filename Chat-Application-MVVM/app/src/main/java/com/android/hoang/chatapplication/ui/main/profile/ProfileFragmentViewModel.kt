package com.android.hoang.chatapplication.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseViewModel
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.data.remote.model.UserResponse
import com.android.hoang.chatapplication.di.qualifier.IoDispatcher
import com.android.hoang.chatapplication.domain.usecase.GetUserUseCase
import com.android.hoang.chatapplication.util.Resource
import com.android.hoang.chatapplication.util.State
import com.blankj.utilcode.util.StringUtils
import com.google.firebase.auth.FirebaseAuth
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileFragmentViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val getUserUseCase: GetUserUseCase
) : BaseViewModel(){

    private val _user = MutableLiveData<Resource<UserFirebase>>()
    val user: LiveData<Resource<UserFirebase>>
        get() = _user

    init {
        getCurrentUser()
    }

    fun signOut(){
        FirebaseAuth.getInstance().signOut()
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            getUserUseCase.invokeCurrentUser().collect {
                when (it) {
                    is State.Loading -> {
                        _user.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _user.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _user.postValue(
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