package com.android.hoang.chatapplication.domain.usecase

import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.domain.repository.UserRepository
import com.android.hoang.chatapplication.util.State
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend fun invokeSignUp(email: String, password: String): Flow<State<String>> = flow {
        try {
            emit(State.Loading())
            val result = userRepository.signUp(email, password)
            if (result == StringUtils.getString(R.string.ok))
                emit(State.Success(result))
            else
                emit(State.Error(result))
        } catch (e: Exception) {
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }

    suspend fun invokeSignIn(email: String, password: String): Flow<State<String>> = flow {
        try {
            emit(State.Loading())
            val result = userRepository.signIn(email, password)
            if (result == StringUtils.getString(R.string.ok))
                emit(State.Success(result))
            else
                emit(State.Error(result))
        } catch (e: Exception) {
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }
}