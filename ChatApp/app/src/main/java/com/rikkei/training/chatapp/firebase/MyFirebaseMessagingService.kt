package com.rikkei.training.chatapp.firebase

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rikkei.training.chatapp.CHANNEL_ID
import com.rikkei.training.chatapp.MainActivity
import com.rikkei.training.chatapp.R
import com.rikkei.training.chatapp.TAG

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("hoangminhdh11", "onMessageReceived: ")
        Log.d(TAG, "onMessageReceived: ${message.from}")
        val notification = message.notification
        if(notification != null){
            val strTitle = notification.title
            val strMessage = notification.body
            Log.d("hoangminhdh11", "onMessageReceived: $strTitle -> $strMessage")
            sendNotification(strTitle, strMessage)
        }


    }

    private fun sendNotification(strTitle: String?, strMessage: String?) {

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        @SuppressLint("UnspecifiedImmutableFlag")
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(strTitle)
            .setContentText(strMessage)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notification = notificationBuilder.build()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(1, notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d(TAG, token )
    }
}