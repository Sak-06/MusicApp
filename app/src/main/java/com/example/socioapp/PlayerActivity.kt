package com.example.socioapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.socioapp.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlayerActivity : AppCompatActivity(),ServiceConnection , MediaPlayer.OnCompletionListener{
    @SuppressLint("StaticFieldLeak")
    companion object{
       lateinit var musiclistPA:ArrayList<Music>
        var songposition: Int=0
        var isplay: Boolean=false
        var musicService: MusicService?=null
        lateinit var binding: ActivityPlayerBinding
        var repeat : Boolean = false
        var min15:Boolean=false
        var min30:Boolean=false
        var min60:Boolean=false
        var nowPlayingId :String=""
        var isfav :Boolean=false
        var fIndex:Int=-1
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_player)
        setTheme(MainActivity.currTheme[MainActivity.themeId])
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(intent.data?.scheme.contentEquals("content")){
            val uri = intent.data
            if (uri != null) {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistPA = ArrayList()
                musiclistPA.add(getMusicDetails(uri))
                setLayout()
            } else {
                Toast.makeText(this, "Invalid music file", Toast.LENGTH_SHORT).show()
                finish() // Close activity if no valid data
            }
        }
        else intialize()
        binding.playerBack.setOnClickListener {
            finish()
            //startActivity(Intent(this@PlayerActivity, MainActivity::class.java))
        }
        binding.nextPA.setOnClickListener {
            playnextprev(true)
        }
        binding.prevPA.setOnClickListener {
            playnextprev(false)
        }
        binding.playpause.setOnClickListener {
            if (isplay) pausemusic()
            else playmusic()

        }
        binding.seekbarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(p2){
                    musicService!!.mediaPlayer!!.seekTo(p1)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit

            override fun onStopTrackingTouch(p0: SeekBar?) = Unit

        })
        binding.repeatbtn.setOnClickListener{
            if(!repeat){
                repeat=true
                binding.repeatbtn.setColorFilter(ContextCompat.getColor(this,R.color.yellow))
            }
           else{
               repeat=false
            }
        }
        binding.equalizerbtn.setOnClickListener{
            try{
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME,baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE,AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent,10)
            }
            catch(e:Exception){Toast.makeText(this,"Equilizer not supported",Toast.LENGTH_SHORT).show()}
        }
        binding.timerbtn.setOnClickListener {
            val timer = min15 || min30 || min60
            if(!timer){
                showBottonSheet()
            }
            else{
                val builder= MaterialAlertDialogBuilder(this)
                builder.setTitle("Stop Timer")
                    .setMessage("Want to Stop Timer?")
                    .setPositiveButton("YES"){_,_ ->
                        min15=false
                        min30=false
                        min60=false
                        binding.timerbtn.setColorFilter(ContextCompat.getColor(this,R.color.lavendar))
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
        binding.sharebtn.setOnClickListener {
            val shrIntent =Intent()
            shrIntent.action=Intent.ACTION_SEND
            shrIntent.type="audio/*"
            shrIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musiclistPA[songposition].path))
            startActivity(Intent.createChooser(shrIntent,"share music"))
        }
        binding.favbtn.setOnClickListener {
            if(isfav){
                isfav=false
                binding.favbtn.setImageResource(R.drawable.baseline_favorite_empty)
                FavoriteActivity.favroiteSongs.removeAt(fIndex)
            }
            else{
                isfav=true
                binding.favbtn.setImageResource(R.drawable.baseline_favorite_24)
                FavoriteActivity.favroiteSongs.add(musiclistPA[songposition])
            }
        }
    }

    private fun getMusicDetails(contentUri: Uri): Music {
        var cursor:Cursor?= null
        try {
            val projection= arrayOf(MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DURATION)
            cursor=this.contentResolver.query(contentUri,projection,null,null,null)
            val dataColumn=cursor?.getColumnIndex(MediaStore.Audio.Media.DATA)
            val durationColumn=cursor?.getColumnIndex(MediaStore.Audio.Media.DURATION)
            cursor!!.moveToFirst()
            val path= dataColumn?.let { cursor.getString(it) }
            val duration= durationColumn?.let { cursor.getLong(it) }!!
            return Music(id="Unknown", title = path.toString(), album = "Unknown",
                duration=duration, artist = "Unknown",path=path.toString(), arturi = "Unknown")
        }
        finally {
            cursor?.close()
        }

    }

    private fun intialize(){
        songposition=intent.getIntExtra("index",0)
        when(intent.getStringExtra("class")){
            "FavoriteShuffle"->{
                val intent=Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistPA=ArrayList()
                musiclistPA.addAll(FavoriteActivity.favroiteSongs)
                musiclistPA.shuffle()
                setLayout()
            }
            "FavoriteAdapter"->{
                val intent=Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistPA= ArrayList()
                musiclistPA.addAll(FavoriteActivity.favroiteSongs)
                setLayout()
            }
            "NowPlaying"->{
                setLayout()
                if(isplay) binding.playpause.setImageResource(R.drawable.pause_icon)
                else binding.playpause.setImageResource(R.drawable.play_icon)
                binding.seekbarStart.text= formduration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.seekbarEnd.text= formduration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekbarPA.progress= musicService!!.mediaPlayer!!.currentPosition
                binding.seekbarPA.max= musicService!!.mediaPlayer!!.duration
            }
            "MusicadapterSearch"->{
                val intent=Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistPA= ArrayList()
                musiclistPA.addAll(MainActivity.MusicListSearch)
                setLayout()
            }
            "Musicadapter" ->{
                val intent=Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistPA=ArrayList()
                musiclistPA.addAll(MainActivity.MusiclistMA)
                setLayout()
                //playMedia()

            }
            "MainActivity" ->{
                val intent=Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistPA=ArrayList()
                musiclistPA.addAll(MainActivity.MusiclistMA)
                musiclistPA.shuffle()
                setLayout()
                //playMedia()

            }
            "PlaylistDetailsAdapter"->{
                val intent=Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistPA=ArrayList()
                musiclistPA.addAll(PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist)
                setLayout()
            }
            "PlaylistDetailShuffle"->{
                val intent=Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistPA=ArrayList()
                musiclistPA.addAll(PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist)
                musiclistPA.shuffle()
                setLayout()
            }
        }
        //musicService?.showNotification()
    }
    private fun setLayout(){
        fIndex= favoriteCheck(musiclistPA[songposition].id)
        Glide.with(this).load(musiclistPA[songposition].arturi)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(binding.songingPA)
        binding.titlePA.text= musiclistPA[songposition].title
        if(repeat) binding.repeatbtn.setColorFilter(ContextCompat.getColor(this,R.color.blue))
        if(min15 || min30 || min60) binding.timerbtn.setColorFilter(ContextCompat.getColor(this,R.color.blue))
        if(isfav) binding.favbtn.setImageResource(R.drawable.baseline_favorite_24)
        else binding.favbtn.setImageResource(R.drawable.baseline_favorite_empty)
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun playMedia(){
        try {
            if (musicService!!.mediaPlayer== null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musiclistPA[songposition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isplay=true
            binding.playpause.setImageResource(R.drawable.pause_icon)
            musicService!!.showNotification(R.drawable.pause_icon,0F)
            binding.seekbarStart.text= formduration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.seekbarEnd.text= formduration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekbarPA.progress=0
            binding.seekbarPA.max= musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            nowPlayingId= musiclistPA[songposition].id
        }
        catch(err:Exception){return}
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun playmusic(){
        binding.playpause.setImageResource(R.drawable.pause_icon)
        musicService!!.showNotification(R.drawable.pause_icon,1F)
        isplay=true
        musicService!!.mediaPlayer!!.start()
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun pausemusic(){
        binding.playpause.setImageResource(R.drawable.play_icon)
        musicService!!.showNotification(R.drawable.play_icon,0F)
        isplay=false
        musicService!!.mediaPlayer!!.pause()
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun playnextprev(increment:Boolean){
        if(increment){
            setpos(true)
            setLayout()
            playMedia()
        }
        else{
            setpos(false)
            setLayout()
            playMedia()
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val binder= p1 as MusicService.MyBinder
        musicService=binder.currentService()
        playMedia()
        musicService!!.seekbarSetup()
        musicService!!.audioManager=getSystemService(Context.AUDIO_SERVICE) as AudioManager
        musicService!!.audioManager.requestAudioFocus(musicService,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN)
       }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService=null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCompletion(p0: MediaPlayer?) {
        setpos(true)
        playMedia()
        try{
            setLayout()
        }
        catch(e:Exception){return}
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==10 || resultCode== RESULT_OK)
            return
    }
    private fun showBottonSheet(){
        val diag= BottomSheetDialog(this)
        diag.setContentView(R.layout.bottom_sheet)
        diag.show()
        diag.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener{
            Toast.makeText(this,"Music will stop after 15 minutes",Toast.LENGTH_SHORT).show()
            binding.timerbtn.setColorFilter(ContextCompat.getColor(this,R.color.blue))
            min15=true
            Thread{Thread.sleep(15*60000)
            if(min15) exitActivity()}.start()
            diag.dismiss()

        }
        diag.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener{
            Toast.makeText(this,"Music will stop after 30 minutes",Toast.LENGTH_SHORT).show()
            binding.timerbtn.setColorFilter(ContextCompat.getColor(this,R.color.blue))
            min30=true
            Thread{Thread.sleep(30*60000)
                if(min30) exitActivity()}.start()
            diag.dismiss()
        }
        diag.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener{
            Toast.makeText(this,"Music will stop after 60 minutes",Toast.LENGTH_SHORT).show()
            binding.timerbtn.setColorFilter(ContextCompat.getColor(this,R.color.blue))
            min60=true
            Thread{Thread.sleep(60*60000)
                if(min60) exitActivity()}.start()
            diag.dismiss()
        }
    }


}

