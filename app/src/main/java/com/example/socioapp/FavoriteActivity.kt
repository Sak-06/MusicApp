package com.example.socioapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.socioapp.databinding.ActivityFavoriteBinding

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter: FacoriteAdapter

    companion object{
        var favroiteSongs:ArrayList<Music> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currTheme[MainActivity.themeId])
//        setContentView(R.layout.activity_favorite)
        binding= ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        favroiteSongs= checkPlaylist(favroiteSongs)
        binding.favoriteback.setOnClickListener{
            finish()
        //startActivity(Intent(this@FavoriteActivity,MainActivity::class.java))
        }
        binding.favoriteRV.setHasFixedSize(true)
        binding.favoriteRV.setItemViewCacheSize(10)
        binding.favoriteRV.layoutManager = GridLayoutManager(this@FavoriteActivity,3)
        adapter = FacoriteAdapter(this@FavoriteActivity, favroiteSongs)
        binding.favoriteRV.adapter = adapter
        if(favroiteSongs.size<1) binding.shufflebtnFV.visibility= View.INVISIBLE
        binding.shufflebtnFV.setOnClickListener {
            val intent= Intent(this@FavoriteActivity, PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","FavoriteShuffle")
            startActivity(intent)
        }
    }
}