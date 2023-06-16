package com.example.gallery.main.music.viewModels

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.example.gallery.Strings
import com.example.gallery.base.utils.LocalMediaUtils
import com.example.gallery.main.video.player.controller.MusicPlayer
import com.example.gallery.main.video.player.state.PlayState
import com.example.gallery.media.MusicRepository
import com.example.gallery.media.local.bean.Music
import com.example.gallery.media.remote.mv.MusicVideoResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MusicViewModel : ViewModel() {

    private val repository = MusicRepository.getInstance()

    val musicPlayer = MusicPlayer()
    var index: Int = 0

    private var _musicVideo: MutableLiveData<MusicVideoResult> = MutableLiveData()
    val musicVideo: LiveData<MusicVideoResult> = _musicVideo

    private var _music = MutableLiveData<Music>()
    val music: LiveData<Music> = _music

    private var _musics = MutableLiveData<ArrayList<Music>>()
    val musics: LiveData<ArrayList<Music>> = _musics

    private var _state = MutableLiveData<PlayState>()
    val state: LiveData<PlayState> = _state

    fun loadMusic() {
        viewModelScope.launch(Dispatchers.IO) {
            _musics.value?.clear()
            val list = LocalMediaUtils.getMusic(Utils.getApp())
            _musics.postValue(list)
        }
    }

    private fun saveAlbumCover(music: Music) {
        repository.getMusicDetail(
            music.mediaId,
            success = {
                val albumImagePath = Strings.ALBUM_COVER_DIR + "${music.mediaAlbumId}.jpg"
                if (!File(albumImagePath).exists()) {
                    val request = ImageRequest.Builder(Utils.getApp())
                        .data(it.songs[0].album.picUrl).target { drawable ->
                            val bitmap = drawable as BitmapDrawable
                            LocalMediaUtils.bitmapToFile(albumImagePath, bitmap.bitmap, 100)
                        }.build()
                    val imageLoader = ImageLoader.Builder(Utils.getApp()).build()
                    imageLoader.enqueue(request)
                }
            }, failed = {
                Log.e(this.javaClass.simpleName, it)
            })
    }

    fun getMv(music: Music) {
        repository.getMv(music,
            successful = { _musicVideo.postValue(it) })
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

    fun saveLyric(music: Music) {
        if (music.mediaId.isEmpty()) return
        repository.getLyrics(
            music.mediaId,
            success = {
                val data = it.lrc.lyric
                Log.d(this.javaClass.simpleName, data)
                LocalMediaUtils.writeStringToFile(Strings.LYRIC_DIR + music.mediaId + ".txt", data)
            }, failed = {
                Log.e(this.javaClass.simpleName, it)
            })
    }

    fun playMusic(position: Int) {
        if (musics.value == null) return
        val item = musics.value!![position]
        if (item.id != music.value?.id) {
            _state.postValue(PlayState.PLAY)
            _music.postValue(item)
            musicPlayer.play(item, musics.value)
        } else if (item.id == music.value?.id) {
            _state.postValue(PlayState.PAUSE)
            musicPlayer.pause()
        }
    }

    fun onPlayPressed() {
        if (state.value == PlayState.PLAY) {
            musicPlayer.pause()
            _state.postValue(PlayState.PAUSE)
        }
        else {
            musicPlayer.play()
            _state.postValue(PlayState.PLAY)
        }
    }

    fun onNextPressed() {
        musicPlayer.playNext()
        _music.postValue(musicPlayer.getCurrentMusic())
    }
}