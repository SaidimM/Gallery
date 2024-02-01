package com.example.gallery.media.music

import com.example.gallery.media.music.local.bean.Music
import kotlinx.coroutines.flow.Flow

interface MusicDataSource {
    fun searchMusic(music: Music): Flow<Result<Any>>
    fun getMv(music: Music): Flow<Result<Any>>
    fun getAlbum(music: Music): Flow<Result<Any>>
    fun getArtist(music: Music): Flow<Result<Any>>
    fun getMusicDetail(music: Music): Flow<Result<Any>>
    fun getLyrics(music: Music): Flow<Result<Any>>
    fun getMusicList(): Flow<Result<Any>>
}