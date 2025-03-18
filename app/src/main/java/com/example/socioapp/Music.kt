package com.example.socioapp

import android.annotation.SuppressLint
import android.app.Service
import android.media.MediaMetadataRetriever
import com.example.socioapp.PlayerActivity.Companion.musicService
import java.io.File
import java.util.concurrent.TimeUnit

data class Music(
    val id:String, val title:String, val album:String, val duration: Long = 0,
    val artist:String, val path: String, val arturi: String)
class Playlist{
    lateinit var name: String
    lateinit var playlist: ArrayList<Music>
    lateinit var createdBy: String
    lateinit var createdOn: String
}
class MusicPlaylist{
    var ref : ArrayList<Playlist> = ArrayList()
}
@SuppressLint("DefaultLocale")
fun formduration(duration: Long):String{
    val min= TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS)
    val sec=(TimeUnit.SECONDS.convert(duration,TimeUnit.MILLISECONDS)-
            min*TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
    return String.format("%02d:%02d",min,sec)
}
fun getImageArt(path:String): ByteArray? {
    val retriever=MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture

}
fun setpos(increment: Boolean){
    if(!PlayerActivity.repeat) {
        if (increment) {
            if (PlayerActivity.musiclistPA.size - 1 == PlayerActivity.songposition)
                PlayerActivity.songposition = 0
            else
                ++PlayerActivity.songposition
        } else {
            if (PlayerActivity.songposition == 0)
                PlayerActivity.songposition = PlayerActivity.musiclistPA.size - 1
            else
                --PlayerActivity.songposition
        }
    }
}
fun exitActivity(){
    if(musicService!=null) {
        musicService!!.audioManager.abandonAudioFocus(PlayerActivity.musicService)
        musicService!!.stopForeground(Service.STOP_FOREGROUND_REMOVE)
        musicService!!.mediaPlayer!!.release()
        musicService = null}
    kotlin.system.exitProcess(1)

}
fun favoriteCheck(id: String):Int {
    PlayerActivity.isfav = false
    FavoriteActivity.favroiteSongs.forEachIndexed { index, music ->
        if (id == music.id) {
            PlayerActivity.isfav = true
            return index
        }
    }
    return -1
}
fun checkPlaylist(playlist:ArrayList<Music>):ArrayList<Music> {
playlist.forEachIndexed { index, music ->
    val file= File(music.path)
    if(!file.exists())
        playlist.removeAt(index)
    }
    return playlist
}



