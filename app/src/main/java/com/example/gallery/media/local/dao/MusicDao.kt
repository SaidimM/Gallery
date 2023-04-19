package com.example.gallery.media.local.dao

import androidx.room.*
import com.example.gallery.media.local.bean.Music

@Dao
interface MusicDao {
    @Query("select * from music")
    fun getAll(): List<Music>

    @Query("select * from Music where id=:mediaId")
    fun getMusicByMediaId(mediaId: String): Music?

    @Insert
    fun insert(Music: Music)

    @Delete
    fun delete(Music: Music)

    @Update
    fun update(Music: Music)
}