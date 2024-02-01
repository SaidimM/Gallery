package com.example.gallery.main.video.player.controller

import android.media.MediaPlayer
import com.example.gallery.main.video.player.IMediaPlayer
import com.example.gallery.media.music.local.bean.Music
import com.example.gallery.main.video.player.listener.PlayerListener
import com.example.gallery.main.video.player.controller.PlayMode.*
import kotlin.random.Random

class MusicPlayer : IMediaPlayer {
    private var playMode: PlayMode = LIST
    private val list: ArrayList<Music> = arrayListOf()
    private val historyList: ArrayList<Music> = arrayListOf()
    private var position: Int = 0
    private val player: Player = Player()

    private val listener: PlayerListener = object: PlayerListener {
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

    override fun playNext() {
        if (list.isEmpty()) return
        position = when (playMode) {
            LIST -> if (position == list.size - 1) 0 else position + 1
            RANDOM -> Random.nextInt(list.size)
            SINGLE -> position
        }
        player.path = list[position].path
        player.initialize()
    }

    override fun playPrevious() {
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

    override fun play(music: Music?, musics: List<Music>?) {
        if (musics != null && music != null && musics != list) {
            list.clear()
            list.addAll(musics)
            position = list.indexOf(music)
        } else if (music == null && musics == null) {
            player.start()
            return
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

    override fun pause() {
        player.pause()
    }

    override fun seekTo(position: Int) { player.seekTo(position) }

    override fun getCurrentMusic() = list[position]

    override fun recycle() = player.release()
}