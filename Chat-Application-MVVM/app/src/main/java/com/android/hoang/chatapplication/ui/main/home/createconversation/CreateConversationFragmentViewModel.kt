package com.android.hoang.chatapplication.ui.main.home.createconversation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseViewModel
import com.android.hoang.chatapplication.data.remote.model.Message
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.di.qualifier.IoDispatcher
import com.android.hoang.chatapplication.domain.usecase.GetUserUseCase
import com.android.hoang.chatapplication.util.Resource
import com.android.hoang.chatapplication.util.State
import com.blankj.utilcode.util.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateConversationFragmentViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val userUseCase: GetUserUseCase
) : BaseViewModel() {

    private val _userListNotYetChat = MutableLiveData<Resource<MutableList<UserFirebase>>>()
    val userListNotYetChat: LiveData<Resource<MutableList<UserFirebase>>>
     get() = _userListNotYetChat

    private val _userSelected = MutableLiveData<UserFirebase?>()
    val userSelected: LiveData<UserFirebase?>
        get() = _userSelected

    fun getUserListNotYetChat(listId: List<String>){
        viewModelScope.launch(ioDispatcher) {
            userUseCase.invokeGetUserListOutsideListId(listId).collect{
                when (it) {
                    is State.Loading -> {
                        _userListNotYetChat.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        val sortList = it.data?.sortedBy { user -> user.username.split(" ").last() } as MutableList<UserFirebase>
                        _userListNotYetChat.postValue(Resource.success(sortList))
                    }
                    is State.Error -> {
                        _userListNotYetChat.postValue(
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

    fun setUserSelected(user: UserFirebase?){
        _userSelected.postValue(user)
    }

}