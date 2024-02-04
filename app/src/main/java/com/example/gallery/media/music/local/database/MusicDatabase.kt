package com.example.gallery.media.music.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.blankj.utilcode.util.Utils
import com.example.gallery.media.music.local.bean.Music
import com.example.gallery.media.music.local.dao.MusicDao
import com.example.gallery.media.music.local.dao.PlayHistoryDao
import com.example.gallery.media.music.local.dao.PlayListDao

@Database(entities = [Music::class], version = 1)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun getMusicDao(): MusicDao
    abstract fun getPlayListDao(): PlayListDao
    abstract fun getPlayHistoryDao(): PlayHistoryDao

    companion object {
        private var instance: MusicDatabase? = null
        fun getInstance(): MusicDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(Utils.getApp(), MusicDatabase::class.java, "gallery").build()
            }
            return instance!!
        }
    }
}