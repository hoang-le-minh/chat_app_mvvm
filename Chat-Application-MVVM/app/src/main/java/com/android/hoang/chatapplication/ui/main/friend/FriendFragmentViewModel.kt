package com.android.hoang.chatapplication.ui.main.friend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseViewModel
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.di.qualifier.IoDispatcher
import com.android.hoang.chatapplication.domain.usecase.GetUserUseCase
import com.android.hoang.chatapplication.util.Resource
import com.android.hoang.chatapplication.util.State
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendFragmentViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val getUserUseCase: GetUserUseCase
) : BaseViewModel() {
    private val _userList = MutableLiveData<Resource<List<UserFirebase>>>()
    val userList: LiveData<Resource<List<UserFirebase>>>
        get() = _userList
    init {
        LogUtils.d("$this initialized")
    }

    fun searchUser(query: String){
        viewModelScope.launch(ioDispatcher) {
            getUserUseCase.invokeSearchUser(query).collect{
                when (it) {
                    is State.Loading -> {
                        _userList.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _userList.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _userList.postValue(
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