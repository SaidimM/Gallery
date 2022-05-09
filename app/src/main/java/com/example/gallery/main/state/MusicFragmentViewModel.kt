package com.example.gallery.main.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gallery.media.MusicRepository
import com.example.gallery.media.local.Music
import com.example.gallery.media.local.MusicDatabase
import com.example.gallery.media.remote.mv.MusicVideoResult

class MusicFragmentViewModel : ViewModel() {

    private var _musicVideo: MutableLiveData<MusicVideoResult> = MutableLiveData()
    val musicVideo: LiveData<MusicVideoResult> = _musicVideo

    private val db: MusicDatabase = MusicDatabase.getInstance()

    private val repository = MusicRepository.getInstance()

    fun getMv(music: Music) {
        repository.getMv(music,
        successful = { _musicVideo.postValue(it) })
    }
}