package com.android.hoang.chatapplication.data.remote.model

data class PushNotification(
    var data: NotificationData,
    var to: String
) {
}

data class NotificationData(var title: String, var messager: String) {
}