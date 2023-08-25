package com.example.gallery.main.music.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gallery.media.MusicRepository
import com.example.gallery.media.remote.mv.MusicVideoResult

class MusicPlayerViewModel : ViewModel() {

    private val repository = MusicRepository.getInstance()

    private var _isLyricsShowing = MutableLiveData(false)
    val isLyricsShowing: LiveData<Boolean> = _isLyricsShowing


}