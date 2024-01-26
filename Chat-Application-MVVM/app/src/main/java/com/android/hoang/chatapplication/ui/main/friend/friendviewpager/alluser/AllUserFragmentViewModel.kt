package com.android.hoang.chatapplication.ui.main.friend.friendviewpager.alluser

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseViewModel
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.di.qualifier.IoDispatcher
import com.android.hoang.chatapplication.domain.usecase.FriendUseCase
import com.android.hoang.chatapplication.domain.usecase.GetUserUseCase
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.android.hoang.chatapplication.util.Resource
import com.android.hoang.chatapplication.util.State
import com.blankj.utilcode.util.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllUserFragmentViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val getUserUseCase: GetUserUseCase,
    private val friendUseCase: FriendUseCase
    ): BaseViewModel() {
    private val _userList = MutableLiveData<Resource<List<UserFirebase>>>()
    val userList: LiveData<Resource<List<UserFirebase>>>
        get() = _userList

    private val _addFriendResult = MutableLiveData<Resource<String>>()
    val addFriendResult: LiveData<Resource<String>>
        get() = _addFriendResult

    private val _existFriendResult = MutableLiveData<Resource<String>>()
    val existFriendResult: LiveData<Resource<String>>
        get() = _existFriendResult

    init {
        Log.d(LOG_TAG, "allUserFragmentViewModel: getUserList")
        getUserList()
    }

    private fun getUserList(){
        viewModelScope.launch(ioDispatcher) {
            getUserUseCase.invokeAllUser().collect{
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

    fun addFriend(user: UserFirebase){
        viewModelScope.launch {
            friendUseCase.invokeAddFriend(user).collect{
                when (it) {
                    is State.Loading -> {
                        _addFriendResult.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _addFriendResult.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _addFriendResult.postValue(
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

//    fun checkExistFriend(userId: String){
//        viewModelScope.launch {
//            friendUseCase.invokeCheckExistFriend(userId).collect{
//                when (it) {
//                    is State.Loading -> {
//                        _existFriendResult.postValue(Resource.loading())
//                    }
//                    is State.Success -> {
//                        _existFriendResult.postValue(Resource.success(it.data))
//                    }
//                    is State.Error -> {
//                        _existFriendResult.postValue(
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