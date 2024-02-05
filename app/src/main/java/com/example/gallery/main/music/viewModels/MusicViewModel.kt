package com.example.gallery.main.music.viewModels

import LogUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gallery.ServiceLocator
import com.example.gallery.media.music.local.bean.Music
import com.example.gallery.player.enums.PlayState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MusicViewModel : ViewModel() {
    private val TAG = "MusicViewModel"

    private val repository = ServiceLocator.provideMusicRepository()
    private val musicPlayer = ServiceLocator.provideMusicPlayer()

    private var index: Int = 0

    private var _music = MutableLiveData<Music>()
    val music: LiveData<Music> = _music

    private var _musics = MutableLiveData<List<Music>>()
    val musics: LiveData<List<Music>> = _musics

    private var _state = MutableLiveData<PlayState>()
    val state: LiveData<PlayState> = _state

    private var _progress = MutableLiveData<Float>()
    val progress: LiveData<Float> = _progress

    fun loadMusic() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getMusicList()
                .catch { LogUtil.e(TAG, it.message.toString()) }
                .collect { _musics.postValue(it) }
        }
    }

    fun play(position: Int = index) {
        index = position
        if (musics.value == null) return
        val item = musics.value!![position]
        if (item.id != music.value?.id) {
            _state.value = PlayState.PLAYING
            _music.value = item
            musicPlayer.play()
        } else if (item.id == music.value?.id) {
            _state.value = PlayState.PAUSED
            musicPlayer.pause()
        }
    }

    fun onPlayPressed() {
        if (state.value == PlayState.PLAYING) {
            _state.postValue(PlayState.PAUSED)
        } else {
            _state.postValue(PlayState.PLAYING)
        }
        _state.postValue(if (state.value == PlayState.PLAYING) PlayState.PAUSED else PlayState.PLAYING)
        play()
    }

    fun seekTo(position: Long) = musicPlayer.seekTo(position)

    fun onNextPressed() {
        musicPlayer.next()
        _music.postValue(musicPlayer.getCurrentMusic())
    }

    fun getLastPlayedMusic() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getLastPlayedMusic()
                .catch { LogUtil.e(TAG, it.message.toString()) }
                .collect {
                    _music.value = it
                }
        }
    }

    fun saveCurrentMusic() {
        music.value?.let { repository.saveLastPlayedMusic(it) }
    }

    fun recyclePlayer() {
        musicPlayer.recycle()
    }
}