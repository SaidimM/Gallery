package com.example.gallery.main.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gallery.R
import com.example.gallery.media.local.Music

class MainActivityViewModel : ViewModel() {
    private val _index = MutableLiveData<Int>()
    val index: LiveData<Int> = _index

    private var _music = MutableLiveData<Music>()
    val music: LiveData<Music> = _music

    fun toLyric(music: Music? = null) {
        music?.let { _music.value = music }
        _index.value = R.id.lyricsFragment
    }
}