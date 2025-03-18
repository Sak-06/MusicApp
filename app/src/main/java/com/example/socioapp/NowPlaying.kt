package com.example.socioapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.socioapp.PlayerActivity.Companion.musicService
import com.example.socioapp.PlayerActivity.Companion.musiclistPA
import com.example.socioapp.PlayerActivity.Companion.songposition
import com.example.socioapp.databinding.FragmentNowPlayingBinding


class NowPlaying : Fragment() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentNowPlayingBinding
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireContext().theme.applyStyle(MainActivity.currTheme[MainActivity.themeId],true)
        val view=inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding= FragmentNowPlayingBinding.bind(view)
        binding.root.visibility=View.INVISIBLE
        binding.playpausebtn.setOnClickListener{
            if(PlayerActivity.isplay) pauseMusic()
            else playMusic()
        }
        binding.prevbtn.setOnClickListener {
            setpos(increment = false)
            musicService!!.playMedia()
            Glide.with(this).load(musiclistPA[songposition].arturi)
                .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
                .into(binding.songimg)
            binding.songn.text= musiclistPA[songposition].title
            musicService!!.showNotification(R.drawable.pause_icon,0F)
            playMusic()
        }
        binding.nextbtn.setOnClickListener {
            setpos(true)
            musicService!!.playMedia()
            Glide.with(this).load(musiclistPA[songposition].arturi)
                .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
                .into(binding.songimg)
            binding.songn.text= musiclistPA[songposition].title
            musicService!!.showNotification(R.drawable.pause_icon,0F)
            playMusic()
        }
        binding.root.setOnClickListener {
            val intent = Intent(requireContext(),PlayerActivity::class.java)
            intent.putExtra("index",PlayerActivity.songposition)
            intent.putExtra("class","NowPlaying")
            ContextCompat.startActivity(requireContext(),intent,null)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if(PlayerActivity.musicService!=null){
            binding.root.visibility=View.VISIBLE
            binding.songn.isSelected=true
            Glide.with(this).load(musiclistPA[songposition].arturi)
                .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
                .into(binding.songimg)
            binding.songn.text= musiclistPA[songposition].title
            if(PlayerActivity.isplay) binding.playpausebtn.setImageResource(R.drawable.pause_icon)
            else binding.playpausebtn.setImageResource(R.drawable.play_icon,)
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun playMusic(){
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        binding.playpausebtn.setImageResource(R.drawable.pause_icon)
        PlayerActivity.binding.playpause.setImageResource(R.drawable.pause_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon,0F)
        PlayerActivity.isplay=true
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun pauseMusic(){
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        binding.playpausebtn.setImageResource(R.drawable.play_icon)
        PlayerActivity.binding.playpause.setImageResource(R.drawable.play_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon,1F)
        PlayerActivity.isplay=false
    }

}