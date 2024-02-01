package com.example.gallery.media.music.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.blankj.utilcode.util.Utils
import com.example.gallery.media.music.local.bean.Music
import com.example.gallery.media.music.local.dao.MusicDao

@Database(entities = [Music::class], version = 1)
abstract class GalleryDatabase : RoomDatabase() {
    abstract fun getMusicDao(): MusicDao

    companion object {
        private var instance: GalleryDatabase? = null
        fun getInstance(): GalleryDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(Utils.getApp(), GalleryDatabase::class.java, "gallery").build()
            }
            return instance!!
        }
    }
}