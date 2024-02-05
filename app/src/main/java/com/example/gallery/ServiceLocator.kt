package com.example.gallery

import com.blankj.utilcode.util.Utils
import com.example.gallery.media.music.IMusicRepository
import com.example.gallery.media.music.MusicRepository
import com.example.gallery.media.music.local.LocalDataSource
import com.example.gallery.media.music.local.database.MusicDatabase
import com.example.gallery.media.music.remote.RemoteDataSource
import com.example.gallery.player.controller.IPlayerController
import com.example.gallery.player.controller.PlayerController

object ServiceLocator {
    private val repository: IMusicRepository? = null
    private val playerController: IPlayerController? = null

    private fun getMusicRepository(): IMusicRepository {
        val database = MusicDatabase.getInstance()
        val localDataSource = LocalDataSource(database)
        val remoteDataSource = RemoteDataSource()
        return MusicRepository(localDataSource, remoteDataSource)
    }

    fun provideMusicRepository(): IMusicRepository {
        return repository ?: getMusicRepository()
    }

    fun provideMusicPlayer(): IPlayerController {
        return playerController ?: PlayerController(Utils.getApp())
    }
}