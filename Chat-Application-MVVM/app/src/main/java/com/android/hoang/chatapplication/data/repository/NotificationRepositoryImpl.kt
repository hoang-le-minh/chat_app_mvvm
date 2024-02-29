package com.android.hoang.chatapplication.data.repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.android.hoang.chatapplication.data.remote.model.PushNotification
import com.android.hoang.chatapplication.data.remote.service.NotificationApi
import com.android.hoang.chatapplication.domain.repository.NotificationRepository
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(private val notificationApi: NotificationApi): NotificationRepository {

    override suspend fun sendNotification(notification: PushNotification) {
        try {
            notificationApi.postNotification(notification)
        } catch (e: Exception){
            Log.d(LOG_TAG, "sendNotification: Failed")
        }
    }
}