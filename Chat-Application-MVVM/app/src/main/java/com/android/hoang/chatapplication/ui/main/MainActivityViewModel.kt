package com.android.hoang.chatapplication.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.hoang.chatapplication.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(): BaseViewModel() {

    private val _requestCount = MutableLiveData(0)
    val requestCount: LiveData<Int>
        get() = _requestCount

    private val _updateRequestCount = MutableLiveData(false)
    val updateRequestCount: LiveData<Boolean>
        get() = _updateRequestCount

    private val _updateProfileStatus = MutableLiveData(false)
    val updateProfileStatus: LiveData<Boolean>
        get() = _updateProfileStatus

    private val _updateListFriendStatus = MutableLiveData(false)
    val updateListFriendStatus: LiveData<Boolean>
        get() = _updateListFriendStatus

    fun updateProfileStatus(status: Boolean){
        _updateProfileStatus.postValue(status)
    }

    private val _updateAllUserStatus = MutableLiveData(false)
    val updateAllUserStatus: LiveData<Boolean>
        get() = _updateAllUserStatus

    private val _updateRequestStatus = MutableLiveData(false)
    val updateRequestStatus: LiveData<Boolean>
        get() = _updateRequestStatus

    fun updateRequestCount(status: Boolean){
        _updateRequestStatus.postValue(status)
    }

    fun updateAllUserStatus(status: Boolean){
        _updateAllUserStatus.postValue(status)
    }
    fun updateListFriendStatus(status: Boolean){
        _updateListFriendStatus.postValue(status)
    }

    fun updateRequestStatus(status: Boolean){
        _updateRequestStatus.postValue(status)
    }

    fun setRequestCount(count: Int){
        _requestCount.postValue(count)
    }
}