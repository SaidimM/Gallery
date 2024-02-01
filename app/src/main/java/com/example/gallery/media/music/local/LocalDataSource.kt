package com.example.gallery.media.music.local

import LogUtil
import com.blankj.utilcode.util.Utils
import com.example.gallery.Strings
import com.example.gallery.base.utils.LocalMediaUtils
import com.example.gallery.media.music.MusicDataSource
import com.example.gallery.media.music.local.bean.Music
import com.example.gallery.media.music.local.database.GalleryDatabase
import com.example.gallery.media.music.remote.lyrics.Lyric
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.io.File

class LocalDataSource(private val database: GalleryDatabase) : MusicDataSource {
    private val TAG = "LocalDataSource"

    private val dao = database.getMusicDao()
    override fun searchMusic(music: Music) = flow {
        val result = dao.getMusic(music.id)
        if (result != null) emit(Result.success(result))
        else emit(Result.failure(Exception("Music not found!")))
    }

    override fun getMv(music: Music): Flow<Result<Any>> {
        return flow { emit(Result.failure(Exception("Not implemented!"))) }
    }

    override fun getAlbum(music: Music): Flow<Result<Any>> {
        return flow { emit(Result.failure(Exception("Not implemented!"))) }
    }

    override fun getArtist(music: Music): Flow<Result<Any>> {
        return flow { emit(Result.failure(Exception("Not implemented!"))) }
    }

    override fun getMusicDetail(music: Music): Flow<Result<Any>> {
        return flow { emit(Result.failure(Exception("Not implemented!"))) }
    }

    override fun getLyrics(music: Music) = flow {
        val path = Strings.LYRIC_DIR + music.mediaId + ".txt"
        if (!File(path).exists()) error("$path does not exist!")
        val data = LocalMediaUtils.readFile(path)
        val strings: ArrayList<String> = data.split(Regex("\n"), 0) as ArrayList<String>
        val lyrics: ArrayList<Lyric> = arrayListOf()
        var position = 0
        strings.forEach { string ->
            try {
                if (string == "") return@forEach
                val text = string.substring(string.indexOf(']') + 1)
                val time = string.substring(string.indexOf('[') + 1, string.indexOf(']'))
                val min = time.substring(0, time.indexOf(':')).toInt() * 60 * 1000
                val sec = (time.substring(time.indexOf(':') + 1).toFloat() * 1000).toInt()
                val lyric = Lyric(position, min + sec, text)
                position = min + sec
                lyrics.add(lyric)
                LogUtil.d(TAG, lyric.toString())
                if (lyrics.isNotEmpty()) lyrics.last().endPosition = min + sec
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.failure<String>(Exception(e)))
            }
        }
        val newList = lyrics.filter { it.text.isNotEmpty() } as ArrayList
        emit(Result.success(newList))
    }.catch { LogUtil.e(TAG, it.message.toString()) }

    override fun getMusicList() = flow {
        val local = LocalMediaUtils.getMusic(Utils.getApp())
        val fromDatabase = dao.getAll()
        val saved = mutableListOf<Music>()
        local.forEach { item -> fromDatabase.find { music -> music.id == item.id }?.let { saved.add(it) } }
        val deletedMusics = fromDatabase.subtract(saved.toSet())
        val upcomingMusics = local.subtract(saved.toSet())
        deletedMusics.forEach { item -> dao.deleteMusic(item) }
        upcomingMusics.forEach { item -> dao.saveMusic(item) }
        LogUtil.i(TAG, "local: ${local.size}, saved: ${saved.size}, deleted: ${deletedMusics.size}, upcoming: ${upcomingMusics.size}")
        val stored = dao.getAll()
        emit(Result.success(stored))
    }

    fun getFavorits() {

    }
}