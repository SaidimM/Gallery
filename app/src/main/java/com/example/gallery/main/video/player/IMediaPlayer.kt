package com.example.gallery.main.video.player

import com.example.gallery.media.music.local.bean.Music

interface IMediaPlayer {
    fun playNext()
    fun playPrevious()
    fun play(music: Music? = null, musics: List<Music>? = null)
    fun pause()
    fun seekTo(position: Int)
    fun getCurrentMusic(): Music
    fun recycle()
}