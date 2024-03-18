package com.example.gallery.media.music.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gallery.media.music.local.bean.PlayHistory

@Dao
interface PlayHistoryDao {
    @Insert
    fun save(playHistory: PlayHistory): Long

    @Query("SELECT * FROM playhistory")
    fun getAll(): List<PlayHistory>
}