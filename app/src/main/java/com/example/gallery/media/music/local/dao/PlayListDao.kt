package com.example.gallery.media.music.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gallery.media.music.local.bean.PlayList

@Dao
interface PlayListDao {

    @Insert
    fun save(playList: PlayList): Long

    @Delete
    fun delete(playList: PlayList): Int

    @Update
    fun update(playList: PlayList): Int

    @Query("SELECT * FROM playlist")
    fun getAll(): List<PlayList>

    @Query("SELECT * FROM playlist WHERE name = 'favorite'")
    fun getFavorites(): PlayList
}