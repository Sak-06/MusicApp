package com.example.socioapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.socioapp.Musicadapter.Myadapter
import com.example.socioapp.databinding.MusicViewBinding

class Musicadapter(private val context: Context, private var musiclist : ArrayList<Music>,private val playlistDetails:Boolean=false,private val selectionActivity: Boolean=false) : RecyclerView.Adapter<Myadapter>() {
    class Myadapter(binding: MusicViewBinding):RecyclerView.ViewHolder(binding.root){
        val title = binding.songName
        val album = binding.songAlbum
        val duration = binding.songDuration
        val image = binding.songImage
        val root= binding.root

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Myadapter {
        return Myadapter(MusicViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: Myadapter, position: Int) {
        holder.title.text=musiclist[position].title
        holder.album.text=musiclist[position].album
        holder.duration.text= formduration(musiclist[position].duration)
        Glide.with(context).load(musiclist[position].arturi)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(holder.image)
        when{
            playlistDetails ->{
                holder.root.setOnClickListener{
                    sendIntent("PlaylistDetailsAdapter", pos = position)
                }
            }
            selectionActivity->{
                holder.root.setOnClickListener{
                    if(addSong(musiclist[position])) holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.pink))
                    else  holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
                }
            }
            else ->{
                holder.root.setOnClickListener{
                    when{
                        MainActivity.search->sendIntent(ref="MusicadapterSearch",pos=position)
                        musiclist[position].id==PlayerActivity.nowPlayingId ->{
                            sendIntent(ref="NowPlaying",pos=PlayerActivity.songposition)
                        }
                        else-> sendIntent(ref="Musicadapter",pos=position)
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return musiclist.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateMusiclist(searchList: ArrayList<Music>){
        musiclist=ArrayList()
        musiclist.addAll(searchList)
        notifyDataSetChanged()
    }
    private fun sendIntent(ref: String, pos :Int){
        val intent = Intent(context,PlayerActivity::class.java)
        intent.putExtra("index",pos)
        intent.putExtra("class",ref)
        context.startActivity(intent)

    }
    private fun addSong(song:Music):Boolean{
        PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if(song.id==music.id){
                PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.removeAt(index)
                return false
            }
        }
        PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.add(song)
        return true
    }
    fun refresh(){
        musiclist=ArrayList()
        musiclist=PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist
        notifyDataSetChanged()
    }
}