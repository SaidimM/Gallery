package com.example.gallery.main.music.viewModels

import LogUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gallery.ServiceLocator
import com.example.gallery.main.video.player.IMediaPlayer
import com.example.gallery.main.video.player.controller.MusicPlayer
import com.example.gallery.main.video.player.state.PlayState
import com.example.gallery.media.music.local.bean.Music
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MusicViewModel : ViewModel() {
    private val TAG = "MusicViewModel"

    private val repository = ServiceLocator.provideMusicRepository()

    private val musicPlayer: IMediaPlayer = MusicPlayer()
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
            _state.value = PlayState.PLAY
            _music.value = item
            musicPlayer.play(item, musics.value)
        } else if (item.id == music.value?.id) {
            _state.value = PlayState.PAUSE
            musicPlayer.pause()
        }
    }

    fun onPlayPressed() {
        if (state.value == PlayState.PLAY) {
            _state.postValue(PlayState.PAUSE)
        } else {
            _state.postValue(PlayState.PLAY)
        }
        _state.postValue(if (state.value == PlayState.PLAY) PlayState.PAUSE else PlayState.PLAY)
        play()
    }

    fun seekTo(position: Int) = musicPlayer.seekTo(position)

    fun onNextPressed() {
        musicPlayer.playNext()
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