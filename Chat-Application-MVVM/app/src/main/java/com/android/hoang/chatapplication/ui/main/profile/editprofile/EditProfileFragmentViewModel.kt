package com.android.hoang.chatapplication.ui.main.profile.editprofile

import com.android.hoang.chatapplication.base.BaseViewModel
import com.android.hoang.chatapplication.di.qualifier.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class EditProfileFragmentViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher

): BaseViewModel() {


}