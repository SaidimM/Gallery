package com.example.gallery.main.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.blankj.utilcode.util.Utils
import com.example.gallery.base.utils.LocalMusicUtils
import com.example.gallery.media.local.Music
import com.example.gallery.media.local.MusicDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class MusicFragmentViewModel : ViewModel() {
    private var _songs: MutableLiveData<ArrayList<Music>> = MutableLiveData()
    val songs: LiveData<ArrayList<Music>> = _songs

    private val db: MusicDatabase = MusicDatabase.getInstance()

    fun loadMusic() {
        viewModelScope.launch(Dispatchers.IO) {
            val stored = db.getDao().getAll() as ArrayList<Music>
            val local = LocalMusicUtils.getmusic(Utils.getApp())
            restore(stored, local)
            _songs.postValue(stored)
        }
    }

    fun getArtistImage(music: Music) =
        LocalMusicUtils.getArtwork(
            Utils.getApp(),
            music.id,
            music.albumId,
            allowdefalut = true,
            small = false
        )

    private fun restore(listA: ArrayList<Music>, listB: ArrayList<Music>) {
        val hashTable = Hashtable<Long, Music>()
        val union = arrayListOf<Music>()
        listA.forEach { hashTable[it.id] = it }
        listB.forEach {
            if (hashTable[it.id] != null) union.add(it)
            else db.getDao().insert(it)
        }
        union.forEach { if (hashTable[it.id] == null) db.getDao().delete(it) }
        listA.clear()
        listA.addAll(db.getDao().getAll())
    }
}