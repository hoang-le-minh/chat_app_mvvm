package com.android.hoang.chatapplication.ui.main.friend.friendviewpager.listfriend

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
import com.android.hoang.chatapplication.util.Constants
import com.android.hoang.chatapplication.util.Resource
import com.android.hoang.chatapplication.util.State
import com.blankj.utilcode.util.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListFriendFragmentViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val getUserUseCase: GetUserUseCase,
    private val friendUseCase: FriendUseCase
): BaseViewModel() {
    private val _listIdFriend = MutableLiveData<Resource<List<String>>>()
    val listIdFriend: LiveData<Resource<List<String>>>
        get() = _listIdFriend

    private val _listFriend = MutableLiveData<Resource<List<UserFirebase>>>()
    val listFriend: LiveData<Resource<List<UserFirebase>>>
        get() = _listFriend

    init {
        Log.d(Constants.LOG_TAG, "allUserFragmentViewModel: getUserList")
    }

    fun getFriendList(){
        viewModelScope.launch(ioDispatcher) {
            friendUseCase.invokeGetListFriend().collect{
                when (it) {
                    is State.Loading -> {
                        _listIdFriend.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _listIdFriend.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _listIdFriend.postValue(
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

    fun getUserListByListId(list: List<String>){
        viewModelScope.launch {
            getUserUseCase.invokeGetUserListByListId(list).collect{
                when (it) {
                    is State.Loading -> {
                        _listFriend.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _listFriend.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _listFriend.postValue(
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