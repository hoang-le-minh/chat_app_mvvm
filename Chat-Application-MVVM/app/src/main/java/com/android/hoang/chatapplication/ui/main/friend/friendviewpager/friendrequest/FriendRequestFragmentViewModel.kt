package com.android.hoang.chatapplication.ui.main.friend.friendviewpager.friendrequest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseViewModel
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.di.qualifier.IoDispatcher
import com.android.hoang.chatapplication.domain.usecase.FriendUseCase
import com.android.hoang.chatapplication.domain.usecase.GetUserUseCase
import com.android.hoang.chatapplication.util.Constants
import com.android.hoang.chatapplication.util.Resource
import com.android.hoang.chatapplication.util.State
import com.android.hoang.chatapplication.util.Status
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

    private val _listIdRequest = MutableLiveData<Resource<MutableList<String>>>()
    val listIdRequest: LiveData<Resource<MutableList<String>>>
        get() = _listIdRequest

    private val _listRequest = MutableLiveData<Resource<MutableList<UserFirebase>>>()
    val listRequest: LiveData<Resource<MutableList<UserFirebase>>>
        get() = _listRequest

    private val _listIdSent = MutableLiveData<Resource<MutableList<String>>>()
    val listIdSent: LiveData<Resource<MutableList<String>>>
        get() = _listIdSent

    private val _listSent = MutableLiveData<Resource<MutableList<UserFirebase>>>()
    val listSent: LiveData<Resource<MutableList<UserFirebase>>>
        get() = _listSent

    private val _resultAccept = MutableLiveData<Resource<String>>()
    val resultAccept: LiveData<Resource<String>>
        get() = _resultAccept

    private val _resultRefuse = MutableLiveData<Resource<String>>()
    val resultRefuse: LiveData<Resource<String>>
        get() = _resultRefuse

    private val _resultCancelRequest = MutableLiveData<Resource<String>>()
    val resultCancelRequest: LiveData<Resource<String>>
        get() = _resultCancelRequest

    init {
        Log.d(Constants.LOG_TAG, "allUserFragmentViewModel: getUserList")
    }


    fun acceptFriend(user: UserFirebase){
        viewModelScope.launch {
            friendUseCase.invokeAcceptFriend(user).collect{
                when (it) {
                    is State.Loading -> {
                        _resultAccept.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _resultAccept.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _resultAccept.postValue(
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

    fun refuseFriend(user: UserFirebase){
        viewModelScope.launch {
            friendUseCase.invokeRefuseFriend(user).collect{
                when (it) {
                    is State.Loading -> {
                        _resultRefuse.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _resultRefuse.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _resultRefuse.postValue(
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

    fun cancelFriendRequest(user: UserFirebase){
        viewModelScope.launch {
            friendUseCase.invokeCancelFriendRequest(user).collect{
                when (it) {
                    is State.Loading -> {
                        _resultCancelRequest.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _resultCancelRequest.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _resultCancelRequest.postValue(
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

    fun getRequestListByListId(list: MutableList<String>){
        viewModelScope.launch {
            getUserUseCase.invokeGetUserListByListId(list).collect{
                when (it) {
                    is State.Loading -> {
                        _listRequest.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _listRequest.postValue(Resource.success(it.data?.let { it1 -> sortedList(it1.toMutableList()) }))
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

    fun getSentListByListId(list: MutableList<String>){
        viewModelScope.launch {
            getUserUseCase.invokeGetUserListByListId(list).collect{
                when (it) {
                    is State.Loading -> {
                        _listSent.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _listSent.postValue(Resource.success(it.data?.let { it1 -> sortedList(it1.toMutableList()) }))
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

    private fun sortedList(list: MutableList<UserFirebase>): MutableList<UserFirebase>{

        val sortedDataList = list.sortedBy {
            it.username.split(" ").last()
        }
        return sortedDataList as MutableList<UserFirebase>
    }

}