package com.example.gallery.main.music.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gallery.media.music.MusicRepository
import com.example.gallery.media.music.local.bean.Music
import com.example.gallery.media.music.remote.mv.MusicVideoResult
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MusicListViewModel : ViewModel() {

    private val repository = MusicRepository.getInstance()
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