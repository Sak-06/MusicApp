package com.example.socioapp

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.socioapp.databinding.ActivityPlaylistBinding
import com.example.socioapp.databinding.AddPlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistActivity : AppCompatActivity() {
    private lateinit var binding :ActivityPlaylistBinding
    private lateinit var playlistadapter: PlaylistAdapter
    companion object{
        var musicPlaylist:MusicPlaylist=MusicPlaylist()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currTheme[MainActivity.themeId])
        //setContentView(R.layout.activity_playlist)
        binding=ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        val tempList=ArrayList<String>()
//        tempList.add("Playlist 1")
//        tempList.add("Playlist 2")
//        tempList.add("Playlist 3")
//        tempList.add("Playlist 4")
        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(10)
        binding.playlistRV.layoutManager = GridLayoutManager(this,2)
        playlistadapter = PlaylistAdapter(this, playlistList = musicPlaylist.ref)
        binding.playlistRV.adapter = playlistadapter
        binding.playlistback.setOnClickListener{
            finish()
            //startActivity(Intent(this@PlaylistActivity,MainActivity::class.java))
        }
        binding.playlistadd.setOnClickListener { customAlertDialogue() }


    }
    private fun customAlertDialogue(){
        val customDiag=LayoutInflater.from(this).inflate(R.layout.add_playlist, binding.root,false)
        val binder= AddPlaylistBinding.bind(customDiag)

        val builder=MaterialAlertDialogBuilder(this)
        builder.setView(customDiag)
            .setTitle("Playlist Details")
            .setPositiveButton("ADD"){dialogue,_ ->
                val playlistNme=binder.playlistlistname.text
                val createdBy=binder.username.text
                if(playlistNme !=null && createdBy !=null)
                    if(playlistNme.isNotEmpty() && createdBy.isNotEmpty()){
                        addplaylist(playlistNme.toString(),createdBy.toString())
                    }
                dialogue.dismiss()
            }.show()
    }
    private fun addplaylist(name: String,createdBy: String){
        var playlistExist=false
        for(i in musicPlaylist.ref){
            if(name.equals(i.name)){
                playlistExist=true
                break
            }
        }
        if(playlistExist) Toast.makeText(this,"Playlist Exists",Toast.LENGTH_SHORT).show()
        else{
            val templ=Playlist()
            templ.name=name
            templ.playlist=ArrayList()
            templ.createdBy=createdBy
            val calendar=java.util.Calendar.getInstance().time
            val sdf= SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            templ.createdOn=sdf.format(calendar)
            musicPlaylist.ref.add(templ)
            playlistadapter.refreshPlaylist()
        }
    }

    override fun onResume() {
        super.onResume()
        playlistadapter.notifyDataSetChanged()
    }
}