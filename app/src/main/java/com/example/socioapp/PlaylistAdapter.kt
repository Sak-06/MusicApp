package com.example.socioapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.socioapp.databinding.PlaylistViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistAdapter(private val context: Context, private var playlistList : ArrayList<Playlist>) : RecyclerView.Adapter<PlaylistAdapter.Myadapter>() {
    class Myadapter(binding: PlaylistViewBinding): RecyclerView.ViewHolder(binding.root){
        val image = binding.playlistimg
        val name= binding.playlistname
        val root = binding.root
        val delete=binding.playlistdelete

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Myadapter {
        return Myadapter(PlaylistViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: Myadapter, position: Int) {
        holder.name.text=playlistList[position].name
        holder.name.isSelected=true
        holder.delete.setOnClickListener {
            val builder= MaterialAlertDialogBuilder(context)
            builder.setTitle(playlistList[position].name)
                .setMessage("Want to Delete this playlist?")
                .setPositiveButton("YES"){dialogue,_ ->
                    PlaylistActivity.musicPlaylist.ref.removeAt(position)
                    refreshPlaylist()
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
        holder.root.setOnClickListener{
            val intent=Intent(context,PlaylistDetails::class.java)
            intent.putExtra("index",position)
            ContextCompat.startActivity(context,intent,null)
        }
        if(PlaylistActivity.musicPlaylist.ref[position].playlist.size>0){
            Glide.with(context)
                .load(PlaylistActivity.musicPlaylist.ref[position].playlist[0].arturi)
                .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
                .into(holder.image)
        }
//        Glide.with(context)
//            .load(musiclist[position].arturi)
//            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
//            .into(holder.image)
//        holder.root.setOnClickListener{
//            val intent = Intent(context,PlayerActivity::class.java)
//            intent.putExtra("index",position)
//            intent.putExtra("class","FavoriteAdapter")
//            context.startActivity(intent)
//        }

    }

    override fun getItemCount(): Int {
        return playlistList.size
    }
    fun refreshPlaylist(){
        playlistList=ArrayList()
        playlistList.addAll(PlaylistActivity.musicPlaylist.ref)
        notifyDataSetChanged()
    }

}