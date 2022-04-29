package com.example.gallery.main.state

import android.util.Log
import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.Utils
import com.example.gallery.R
import com.example.gallery.base.utils.LocalMusicUtils
import com.example.gallery.media.local.Music
import com.example.gallery.media.local.MusicDatabase
import com.example.gallery.media.MusicRepository
import com.example.gallery.media.remote.search.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            else db.getDao().insert(it)
        }
        storeNewSongs(newList.iterator())
    }

    private fun storeNewSongs(new: Iterator<Music>) {
        if (!new.hasNext()) return
        val music = new.next()
        repository.getMusicInfo(music,
            success = {
                _songs.value?.add(music)
                storeNewSongs(new)
            }, failed = {
                Log.e(this.javaClass.simpleName, it)
                storeNewSongs(new)
            })
    }
}