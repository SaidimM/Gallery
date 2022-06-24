package com.example.gallery.player.controller

import android.media.MediaPlayer
import com.example.gallery.media.local.Music
import com.example.gallery.player.listener.PlayerListener
import com.example.gallery.player.controller.PlayMode.*
import kotlin.random.Random

class MusicPlayer {
    private var playMode: PlayMode = LIST
    private val list: ArrayList<Music> = arrayListOf()
    private val historyList: ArrayList<Music> = arrayListOf()
    private var position: Int = 0
    private val player: Player = Player()

    private val listener: PlayerListener = object: PlayerListener{
        override fun onPrepared(mediaPlayer: MediaPlayer) {
            val music = list[position]
            list.remove(music)
            list.add(music)
            player.start()
        }
        override fun onLoadingChanged(isLoaded: Boolean) {}
        override fun onBufferinghChengedListener(mediaPlayer: MediaPlayer, percent: Int) {}
        override fun onCompletionListener(mediaPlayer: MediaPlayer) { playNext() }
        override fun onError(mp: MediaPlayer, what: Int, extra: Int) {}
        override fun onVideoSizeChanged(mediaPlayer: MediaPlayer, width: Int, height: Int) {}
    }

    init { player.playerListener = listener }

    fun playNext() {
        if (list.isEmpty()) return
        position = when (playMode) {
            LIST -> if (position == list.size - 1) 0 else position + 1
            RANDOM -> Random.nextInt(list.size)
            SINGLE -> position
        }
        player.path = list[position].path
        player.initialize()
    }

    fun playPrevious() {
        if (list.isEmpty()) return
        position = when(playMode) {
            LIST -> if (position == 0) list.size -1 else position - 1
            RANDOM -> list.indexOf(historyList[historyList.size - 2])
            SINGLE -> position
        }
        player.path = list[position].path
        player.initialize()
    }

    private fun addHistory(music: Music) {
        list.remove(music)
        list.add(music)
    }

    fun play(music: Music? = null, musics: ArrayList<Music>? = null) {
        if (musics != null && music != null && musics != list) {
            list.clear()
            list.addAll(musics)
            position = list.indexOf(music)
        }
        player.stop()
        if (list.isEmpty()) return
        if (music == null) {
            player.path = list[position].path
        } else {
            position = list.indexOf(music)
            player.path = music.path
        }
        player.initialize()
    }

    fun pause() {
        player.pause()
    }

    fun seekTo(position: Int) { player.seekTo(position) }
}