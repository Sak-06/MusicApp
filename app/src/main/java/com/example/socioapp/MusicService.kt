package com.example.socioapp

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.socioapp.PlayerActivity.Companion.binding
import com.example.socioapp.PlayerActivity.Companion.isplay
import com.example.socioapp.PlayerActivity.Companion.musicService
import com.example.socioapp.PlayerActivity.Companion.musiclistPA
import com.example.socioapp.PlayerActivity.Companion.nowPlayingId
import com.example.socioapp.PlayerActivity.Companion.songposition

class MusicService: Service(), AudioManager.OnAudioFocusChangeListener{
    private var myBinder= MyBinder()
    var mediaPlayer:MediaPlayer?=null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager
    override fun onBind(p0: Intent?): IBinder? {
        mediaSession = MediaSessionCompat(baseContext,"My music")
        return myBinder
    }
    inner class MyBinder:Binder(){
        fun currentService():MusicService{
            return this@MusicService
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("ForegroundServiceType")
    fun showNotification(playpaausebtn:Int,playBackSpeed:Float){
        val intent = Intent(baseContext,MainActivity::class.java)
        val comtextIntent=PendingIntent.getActivity(baseContext,0,intent,PendingIntent.FLAG_IMMUTABLE)

        val prevIntent=Intent(baseContext,NotificationReciever::class.java).setAction(MusicApplication.PREV)
        val prevPending= PendingIntent.getBroadcast(baseContext,0,prevIntent,PendingIntent.FLAG_IMMUTABLE)

        val playIntent=Intent(baseContext,NotificationReciever::class.java).setAction(MusicApplication.PLAY)
        val playPending= PendingIntent.getBroadcast(baseContext,0,playIntent,PendingIntent.FLAG_IMMUTABLE)

        val nextIntent=Intent(baseContext,NotificationReciever::class.java).setAction(MusicApplication.NEXT)
        val nextPending= PendingIntent.getBroadcast(baseContext,0,nextIntent,PendingIntent.FLAG_IMMUTABLE)

        val exitIntent=Intent(baseContext,NotificationReciever::class.java).setAction(MusicApplication.EXIT)
        val exitPending= PendingIntent.getBroadcast(baseContext,0,exitIntent,PendingIntent.FLAG_IMMUTABLE)

        val imgArt= getImageArt(PlayerActivity.musiclistPA[PlayerActivity.songposition].path)
        val image= if(imgArt!=null){
            BitmapFactory.decodeByteArray(imgArt,0,imgArt.size)
        }
        else{
            BitmapFactory.decodeResource(resources,R.drawable.splash_screen)
        }
        val notification=NotificationCompat.Builder(baseContext,MusicApplication.CHANNELID)
            .setContentIntent(comtextIntent)
            .setContentTitle(PlayerActivity.musiclistPA[PlayerActivity.songposition].title)
            .setContentText(PlayerActivity.musiclistPA[PlayerActivity.songposition].artist)
            .setSmallIcon(R.drawable.music_icon)
            .setLargeIcon(image)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(1, 2, 3) // Indexes
                .setMediaSession(mediaSession.sessionToken))
//            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.previous_icon,"previous",prevPending)
            .addAction(playpaausebtn,"play",playPending)
            .addAction(R.drawable.next_icon,"next",nextPending)
            .addAction(R.drawable.exit_icon,"exit",exitPending)
            .build()
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.Q){
            mediaSession.setMetadata(MediaMetadataCompat.Builder()
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer!!.duration.toLong())
                .build())
            mediaSession.setPlaybackState(PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING,mediaPlayer!!.currentPosition.toLong(),playBackSpeed)
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build())
        }
            startForeground(10, notification)

    }
     @RequiresApi(Build.VERSION_CODES.Q)
     fun playMedia(){
        try {
            if (musicService!!.mediaPlayer== null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musiclistPA[songposition].path)
            musicService!!.mediaPlayer!!.prepare()
            binding.playpause.setImageResource(R.drawable.pause_icon)
            musicService!!.showNotification(R.drawable.pause_icon,0F)
            binding.seekbarStart.text= formduration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.seekbarEnd.text= formduration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekbarPA.progress=0
            binding.seekbarPA.max= musicService!!.mediaPlayer!!.duration
            nowPlayingId = musiclistPA[songposition].id

        }
        catch(err:Exception){return}
    }
    fun seekbarSetup(){
        runnable= Runnable {
            binding.seekbarStart.text= formduration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.seekbarPA.progress= musicService!!.mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onAudioFocusChange(p0: Int) {
        if(p0<=0){
            //pause music
            binding.playpause.setImageResource(R.drawable.play_icon)
            NowPlaying.binding.playpausebtn.setImageResource(R.drawable.play_icon)
            musicService!!.showNotification(R.drawable.play_icon,1F)
            isplay =false
            musicService!!.mediaPlayer!!.pause()
        }
        else{
            //play music
            binding.playpause.setImageResource(R.drawable.pause_icon)
            NowPlaying.binding.playpausebtn.setImageResource(R.drawable.pause_icon)
            musicService!!.showNotification(R.drawable.pause_icon,1F)
            isplay =true
            musicService!!.mediaPlayer!!.start()
        }
    }
}

