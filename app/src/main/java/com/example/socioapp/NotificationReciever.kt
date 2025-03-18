package com.example.socioapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.socioapp.PlayerActivity.Companion.binding
import com.example.socioapp.PlayerActivity.Companion.musicService
import com.example.socioapp.PlayerActivity.Companion.musiclistPA
import com.example.socioapp.PlayerActivity.Companion.songposition

class NotificationReciever : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p1==null) return
        when(p1.action){
            MusicApplication.PREV-> prevNext(false,p0!!)
            MusicApplication.PLAY-> if (PlayerActivity.isplay) pauseMusic()  else playMusic()
            MusicApplication.NEXT-> prevNext(true,p0!!)
            MusicApplication.EXIT-> {
                exitActivity()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun playMusic(){
        if (musicService == null || musicService?.mediaPlayer == null) return
        PlayerActivity.isplay=true
        musicService!!.mediaPlayer!!.start()
        musicService!!.showNotification(R.drawable.pause_icon,1F)
        binding.playpause.setImageResource(R.drawable.pause_icon)
        NowPlaying.binding.playpausebtn.setImageResource(R.drawable.pause_icon)

    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun pauseMusic(){
        if (musicService == null || musicService?.mediaPlayer == null) return
        PlayerActivity.isplay=false
        musicService!!.mediaPlayer!!.pause()
        musicService!!.showNotification(R.drawable.play_icon,0F)
        binding.playpause.setImageResource(R.drawable.play_icon)
        NowPlaying.binding.playpausebtn.setImageResource(R.drawable.play_icon)

    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun prevNext(increment:Boolean, context:Context){
        if (musicService == null) return
        setpos(increment)
        musicService!!.playMedia()
        if (musiclistPA.isNotEmpty() && songposition < musiclistPA.size) {
            Glide.with(context).load(musiclistPA[songposition].arturi)
                .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
                .into(binding.songingPA)
            binding.titlePA.text = musiclistPA[songposition].title
            Glide.with(context).load(musiclistPA[songposition].arturi)
                .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
                .into(NowPlaying.binding.songimg)
            NowPlaying.binding.songn.text = musiclistPA[songposition].title
            playMusic()
            PlayerActivity.fIndex = favoriteCheck(musiclistPA[songposition].id)
            if (PlayerActivity.isfav) PlayerActivity.binding.favbtn.setImageResource(R.drawable.baseline_favorite_24)
            else PlayerActivity.binding.favbtn.setImageResource(R.drawable.baseline_favorite_empty)
        }
    }
}