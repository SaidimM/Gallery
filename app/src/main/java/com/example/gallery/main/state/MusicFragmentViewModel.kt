package com.example.gallery.main.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.Utils
import com.example.gallery.base.utils.LocalMusicUtils
import com.example.gallery.media.local.Music
import com.example.gallery.media.local.MusicDatabase
import com.example.gallery.media.MusicRepository
import com.example.gallery.media.remote.mv.MusicVideoResult
import kotlin.collections.ArrayList

class MusicFragmentViewModel : ViewModel() {
    private var _songs: MutableLiveData<ArrayList<Music>> = MutableLiveData()
    val songs: LiveData<ArrayList<Music>> = _songs

    private var _musicVideo: MutableLiveData<MusicVideoResult> = MutableLiveData()
    val musicVideo: LiveData<MusicVideoResult> = _musicVideo

    private val db: MusicDatabase = MusicDatabase.getInstance()

    private val repository = MusicRepository.getInstance()

    fun getArtistImage(music: Music) =
        LocalMusicUtils.getArtwork(
            Utils.getApp(),
            music.id,
            music.albumId,
            allowdefalut = true,
            small = false
        )

    fun getMv(music: Music) {
        repository.getMv(music,
        successful = { _musicVideo.postValue(it) })
    }
}