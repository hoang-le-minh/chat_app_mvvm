package com.android.hoang.chatapplication.ui.main.friend.friendviewpager.friendrequest

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
class FriendRequestFragmentViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val getUserUseCase: GetUserUseCase,
    private val friendUseCase: FriendUseCase
): BaseViewModel() {

    private val _listIdRequest = MutableLiveData<Resource<List<String>>>()
    val listIdRequest: LiveData<Resource<List<String>>>
        get() = _listIdRequest

    private val _listRequest = MutableLiveData<Resource<List<UserFirebase>>>()
    val listRequest: LiveData<Resource<List<UserFirebase>>>
        get() = _listRequest

    private val _listIdSent = MutableLiveData<Resource<List<String>>>()
    val listIdSent: LiveData<Resource<List<String>>>
        get() = _listIdSent

    private val _listSent = MutableLiveData<Resource<List<UserFirebase>>>()
    val listSent: LiveData<Resource<List<UserFirebase>>>
        get() = _listSent

    init {
        Log.d(Constants.LOG_TAG, "allUserFragmentViewModel: getUserList")
    }

    fun getRequestIdList(){
        viewModelScope.launch(ioDispatcher) {
            friendUseCase.invokeGetListRequestFriend().collect{
                when (it) {
                    is State.Loading -> {
                        _listIdRequest.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _listIdRequest.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _listIdRequest.postValue(
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

    fun getSentIdList(){
        viewModelScope.launch(ioDispatcher) {
            friendUseCase.invokeGetListSentFriend().collect{
                when (it) {
                    is State.Loading -> {
                        _listIdSent.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _listIdSent.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _listIdSent.postValue(
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

    fun getRequestListByListId(list: List<String>){
        viewModelScope.launch {
            getUserUseCase.invokeGetUserListByListId(list).collect{
                when (it) {
                    is State.Loading -> {
                        _listRequest.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _listRequest.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _listRequest.postValue(
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

    fun getSentListByListId(list: List<String>){
        viewModelScope.launch {
            getUserUseCase.invokeGetUserListByListId(list).collect{
                when (it) {
                    is State.Loading -> {
                        _listSent.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _listSent.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _listSent.postValue(
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