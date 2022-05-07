package com.example.gallery.main.state

import android.graphics.drawable.BitmapDrawable
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
import com.example.gallery.Strings.ALBUM_COVER_DIR
import com.example.gallery.base.utils.LocalMusicUtils
import com.example.gallery.base.utils.LocalMusicUtils.bitmapToFile
import com.example.gallery.media.MusicRepository
import com.example.gallery.media.local.Music
import com.example.gallery.media.local.MusicDatabase
import com.example.gallery.media.remote.search.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.util.*

class MainActivityViewModel : ViewModel() {

    private val repository = MusicRepository.getInstance()

    private val db: MusicDatabase = MusicDatabase.getInstance()

    var index: Int = 0

    private var _song = MutableLiveData<Song>()
    val song: LiveData<Song> = _song

    private var _music = MutableLiveData<Music>()
    val music: LiveData<Music> = _music

    fun toLyric(music: Music? = null) {
        music?.let { _music.value = music }
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
                }
                storeNewSongs(new)
            }, failed = {
                Log.e(this.javaClass.simpleName, it)
                storeNewSongs(new)
            })
    }

    fun saveAlbumImage(music: Music, imageView: ImageView) {
        repository.getMusicDetail(music.mediaId.toString(),
            success = {
                val albumImagePath = ALBUM_COVER_DIR + "${music.mediaAlbumId}.jpg"
                if (!File(albumImagePath).exists()) {
                    val request = ImageRequest.Builder(Utils.getApp())
                        .data(it.songs[0].album.picUrl).target { drawable ->
                            val bitmap = drawable as BitmapDrawable
                            bitmapToFile(albumImagePath, bitmap.bitmap, 100)
                            doAsync {
                                uiThread { Glide.with(imageView).load(drawable).into(imageView) }
                            }
                        }.build()
                    val imageLoader = ImageLoader.Builder(Utils.getApp()).build()
                    imageLoader.enqueue(request)
                }
            }, failed = {
                Log.e(this.javaClass.simpleName, it)
            })
    }
}