package com.example.socioapp

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socioapp.MainActivity.Companion.MusicListSearch
import com.example.socioapp.MainActivity.Companion.MusiclistMA
import com.example.socioapp.MainActivity.Companion.search
import com.example.socioapp.databinding.ActivitySongSelectionBinding

class SongSelection : AppCompatActivity() {
    private lateinit var binding: ActivitySongSelectionBinding
    private lateinit var adapter: Musicadapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currTheme[MainActivity.themeId])
        binding=ActivitySongSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.SelectionRV.setItemViewCacheSize(10)
        binding.SelectionRV.setHasFixedSize(true)
        binding.SelectionRV.layoutManager= LinearLayoutManager(this)
        adapter=Musicadapter(this,MainActivity.MusiclistMA, selectionActivity = true)
        binding.SelectionRV.adapter=adapter
        binding.searchSS.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean = true

            override fun onQueryTextChange(p0: String?): Boolean {
                MusicListSearch = ArrayList()
                if(p0!=null){
                    val userinput=p0.lowercase()
                    for(song in MusiclistMA){
                        if(song.title.lowercase().contains(userinput))
                            MusicListSearch.add(song)
                    }
                    search =true
                    adapter.updateMusiclist(MusicListSearch)
                }
                return true
            }
        })
        binding.backSS.setOnClickListener { finish() }

    }
}