package com.android.hoang.chatapplication.ui.userinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseViewModel
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.di.qualifier.IoDispatcher
import com.android.hoang.chatapplication.domain.usecase.FriendUseCase
import com.android.hoang.chatapplication.domain.usecase.GetUserUseCase
import com.android.hoang.chatapplication.domain.usecase.MessageUseCase
import com.android.hoang.chatapplication.util.Resource
import com.android.hoang.chatapplication.util.State
import com.blankj.utilcode.util.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoActivityViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val getUserUseCase: GetUserUseCase,
    private val messageUseCase: MessageUseCase,
    private val friendUseCase: FriendUseCase
) : BaseViewModel() {
    // result get user by id
    private val _result = MutableLiveData<Resource<UserFirebase>>()
    val result: LiveData<Resource<UserFirebase>>
        get() = _result

    private val _resultDelete = MutableLiveData<Resource<String>>()
    val resultDelete: LiveData<Resource<String>>
        get() = _resultDelete

    private val _resultRelationship = MutableLiveData<Resource<Int>>()
    val resultRelationship: LiveData<Resource<Int>>
        get() = _resultRelationship

    fun getUserInfoById(userId: String){
        viewModelScope.launch(ioDispatcher){
            getUserUseCase.invokeGetUserById(userId).collect{
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

    fun deleteConversation(userId: String){
        viewModelScope.launch(ioDispatcher) {
            messageUseCase.invokeDeleteConversation(userId).collect{
                when (it) {
                    is State.Loading -> {
                        _resultDelete.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _resultDelete.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _resultDelete.postValue(
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

    fun getRelationship(userId: String){
        viewModelScope.launch(ioDispatcher) {
            friendUseCase.invokeCheckRelationship(userId).collect{
                when (it) {
                    is State.Loading -> {
                        _resultRelationship.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _resultRelationship.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _resultRelationship.postValue(
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

    fun unfriend(userId: String){
        viewModelScope.launch(ioDispatcher) {
            friendUseCase.invokeUnfriend(userId).collect{
                when (it) {
                    is State.Loading -> {

                    }
                    is State.Success -> {

                    }
                    is State.Error -> {

                    }
                }
            }
        }
    }

    fun cancelFriendRequest(userId: String){
        viewModelScope.launch(ioDispatcher) {
            friendUseCase.invokeCancelFriendRequest(userId).collect{
                when (it) {
                    is State.Loading -> {

                    }
                    is State.Success -> {

                    }
                    is State.Error -> {

                    }
                }
            }
        }
    }

    fun acceptFriend(userId: String){
        viewModelScope.launch(ioDispatcher) {
            friendUseCase.invokeAcceptFriend(userId).collect{
                when (it) {
                    is State.Loading -> {

                    }
                    is State.Success -> {

                    }
                    is State.Error -> {

                    }
                }
            }
        }
    }

    fun addFriend(userId: String){
        viewModelScope.launch(ioDispatcher) {
            friendUseCase.invokeAddFriend(userId).collect{
                when (it) {
                    is State.Loading -> {

                    }
                    is State.Success -> {

                    }
                    is State.Error -> {

                    }
                }
            }
        }
    }
}