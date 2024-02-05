package com.example.gallery.player.controller

import android.content.Context
import android.media.MediaPlayer
import com.example.gallery.media.music.local.bean.Music
import com.example.gallery.player.enums.PlayMode

class PlayerController(context: Context): IPlayerController {
    private var player: MediaPlayer = MediaPlayer()


    override fun refreshPlayList(playList: List<Music>) {
        TODO("Not yet implemented")
    }

    override fun getPlayList(): List<Music> {
        TODO("Not yet implemented")
    }

    override fun play() {
        TODO("Not yet implemented")
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun playNext(music: Music) {
        TODO("Not yet implemented")
    }

    override fun playLast(music: Music) {
        TODO("Not yet implemented")
    }

    override fun next() {
        TODO("Not yet implemented")
    }

    override fun previous() {
        TODO("Not yet implemented")
    }

    override fun seekTo(position: Long) {
        TODO("Not yet implemented")
    }

    override fun setPlayMode(mode: PlayMode) {
        TODO("Not yet implemented")
    }

    override fun getCurrentMusic(): Music {
        TODO("Not yet implemented")
    }

    override fun onPause(pause: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun onPlay(play: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun onNext(next: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun onPrevious(previous: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun recycle() {
        TODO("Not yet implemented")
    }
}