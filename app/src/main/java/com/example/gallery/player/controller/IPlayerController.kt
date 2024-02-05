package com.example.gallery.player.controller

import com.example.gallery.media.music.local.bean.Music
import com.example.gallery.player.enums.PlayMode

interface IPlayerController {
    fun refreshPlayList(playList: List<Music>)
    fun getPlayList(): List<Music>
    fun play()
    fun pause()
    fun playNext(music: Music)
    fun playLast(music: Music)
    fun next()
    fun previous()
    fun seekTo(position: Long)
    fun setPlayMode(mode: PlayMode)
    fun getCurrentMusic(): Music
    fun onPause(pause: () -> Unit)
    fun onPlay(play: () -> Unit)
    fun onNext(next: () -> Unit)
    fun onPrevious(previous: () -> Unit)
    fun recycle()
}