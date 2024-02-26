package com.example.gallery.main.music.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gallery.ServiceLocator
import com.example.gallery.main.music.enums.PlayerViewState
import com.example.gallery.media.music.remote.lyrics.Lyric

class MusicPlayerViewModel : ViewModel() {
    private val TAG = "MusicPlayerViewModel"

    private val repository = ServiceLocator.provideMusicRepository()

    private var _lyrics = MutableLiveData<ArrayList<Lyric>>()
    val lyrics: LiveData<ArrayList<Lyric>> = _lyrics

    private var _viewState = MutableLiveData<PlayerViewState>()
    val viewState: LiveData<PlayerViewState> = _viewState

    fun updateViewState() {
        when (viewState.value) {
            null -> _viewState.value = PlayerViewState.LYRICS
            PlayerViewState.ALBUM -> _viewState.value = PlayerViewState.LYRICS
            else -> _viewState.value = PlayerViewState.ALBUM
        }
    }
}