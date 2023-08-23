package com.example.gallery.main.music.viewModels

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
import com.example.gallery.main.video.player.controller.MusicPlayer
import com.example.gallery.main.video.player.state.PlayState
import com.example.gallery.media.MusicRepository
import com.example.gallery.media.local.bean.Music
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MusicViewModel : ViewModel() {
    private val TAG = "MusicViewModel"

    private val repository = MusicRepository.getInstance()

    val musicPlayer = MusicPlayer()
    var index: Int = 0

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
            val list = LocalMediaUtils.getMusic(Utils.getApp())
            _musics.postValue(list)
            repository.getAllSongsInfo(list)
        }
    }

    fun loadAlbumCover(item: Music, imageView: ImageView) {
        val albumCoverPath = Strings.ALBUM_COVER_DIR + "${item.mediaAlbumId}.jpg"
        viewModelScope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                if (File(albumCoverPath).exists()) {
                    BitmapFactory.decodeFile(albumCoverPath)
                } else LocalMediaUtils.getArtwork(
                    Utils.getApp(),
                    item.id,
                    item.albumId,
                    allowdefalut = true,
                    small = false
                )
            }
            this.launch(Dispatchers.Main) {
                Glide.with(imageView).load(bitmap).into(imageView)
            }
        }
    }

    fun getMusicInfo() {
        if (music.value == null) return
        viewModelScope.launch {
            repository.getMusicInfo(music.value!!)
                .catch { LogUtil.e(TAG, it.message.toString()) }
                .collect { response ->
//                    if (response.isSuccessful && response.body() != null) {
//                        response.body()!!.result.songs.forEach { song -> LogUtils.d(song) }
////                        music.mvId = response.body()!!.result.songs[0].mvid
//                    }
                }
        }
    }

    fun saveLyric(music: Music) {
        if (music.mediaId.isEmpty()) return
        viewModelScope.launch {
            repository.getLyrics(music.mediaId)
                .catch { LogUtils.e(it) }.collect {
                if (it.isSuccessful) {
                    val data = it.body()!!.lrc.lyric
                    Log.d(this.javaClass.simpleName, data)
                    LocalMediaUtils.writeStringToFile(Strings.LYRIC_DIR + music.mediaId + ".txt", data)
                }
            }
        }
    }

    fun playMusic(position: Int) {
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
            musicPlayer.pause()
            _state.postValue(PlayState.PAUSE)
        } else {
            musicPlayer.play()
            _state.postValue(PlayState.PLAY)
        }
    }

    fun onNextPressed() {
        musicPlayer.playNext()
        _music.postValue(musicPlayer.getCurrentMusic())
    }
}