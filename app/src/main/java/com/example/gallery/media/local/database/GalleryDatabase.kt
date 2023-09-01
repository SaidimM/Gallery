package com.example.gallery.media.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gallery.media.local.bean.Music
import com.example.gallery.media.local.dao.MusicDao

@Database(entities = [ Music::class ], version = 1)
abstract class GalleryDatabase: RoomDatabase() {
    abstract fun getMusicDao(): MusicDao
}