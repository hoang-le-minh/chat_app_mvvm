package com.android.hoang.chatapplication.domain.usecase

import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.data.remote.model.PushNotification
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.domain.repository.NotificationRepository
import com.android.hoang.chatapplication.util.State
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NotificationUseCase @Inject constructor(private val notificationRepository: NotificationRepository) {
    suspend fun invokeSendNotification(notification: PushNotification): Flow<State<String>> = flow {
        try {
            emit(State.Loading())
            notificationRepository.sendNotification(notification)
            emit(State.Success(StringUtils.getString(R.string.ok)))
        } catch (e: Exception) {
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }
}