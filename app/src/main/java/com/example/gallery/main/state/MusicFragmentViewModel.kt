package com.example.gallery.main.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.Utils
import com.example.gallery.base.utils.LocalMusicUtils
import com.example.gallery.media.Music

class MusicFragmentViewModel : ViewModel() {
    private var _songs: MutableLiveData<ArrayList<Music>> = MutableLiveData()
    val songs: LiveData<ArrayList<Music>> = _songs

    fun loadMusic() = LocalMusicUtils.getmusic(Utils.getApp())

    fun getArtistImage(music: Music) =
        LocalMusicUtils.getArtwork(
            Utils.getApp(),
            music.id,
            music.albumId,
            allowdefalut = true,
            small = false
        )
}