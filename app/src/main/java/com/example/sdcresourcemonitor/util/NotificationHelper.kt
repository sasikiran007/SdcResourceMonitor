package com.example.sdcresourcemonitor.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.example.sdcresourcemonitor.R
import com.example.sdcresourcemonitor.view.MainActivity

class NotificationHelper(val context : Context) {
    private val CHANNEL_ID = "Critical Alerts"
    private val NOTIFICATION_ID = 1211

    fun createNotification() {
        createNotificationChannel()
        val intent = Intent(context,MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context,0,intent,0)
        val icon  = BitmapFactory.decodeResource(context.resources, R.drawable.icon)
        val notification = NotificationCompat.Builder(context,CHANNEL_ID)
            .setContentTitle("Critical alerts")
            .setSmallIcon(R.drawable.icon)
            .setContentText("New alerts")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID,notification)
    }


    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: String = CHANNEL_ID
            val descriptionText : String = "SRM Alerts"
            val importance : Int = NotificationManager.IMPORTANCE_DEFAULT
            val channel  = NotificationChannel(CHANNEL_ID,name,importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}