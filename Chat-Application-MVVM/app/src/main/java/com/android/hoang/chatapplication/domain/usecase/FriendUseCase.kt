package com.android.hoang.chatapplication.domain.usecase

import android.annotation.SuppressLint
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.domain.repository.FriendRepository
import com.android.hoang.chatapplication.domain.repository.UserRepository
import com.android.hoang.chatapplication.util.State
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FriendUseCase @Inject constructor(private val friendRepository: FriendRepository) {
    suspend fun invokeAddFriend(userId: String): Flow<State<String>> = flow {
        try {
            emit(State.Loading())
            val result = friendRepository.addFriend(userId)
            if (result == StringUtils.getString(R.string.ok))
                emit(State.Success(result))
            else
                emit(State.Error(StringUtils.getString(R.string.add_friend_failed)))
        } catch (e: Exception) {
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }

    suspend fun invokeAcceptFriend(userId: String): Flow<State<String>> = flow {
        try {
            emit(State.Loading())
            val result = friendRepository.acceptFriend(userId)
            if (result == StringUtils.getString(R.string.ok))
                emit(State.Success(result))
            else
                emit(State.Error(StringUtils.getString(R.string.add_friend_failed)))
        } catch (e: Exception) {
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }

    suspend fun invokeRefuseFriend(user: UserFirebase): Flow<State<String>> = flow {
        try {
            emit(State.Loading())
            val result = friendRepository.refuseFriend(user)
            if (result == StringUtils.getString(R.string.ok))
                emit(State.Success(result))
            else
                emit(State.Error(StringUtils.getString(R.string.refuse_failed)))
        } catch (e: Exception) {
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }

    suspend fun invokeCancelFriendRequest(userId: String): Flow<State<String>> = flow {
        try {
            emit(State.Loading())
            val result = friendRepository.cancelFriendRequest(userId)
            if (result == StringUtils.getString(R.string.ok))
                emit(State.Success(result))
            else
                emit(State.Error(StringUtils.getString(R.string.cancel_failed)))
        } catch (e: Exception) {
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }

    suspend fun invokeUnfriend(userId: String): Flow<State<String>> = flow {
        try {
            emit(State.Loading())
            val result = friendRepository.unfriend(userId)
            if (result == StringUtils.getString(R.string.ok))
                emit(State.Success(result))
            else
                emit(State.Error(StringUtils.getString(R.string.cancel_failed)))
        } catch (e: Exception) {
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }

    @SuppressLint("SuspiciousIndentation")
    suspend fun invokeGetListSentFriend(): Flow<State<MutableList<String>>> = flow {
        try {
            emit(State.Loading())
            val result = friendRepository.getListSentFriend()
//            if (result.isNotEmpty())
                emit(State.Success(result))
//            else
//                emit(State.Error(StringUtils.getString(R.string.something_went_wrong)))
        } catch (e: Exception) {
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }

    @SuppressLint("SuspiciousIndentation")
    suspend fun invokeGetListRequestFriend(): Flow<State<MutableList<String>>> = flow {
        try {
            emit(State.Loading())
            val result = friendRepository.getListRequestFriend()
//            if (result.isNotEmpty())
                emit(State.Success(result))
//            else
//                emit(State.Error(StringUtils.getString(R.string.something_went_wrong)))
        } catch (e: Exception) {
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }

    suspend fun invokeGetListFriend(): Flow<State<MutableList<String>>> = flow {
        try {
            emit(State.Loading())
            val result = friendRepository.getListFriend()
            if (result.isNotEmpty())
                emit(State.Success(result))
            else
                emit(State.Error(StringUtils.getString(R.string.something_went_wrong)))
        } catch (e: Exception) {
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }

    suspend fun invokeCheckExistFriend(userId: String): Flow<State<String>> = flow {
        try {
            emit(State.Loading())
            val result = friendRepository.checkExistFriend(userId)
            if (result == StringUtils.getString(R.string.exist) || result == StringUtils.getString(R.string.not_exist))
                emit(State.Success(result))
            else
                emit(State.Error(StringUtils.getString(R.string.something_went_wrong)))
        } catch (e: Exception) {
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }

    suspend fun invokeCheckRelationship(userId: String): Flow<State<Int>> = flow {
        try {
            emit(State.Loading())
            val result = friendRepository.checkRelationship(userId)
            emit(State.Success(result))
        } catch (e: Exception) {
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }
}