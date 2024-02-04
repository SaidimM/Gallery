package com.example.gallery

import com.example.gallery.media.music.IMusicRepository
import com.example.gallery.media.music.MusicRepository
import com.example.gallery.media.music.local.LocalDataSource
import com.example.gallery.media.music.local.database.MusicDatabase
import com.example.gallery.media.music.remote.RemoteDataSource

object ServiceLocator {
    private val repository: IMusicRepository? = null

    private fun getMusicRepository(): IMusicRepository {
        val database = MusicDatabase.getInstance()
        val localDataSource = LocalDataSource(database)
        val remoteDataSource = RemoteDataSource()
        return MusicRepository(localDataSource, remoteDataSource)
    }

    fun provideMusicRepository(): IMusicRepository {
        return repository ?: getMusicRepository()
    }
}