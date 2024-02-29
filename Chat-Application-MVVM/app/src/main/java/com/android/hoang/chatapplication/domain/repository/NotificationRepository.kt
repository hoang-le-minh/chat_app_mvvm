package com.android.hoang.chatapplication.domain.repository

import com.android.hoang.chatapplication.data.remote.model.PushNotification
import com.android.hoang.chatapplication.data.remote.service.NotificationApi

interface NotificationRepository {
    suspend fun sendNotification(notification: PushNotification)
}