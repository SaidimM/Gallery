package com.example.gallery.main.music.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gallery.main.video.player.controller.MusicPlayer
import com.example.gallery.media.MusicRepository
import com.example.gallery.media.local.bean.Music
import com.example.gallery.media.remote.mv.MusicVideoResult
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MusicListViewModel : ViewModel() {

    private val repository = MusicRepository.getInstance()

    val musicPlayer = MusicPlayer()
    var index: Int = 0

    private var _musicVideo: MutableLiveData<MusicVideoResult> = MutableLiveData()
    val musicVideo: LiveData<MusicVideoResult> = _musicVideo

    fun getMv(music: Music) {
        viewModelScope.launch {
            repository.getMv(music).collect {
                if (it.isSuccessful) {
                    _musicVideo.postValue(it.body())
                }
            }
        }
    }
}