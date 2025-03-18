package com.example.socioapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MusicApplication:Application() {
    companion object{
        const val CHANNELID="channel1"
        const val PLAY="play"
        const val NEXT="next"
        const val PREV="prev"
        const val EXIT="exit"
    }
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(CHANNELID,"Now playing song",
                NotificationManager.IMPORTANCE_HIGH,)
            notificationChannel.description="important channel"
            val notificationManager=getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}