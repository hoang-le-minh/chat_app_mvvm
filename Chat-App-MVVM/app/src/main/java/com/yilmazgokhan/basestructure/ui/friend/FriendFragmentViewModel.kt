package com.yilmazgokhan.basestructure.ui.friend

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import com.blankj.utilcode.util.LogUtils
import com.yilmazgokhan.basestructure.base.BaseViewModel
import com.yilmazgokhan.basestructure.di.qualifier.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * TODO("The class will delete when [FriendFragment] deleted")
 *
 * The class created for FriendFragment
 */
@ExperimentalCoroutinesApi
class FriendFragmentViewModel @ViewModelInject constructor(
        @Assisted private val savedStateHandle: SavedStateHandle,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    init {
        LogUtils.d("$this initialized")
    }
}