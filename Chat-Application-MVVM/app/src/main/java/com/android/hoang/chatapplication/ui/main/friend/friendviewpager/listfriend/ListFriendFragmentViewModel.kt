package com.android.hoang.chatapplication.ui.main.friend.friendviewpager.listfriend

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
    private val _listIdFriend = MutableLiveData<Resource<MutableList<String>>>()
    val listIdFriend: LiveData<Resource<MutableList<String>>>
        get() = _listIdFriend

    private val _listFriend = MutableLiveData<Resource<List<Any>>>()
    val listFriend: LiveData<Resource<List<Any>>>
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

    fun getUserListByListId(list: MutableList<String>){
        viewModelScope.launch {
            getUserUseCase.invokeGetUserListByListId(list).collect{
                when (it) {
                    is State.Loading -> {
                        _listFriend.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        val mList = it.data?.let { it1 ->
                            sortAlphabet(
                                it1.toMutableList()
                            )
                        }
                        _listFriend.postValue(Resource.success(mList))
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

    private fun sortAlphabet(friendList: MutableList<UserFirebase>): MutableList<Any>{
        val list = mutableListOf<Any>()
        for (user: UserFirebase in friendList){
            list.add(user)
            val ch = user.username.split(" ").last().substring(0,1).uppercase()
            if(list.none{ it is String && it == ch }){
                list.add(ch)
            }
        }

        val sortedDataList = list.sortedBy {
            if (it is String) it
            else (it as UserFirebase).username.split(" ").last()
        }

        return sortedDataList as MutableList<Any>
    }

}