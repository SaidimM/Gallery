package com.example.gallery.main.music.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gallery.main.video.player.controller.MusicPlayer
import com.example.gallery.media.MusicRepository
import com.example.gallery.media.local.bean.Music
import com.example.gallery.media.remote.mv.MusicVideoResult

class MusicListViewModel : ViewModel() {

    private val repository = MusicRepository.getInstance()

    val musicPlayer = MusicPlayer()
    var index: Int = 0

    private var _musicVideo: MutableLiveData<MusicVideoResult> = MutableLiveData()
    val musicVideo: LiveData<MusicVideoResult> = _musicVideo

    fun getMv(music: Music) {
        repository.getMv(music,
            successful = { _musicVideo.postValue(it) })
    }
}