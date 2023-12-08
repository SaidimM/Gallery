package com.example.gallery.main.music.viewModels

import LogUtil
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.example.gallery.Strings
import com.example.gallery.base.utils.LocalMediaUtils
import com.example.gallery.main.video.player.IMediaPlayer
import com.example.gallery.main.video.player.controller.MusicPlayer
import com.example.gallery.main.video.player.state.PlayState
import com.example.gallery.media.MusicRepository
import com.example.gallery.media.local.bean.Music
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MusicViewModel : ViewModel() {
    private val TAG = "MusicViewModel"

    private val repository = MusicRepository.getInstance()

    private val musicPlayer: IMediaPlayer = MusicPlayer()
    private var index: Int = 0

    private var _music = MutableLiveData<Music>()
    val music: LiveData<Music> = _music

    private var _musics = MutableLiveData<ArrayList<Music>>()
    val musics: LiveData<ArrayList<Music>> = _musics

    private var _state = MutableLiveData<PlayState>()
    val state: LiveData<PlayState> = _state

    private var _progress = MutableLiveData<Float>()
    val progress: LiveData<Float> = _progress

    fun loadMusic() {
        viewModelScope.launch(Dispatchers.IO) {
            _musics.value?.clear()
            repository.getMusics().catch { LogUtil.e(TAG, it.message.toString()) }.collect { _musics.postValue(it) }
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
        play()
    }

    fun seekTo(position: Int) = musicPlayer.seekTo(position)

    fun onNextPressed() {
        musicPlayer.playNext()
        _music.postValue(musicPlayer.getCurrentMusic())
    }

    fun getLastPlayedMusic() {
        val lastPlayedMusicId = repository.getLastPlayedMusic()
        var music = musics.value?.let { list -> list.find { it.id == lastPlayedMusicId } }
        if (_musics.value != null && _musics.value?.isEmpty() == false) music = _musics.value?.get(0)
        music?.let { _music.value = music }
    }

    fun saveCurrentMusic() {
        music.value?.let { repository.saveLastPlayedMusic(it) }
    }

    fun recyclePlayer() {
        musicPlayer.recycle()
    }
}