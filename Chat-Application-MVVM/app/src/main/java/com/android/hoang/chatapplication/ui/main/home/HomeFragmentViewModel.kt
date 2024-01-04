package com.android.hoang.chatapplication.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseViewModel
import com.android.hoang.chatapplication.data.remote.model.UserResponse
import com.android.hoang.chatapplication.di.qualifier.IoDispatcher
import com.android.hoang.chatapplication.domain.usecase.GetUserUseCase
import com.android.hoang.chatapplication.util.Resource
import com.android.hoang.chatapplication.util.State
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val getUserUseCase: GetUserUseCase
    //private val userRepository: UserRepository
) : BaseViewModel() {

    //region city info
    private val _user = MutableLiveData<Resource<UserResponse>>()
    val user: LiveData<Resource<UserResponse>>
        get() = _user
    //endregion

    init {
        LogUtils.d("$this initialize")
        getUser("hoang-le-minh")
    }

    val listUserTest = mutableListOf(
        UserTest("Minh Hoang", "16:11", "This is a message", R.drawable.avt_default, false, 0),
        UserTest("Minh Minh", "16:11", "This is a message", R.drawable.avt_default, true, 2),
        UserTest("Hoang Hoang", "16:11", "This is a message", R.drawable.avt_default, false, 0),
        UserTest("Thien An", "16:11", "This is a message", R.drawable.avt_default, true, 3),
        UserTest("Duc Nguyen", "16:11", "This is a message", R.drawable.avt_default, false, 0),
        UserTest("Minh Hoang", "16:11", "This is a message", R.drawable.avt_default, false, 0),
        UserTest("Minh Minh", "16:11", "This is a message", R.drawable.avt_default, true, 2),
        UserTest("Hoang Hoang", "16:11", "This is a message", R.drawable.avt_default, false, 0),
        UserTest("Thien An", "16:11", "This is a message", R.drawable.avt_default, true, 3),
        UserTest("Duc Nguyen", "16:11", "This is a message", R.drawable.avt_default, false, 0)
    )

    /**
     * Send HTTP Request for get user info
     */
    private fun getUser(username: String) {
        viewModelScope.launch {
            getUserUseCase.invoke(username).collect {
                when (it) {
                    is State.Loading -> {
                        _user.postValue(Resource.loading())
                    }
                    is State.Success -> {
                        _user.postValue(Resource.success(it.data))
                    }
                    is State.Error -> {
                        _user.postValue(
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