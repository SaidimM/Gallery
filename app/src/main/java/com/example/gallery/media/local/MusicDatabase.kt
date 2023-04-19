package com.example.gallery.media.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.blankj.utilcode.util.Utils
import com.example.gallery.media.local.bean.Music
import com.example.gallery.media.local.dao.MusicDao


@Database(entities = [Music::class], version = 1, exportSchema = false)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun getDao(): MusicDao

    companion object {
        private var databaseInstance: MusicDatabase? = null
        private val DATABASE_NAME = "music"

        @Synchronized
        fun getInstance(): MusicDatabase {
            if (databaseInstance == null) {
                databaseInstance = Room
                    .databaseBuilder(Utils.getApp(), MusicDatabase::class.java, DATABASE_NAME)
                    .build()
            }
            return databaseInstance!!
        }
    }
}