package com.rikkei.training.chatapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

const val CHANNEL_ID = "channel_id"
const val TAG = "hoangminhdh11"
const val REQUEST_PERMISSION_CODE = 111
class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()
//        FirebaseApp.initializeApp(this);
        createChannelNotification()
    }

    private fun createChannelNotification() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}