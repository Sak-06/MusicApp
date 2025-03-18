package com.example.socioapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socioapp.PlayerActivity.Companion.musicService
import com.example.socioapp.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicadapter: Musicadapter

    companion object{
        lateinit var MusiclistMA : ArrayList<Music>
        lateinit var MusicListSearch : ArrayList<Music>
        var search : Boolean=false
        private const val PERMISSION_REQUEST_CODE = 1001
        var themeId: Int=0
        val currTheme = arrayOf(R.style.lavendar,R.style.pink,R.style.blue,R.style.green,R.style.black)
        val currNavTheme = arrayOf(R.style.lavendarnav,R.style.pinknav,R.style.bluenav,R.style.greennav,R.style.blacknav)
        val currGradient= arrayOf(R.drawable.gradient_lavendar,R.drawable.gradient_pink,R.drawable.gradient_blue,R.drawable.gradient_green,R.drawable.gradient_black)
        var sortOrder=0
        val sortingList= arrayOf(MediaStore.Audio.Media.DATE_ADDED + " DESC",
            MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.SIZE +" DESC")
    }
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeEditor=getSharedPreferences("THEMES", MODE_PRIVATE)
        themeId=themeEditor.getInt("ThemeIndex",0)
        setTheme(currNavTheme[themeId])
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //loading all songs in favorites
        loadFavoriteSongs()
        //Navigation Drawer
        toggle= ActionBarDrawerToggle(this,binding.root,R.string.open,R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(checkAndRequestPermissions()) {
            initializelayout()
            FavoriteActivity.favroiteSongs=ArrayList()
            val editor = getSharedPreferences("Favorite", MODE_PRIVATE)
            val jsonStr=editor.getString("FavoriteSongs",null)
            val typeToken = object : TypeToken<ArrayList<Music>>(){}.type
            if(jsonStr!=null){
                val data : ArrayList<Music> = GsonBuilder().create().fromJson(jsonStr,typeToken)
                FavoriteActivity.favroiteSongs.addAll(data)
            }
            PlaylistActivity.musicPlaylist= MusicPlaylist()
            val jsonStrPlaylist=editor.getString("MusicPlaylist",null)
            if(jsonStrPlaylist!=null){
                val dataPlaylist : MusicPlaylist = GsonBuilder().create().fromJson(jsonStrPlaylist,MusicPlaylist::class.java)
                PlaylistActivity.musicPlaylist=dataPlaylist
            }

        }
        binding.shufflebtn.setOnClickListener {
            val intent=Intent(this@MainActivity, PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","MainActivity")
            startActivity(intent)
        }
        binding.favoritebtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, FavoriteActivity::class.java))

        }
        binding.playistbtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, PlaylistActivity::class.java))
        }
        binding.navView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.nav_feedback -> {
                    startActivity(Intent(this,FeedbackActivity::class.java))
                }
                R.id.nav_setting -> {
                    startActivity(Intent(this,SettingActivity::class.java))
                }
                R.id.nav_about -> {
                    startActivity(Intent(this,AboutActivity::class.java))
                }
                R.id.nav_exit -> {
                    val builder= MaterialAlertDialogBuilder(this)
                    builder.setTitle("EXIT")
                        .setMessage("Want to exit App?")
                        .setPositiveButton("YES"){_,_ ->
                            exitActivity()

                        }
                        .setNegativeButton("NO") { dialogue, _ ->
                            dialogue.dismiss()
                        }
                    val customdialogue = builder.create()
                    customdialogue.show()
                        customdialogue.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
                       customdialogue.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE)


                }
            }
            true
        }
    }
    private fun checkAndRequestPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ (API 33) - Request only READ_MEDIA_AUDIO
            requestPermission(android.Manifest.permission.READ_MEDIA_AUDIO)
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11-12 (API 30-32) - Request READ_EXTERNAL_STORAGE
            requestPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        else {
            // Android 10 and below - Request READ_EXTERNAL_STORAGE
            requestPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
    private fun loadFavoriteSongs() {
        FavoriteActivity.favroiteSongs = ArrayList() // Initialize list
        val prefs = getSharedPreferences("Favorite", MODE_PRIVATE)
        val jsonStr = prefs.getString("FavoriteSongs", null)
        val typeToken = object : TypeToken<ArrayList<Music>>() {}.type
        if (jsonStr != null) {
            val data: ArrayList<Music> = GsonBuilder().create().fromJson(jsonStr, typeToken)
            FavoriteActivity.favroiteSongs.addAll(data)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun initializelayout(){
        search=false
        val sortEditor=getSharedPreferences("SORTING", MODE_PRIVATE)
        sortOrder=sortEditor.getInt("sortOrder",0)
        MusiclistMA = getallaudio()
        binding.musicRV.setHasFixedSize(true)
        binding.musicRV.setItemViewCacheSize(10)
        binding.musicRV.layoutManager = LinearLayoutManager(this@MainActivity)
        musicadapter = Musicadapter(this@MainActivity, MusiclistMA)
        binding.musicRV.adapter = musicadapter
        binding.totalsongs.text = " Total Songs: " + musicadapter.itemCount
    }
    private fun requestPermission(permission:String):Boolean{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), PERMISSION_REQUEST_CODE)
            }
        }
        if(ActivityCompat.checkSelfPermission(this,permission)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
            return false
        }
        else{
            return true
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== PERMISSION_REQUEST_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"PERMISSION GRANTED",Toast.LENGTH_SHORT).show()
                initializelayout()
            }
            else{
                ActivityCompat.requestPermissions(this,permissions, PERMISSION_REQUEST_CODE)
            }
        }
    }
    @SuppressLint("Recycle", "Range", "SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun getallaudio(): ArrayList<Music>{
        val templ = ArrayList<Music>()
        val selection= MediaStore.Audio.Media.IS_MUSIC+ "!=0"
        val projection = arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE
            ,MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ALBUM_ID)
        val cursor=this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,
            sortingList[sortOrder],null)
        if(cursor!=null){
            if(cursor.moveToFirst())
                do{
                    val titleC=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))?:"Unknown"
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))?:"Unknown"
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))?:"Unknown"
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))?:"Unknown"
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))?:"Unknown"
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))?:"Unknown"
                    val albumIDC=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()?:"Unknown"
                    val uri =Uri.parse("content://media/external/audio/albumart")
                    val arturi=Uri.withAppendedPath(uri,albumIDC).toString()

                    val music=Music(id = idC, title = titleC, album = albumC, artist = artistC, path = pathC, duration = durationC as Long, arturi = arturi)
                    val file= File(music.path)
                    if(file.exists())
                        templ.add(music)
                }while (cursor.moveToNext())
            cursor.close()
        }
        return templ


    }

    override fun onDestroy() {
        super.onDestroy()
        saveFavoriteSongs()
        if(!PlayerActivity.isplay && musicService!=null){
            exitActivity()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search,menu)
        findViewById<LinearLayout>(R.id.navigationbar)?.setBackgroundResource(currGradient[themeId])
        val searchView=menu?.findItem(R.id.searchView)?.actionView as androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean = true

            override fun onQueryTextChange(p0: String?): Boolean {
                MusicListSearch= ArrayList()
                if(p0!=null){
                    val userinput=p0.lowercase()
                    for(song in MusiclistMA){
                        if(song.title.lowercase().contains(userinput))
                            MusicListSearch.add(song)
                    }
                    search=true
                    musicadapter.updateMusiclist(MusicListSearch)
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        //storing favorite songs
        saveFavoriteSongs()
        savePlaylist()
        val sortEditor=getSharedPreferences("SORTING", MODE_PRIVATE)
        val sortValue=sortEditor.getInt("sortOrder",0)
        if(sortOrder!=sortValue){
            sortOrder=sortValue
            MusiclistMA=getallaudio()
            musicadapter.updateMusiclist(MusiclistMA)
        }
    }
    private fun saveFavoriteSongs() {
        val editor = getSharedPreferences("Favorite", MODE_PRIVATE).edit()
        val jsonStr = GsonBuilder().create().toJson(FavoriteActivity.favroiteSongs)
        editor.putString("FavoriteSongs", jsonStr)
        editor.apply()
    }
    private fun savePlaylist(){
        val editor = getSharedPreferences("Favorite", MODE_PRIVATE).edit()
        val jsonStr = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStr)
        editor.apply()
    }



}