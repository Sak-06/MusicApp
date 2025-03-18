package com.example.socioapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.socioapp.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class PlaylistDetails : AppCompatActivity() {
    lateinit var binding:ActivityPlaylistDetailsBinding
    lateinit var adapter:Musicadapter
    companion object{
        var currentPlaylistPos:Int=-1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currTheme[MainActivity.themeId])
        binding=ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentPlaylistPos=intent.extras?.get("index") as Int
        PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist= checkPlaylist(PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist)
        binding.playlistdetailRV.setItemViewCacheSize(10)
        binding.playlistdetailRV.setHasFixedSize(true)
        binding.playlistdetailRV.layoutManager=LinearLayoutManager(this)
        adapter=Musicadapter(this,PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist,playlistDetails = true)
        binding.playlistdetailRV.adapter=adapter
        binding.backPD.setOnClickListener{
            finish()
            //startActivity(Intent(this@PlaylistActivity,MainActivity::class.java))
        }
        binding.playlistshuffle.setOnClickListener {
            val intent= Intent(this, PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","PlaylistDetailShuffle")
            startActivity(intent)
        }
        binding.addbtnPD.setOnClickListener {
            val intent=Intent(this,SongSelection::class.java)
            startActivity(intent)
        }
        binding.removebtnPD.setOnClickListener {
            val builder= MaterialAlertDialogBuilder(this)
            builder.setTitle("REMOVE")
                .setMessage("Want to remove all songs?")
                .setPositiveButton("YES"){dialogue,_ ->
                    PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.clear()
                    adapter.refresh()
                    dialogue.dismiss()

                }
                .setNegativeButton("NO") { dialogue, _ ->
                    dialogue.dismiss()
                }
            val customdialogue = builder.create()
            customdialogue.show()
            customdialogue.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
            customdialogue.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE)

        }
        binding.backPD.setOnClickListener { finish() }

    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.playlistNamePD.text=PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].name
        binding.playlistinfo.text= "Total ${adapter.itemCount} Songs.\n\n" +
                "Created On:\n${PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].createdOn}\n\n"+
                "~ ${PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].createdBy}"
        if(adapter.itemCount>0){
            Glide.with(this).load(PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist[0].arturi)
                .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
                .into(binding.playlistimg)
            binding.playlistshuffle.visibility= View.VISIBLE
        }
        adapter.notifyDataSetChanged()
        val editor = getSharedPreferences("Favorite", MODE_PRIVATE).edit()
        val jsonStr = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStr)
        editor.apply()

    }
}