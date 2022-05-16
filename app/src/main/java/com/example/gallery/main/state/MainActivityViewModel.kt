package com.example.gallery.main.state

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.example.gallery.R
import com.example.gallery.Strings
import com.example.gallery.Strings.ALBUM_COVER_DIR
import com.example.gallery.base.utils.LocalMusicUtils
import com.example.gallery.base.utils.LocalMusicUtils.bitmapToFile
import com.example.gallery.blurHash.BlurHash
import com.example.gallery.media.MusicRepository
import com.example.gallery.media.local.Music
import com.example.gallery.media.local.MusicDatabase
import com.example.gallery.player.controller.MusicPlayer
import com.example.gallery.player.controller.Player
import com.example.gallery.player.listener.PlayerListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.util.*

class MainActivityViewModel : ViewModel() {

    private val repository = MusicRepository.getInstance()
    private val db: MusicDatabase = MusicDatabase.getInstance()

    val musicPlayer = MusicPlayer()
    var index: Int = 0

    private var _music = MutableLiveData<Music>()
    val music: LiveData<Music> = _music

    fun toLyric(music: Music) {
        musicPlayer.play(music)
        _music.value = music
        index = R.id.lyricsFragment
    }

    private var _songs: MutableLiveData<ArrayList<Music>> = MutableLiveData()
    val songs: LiveData<ArrayList<Music>> = _songs

    fun loadMusic() {
        viewModelScope.launch(Dispatchers.IO) {
            val stored = db.getDao().getAll() as ArrayList<Music>
            val local = LocalMusicUtils.getmusic(Utils.getApp())
            restore(stored, local)
            _songs.postValue(stored)
        }
    }

    private fun restore(stored: ArrayList<Music>, local: ArrayList<Music>) {
        val hashTable = Hashtable<Long, Music>()
        val newList = arrayListOf<Music>()
        stored.forEach { hashTable[it.id] = it }
        local.forEach {
            if (hashTable[it.id] == null) newList.add(it)
        }
        storeNewSongs(newList.iterator())
    }

    private fun storeNewSongs(new: Iterator<Music>) {
        if (!new.hasNext()) return
        val music = new.next()
        repository.getMusicInfo(music,
            success = {
                songs.value?.let {
                    it.add(music)
                    _songs.postValue(it)
                    saveLyric(music)
                    saveAlbumCover(music)
                }
                storeNewSongs(new)
            }, failed = {
                Log.e(this.javaClass.simpleName, it)
                storeNewSongs(new)
            })
    }

    private fun saveAlbumCover(music: Music) {
        repository.getMusicDetail(
            music.mediaId,
            success = {
                val albumImagePath = ALBUM_COVER_DIR + "${music.mediaAlbumId}.jpg"
                if (!File(albumImagePath).exists()) {
                    val request = ImageRequest.Builder(Utils.getApp())
                        .data(it.songs[0].album.picUrl).target { drawable ->
                            val bitmap = drawable as BitmapDrawable
                            bitmapToFile(albumImagePath, bitmap.bitmap, 100)
                        }.build()
                    val imageLoader = ImageLoader.Builder(Utils.getApp()).build()
                    imageLoader.enqueue(request)
                }
            }, failed = {
                Log.e(this.javaClass.simpleName, it)
            })
    }

    fun loadAlbumCover(item: Music, imageView: ImageView) {
        val albumCoverPath = ALBUM_COVER_DIR + "${item.mediaAlbumId}.jpg"
        doAsync {
            val bitmap = if (File(albumCoverPath).exists()) {
                BitmapFactory.decodeFile(albumCoverPath)
            } else LocalMusicUtils.getArtwork(
                Utils.getApp(),
                item.id,
                item.albumId,
                allowdefalut = true,
                small = false
            )
            uiThread { Glide.with(imageView).load(bitmap).into(imageView) }
            if (bitmap != null && item.albumCoverBlurHash.isEmpty()) {
                val blurHash = BlurHash.encode(bitmap)
                item.albumCoverBlurHash = blurHash
                db.getDao().update(item)
            }
        }
    }

    fun saveLyric(music: Music) {
        if (music.mediaId.isEmpty()) return
        repository.getLyrics(
            music.mediaId,
            success = {
                val data = it.lrc.lyric
                Log.d(this.javaClass.simpleName, data)
                LocalMusicUtils.writeStringToFile(Strings.LYRIC_DIR + music.mediaId + ".txt", data)
            }, failed = {
                Log.e(this.javaClass.simpleName, it)
            })
    }
}