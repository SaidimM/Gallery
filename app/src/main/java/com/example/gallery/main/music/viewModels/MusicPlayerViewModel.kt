package com.example.gallery.main.music.viewModels

import LogUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gallery.ServiceLocator
import com.example.gallery.Constants
import com.example.gallery.base.utils.LocalMediaUtils
import com.example.gallery.main.music.enums.PlayerViewState
import com.example.gallery.media.music.local.bean.Music
import com.example.gallery.media.music.remote.lyrics.Lyric
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

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