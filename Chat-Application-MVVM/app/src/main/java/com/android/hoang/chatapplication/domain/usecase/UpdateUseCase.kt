package com.android.hoang.chatapplication.domain.usecase

import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.domain.repository.UserRepository
import com.android.hoang.chatapplication.util.State
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend fun invokeUpdateUser(name: String, imageUrl: String, phoneNumber: String, dateOfBirth: String): Flow<State<UserFirebase>> = flow {
        try {
            emit(State.Loading())
            val user = userRepository.updateUser(name, imageUrl, phoneNumber, dateOfBirth)
            if (user != null)
                emit(State.Success(user))
            else
                emit(State.Error(StringUtils.getString(R.string.update_failed)))
        } catch (e: Exception) {
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }
}