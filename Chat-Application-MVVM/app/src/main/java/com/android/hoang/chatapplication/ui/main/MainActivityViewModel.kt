package com.android.hoang.chatapplication.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.hoang.chatapplication.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(): BaseViewModel() {


    private val _updateProfileStatus = MutableLiveData(false)
    val updateProfileStatus: LiveData<Boolean>
        get() = _updateProfileStatus

    fun updateProfileStatus(status: Boolean){
        _updateProfileStatus.postValue(status)
    }

}