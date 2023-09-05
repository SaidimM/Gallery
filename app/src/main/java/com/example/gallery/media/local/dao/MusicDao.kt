package com.example.gallery.media.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gallery.media.local.bean.Music

@Dao
interface MusicDao {

    @Insert
    fun saveMusic(music: Music)

    @Update
    fun updateMusic(music: Music)

    @Query("SELECT * FROM music WHERE id = :id LIMIT 1")
    fun getMusic(id: Int): Music

    @Query("SELECT * FROM music")
    fun getAll(): List<Music>
}