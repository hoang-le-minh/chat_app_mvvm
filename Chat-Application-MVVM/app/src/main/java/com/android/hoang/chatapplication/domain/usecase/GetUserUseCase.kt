package com.android.hoang.chatapplication.domain.usecase

import android.util.Log
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.data.remote.model.UserResponse
import com.android.hoang.chatapplication.domain.repository.UserRepository
import com.android.hoang.chatapplication.util.State
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val userRepository: UserRepository) {

    suspend fun invokeCurrentUser(): Flow<State<UserFirebase>> = flow {
        try {
            emit(State.Loading())
            val currentUser = userRepository.getCurrentUser()
            if (currentUser != null)
                emit(State.Success(currentUser))
            else
                emit(State.Error(StringUtils.getString(R.string.cannot_get_current_user)))
        } catch (e: Exception) {
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }

    suspend fun invokeAllUser(): Flow<State<List<UserFirebase>>> = flow {
        try {
            emit(State.Loading())
            val userList = userRepository.getAllUser()
            if(userList.isNotEmpty()){
                emit(State.Success(userList))
            } else
                emit(State.Error(StringUtils.getString(R.string.cannot_get_all_user)))
        } catch (e: Exception){
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }
}