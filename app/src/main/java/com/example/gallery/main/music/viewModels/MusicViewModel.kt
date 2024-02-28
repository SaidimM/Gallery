package com.example.gallery.main.music.viewModels

import LogUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gallery.ServiceLocator
import com.example.gallery.main.music.enums.ControllerState
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

    private var _playState = MutableLiveData<PlayState>()
    val playState: LiveData<PlayState> = _playState

    private var _progress = MutableLiveData<Float>()
    val progress: LiveData<Float> = _progress

    private var _controllerOffset = MutableLiveData<Float>()
    val controllerOffset : LiveData<Float> = _controllerOffset

    private var _controllerState = MutableLiveData<ControllerState>()
    val controllerState: LiveData<ControllerState> = _controllerState

    fun loadMusic() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getMusicList()
                .catch { LogUtil.e(TAG, it.message.toString()) }
                .collect { _musics.postValue(it) }
        }
    }

    fun playMusic(position: Int = index) {
        LogUtil.d(TAG, "playMusic, position: $position")
        index = position
        saveCurrentMusic()
        val item = musics.value!![position]
        if (item.id != music.value?.id) {
            _playState.value = PlayState.PLAYING
            _music.value = item
            musicPlayer.play()
        } else if (item.id == music.value?.id) {
            _playState.value = PlayState.PAUSED
            musicPlayer.pause()
        }
    }

    fun onPlayPressed() {
        _playState.postValue(if (playState.value == PlayState.PLAYING) PlayState.PAUSED else PlayState.PLAYING)
        playMusic()
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
                .collect { _music.postValue(it) }
        }
    }

    private fun saveCurrentMusic() {
        music.value?.let { viewModelScope.launch(Dispatchers.IO) { repository.saveLastPlayedMusic(it) } }
    }

    fun updateControllerOffset(offset: Float) {
        _controllerOffset.value = offset
    }

    fun updateControllerState(state: ControllerState) {
        _controllerState.value = state
    }

    fun recyclePlayer() {
        musicPlayer.recycle()
    }
}