package com.example.gallery.player.controller

import android.content.Context
import android.media.MediaPlayer
import com.example.gallery.media.music.local.bean.Music
import com.example.gallery.player.enums.PlayMode

class PlayerController(context: Context): IPlayerController {
    private var player: MediaPlayer = MediaPlayer()
    private val playList: ArrayList<Music> = arrayListOf()
    private var index = -1
    private var playMode = PlayMode.LOOP

    override fun refreshPlayList(list: ArrayList<Music>) {
        playList.clear()
        playList.addAll(list)
    }

    override fun getPlayList() = playList

    override fun play(index: Int) {
        if (playList.isEmpty()) return
        if (player.isPlaying) return
        when (index) {
            -1 -> return
            this.index -> player.start()
            else -> {
                this.index = index
                player.stop()
                player.setDataSource(playList[index].path)
                player.prepare()
                player.setOnPreparedListener { player.start() }
            }
        }
    }

    override fun pause() {
        if (index == -1) return
        if (player.isPlaying) player.pause()
    }

    override fun playNext(music: Music) {
        playList.add(index + 1, music)
    }

    override fun playLast(music: Music) {
        playList.add(playList.size, music)
    }

    override fun next() {
        if (playList.isEmpty()) return
        index = when (playMode) {
            PlayMode.LOOP -> if (index >= playList.size) 0 else index ++
            PlayMode.SHUFFLE -> if (index >= playList.size) 0 else index ++
            PlayMode.SINGLE -> index
        }
    }

    override fun previous() {
        if (playList.isEmpty()) return
        index = when (playMode) {
            PlayMode.LOOP -> if (index == 0) playList.size - 1 else index --
            PlayMode.SHUFFLE -> if (index == 0) playList.size - 1 else index --
            PlayMode.SINGLE -> index
        }
    }

    override fun seekTo(position: Long) {
        player.seekTo(position.toInt())
    }

    override fun setPlayMode(mode: PlayMode) {
        playMode = mode
    }

    override fun getCurrentMusic() = playList[index]

    override fun recycle() {
        player.stop()
        player.reset()
    }
}