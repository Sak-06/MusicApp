package com.example.socioapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.socioapp.databinding.FavoriteViewBinding

class FacoriteAdapter(private val context: Context, private var musiclist : ArrayList<Music>) : RecyclerView.Adapter<FacoriteAdapter.Myadapter>() {
    class Myadapter(binding: FavoriteViewBinding): RecyclerView.ViewHolder(binding.root){
        val image = binding.songimgFV
        val name= binding.songnameFV
        val root = binding.root

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Myadapter {
        return Myadapter(FavoriteViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: Myadapter, position: Int) {
        holder.name.text=musiclist[position].title
        Glide.with(context)
            .load(musiclist[position].arturi)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(holder.image)
        holder.root.setOnClickListener{
            val intent = Intent(context,PlayerActivity::class.java)
            intent.putExtra("index",position)
            intent.putExtra("class","FavoriteAdapter")
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return musiclist.size
    }

}