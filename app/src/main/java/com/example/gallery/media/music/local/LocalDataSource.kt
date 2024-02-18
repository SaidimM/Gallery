package com.example.gallery.media.music.local

import LogUtil
import android.graphics.BitmapFactory
import com.blankj.utilcode.util.Utils
import com.example.gallery.Constants
import com.example.gallery.base.utils.AlbumCoverUtils
import com.example.gallery.base.utils.LocalMediaUtils
import com.example.gallery.media.music.local.bean.Music
import com.example.gallery.media.music.local.bean.PlayHistory
import com.example.gallery.media.music.local.bean.PlayList
import com.example.gallery.media.music.local.database.MusicDatabase
import com.example.gallery.media.music.remote.lyrics.Lyric
import com.example.gallery.media.music.remote.search.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class LocalDataSource(private val database: MusicDatabase) {
    private val TAG = "LocalDataSource"

    private val musicDao = database.getMusicDao()
    private val playListDao = database.getPlayListDao()
    private val playHistoryDao = database.getPlayHistoryDao()

    fun getMusic(id: Long) = musicDao.getMusic(id)

    fun isMusicLyricsExist(music: Music) = File(Constants.LYRIC_DIR + music.id + ".txt").exists()

    fun getLyrics(music: Music) = flow {
        val path = Constants.LYRIC_DIR + music.id + ".txt"
        if (!File(path).exists()) error("Music [${music.name}] lyric not found!")
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
                LogUtil.i(TAG, lyric.toString())
                if (lyrics.isNotEmpty()) lyrics.last().endPosition = min + sec
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val newList = lyrics.filter { it.text.isNotEmpty() }
        emit(newList)
    }

    fun saveMusicLyrics(music: Music, lyrics: String) = flow {
        val path = Constants.LYRIC_DIR + music.id + ".txt"
        val saveFile = File(path)
        if (!File(Constants.LYRIC_DIR).exists()) File(Constants.LYRIC_DIR).mkdir()
        if (!saveFile.exists()) saveFile.createNewFile()
        var fo: FileOutputStream? = null
        try {
            fo = FileOutputStream(saveFile)
            fo.write(lyrics.toByteArray())
            LogUtil.i(TAG, "Music ${music.name} lyrics saved to path [$path]")
            emit(true)
        } catch (e: IOException) {
            e.printStackTrace()
            LogUtil.e(TAG, "Music ${music.name} lyrics save failed! ${e.message}")
            error("Music ${music.name} lyrics save failed! ${e.message}")
        } finally {
            try {
                fo!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
                emit(false)
            }
        }
    }.flowOn(Dispatchers.IO)

    fun getMusicAlbumCover(music: Music) = flow {
        val bitmap = AlbumCoverUtils.getArtwork(Utils.getApp(), music.id, music.mediaAlbumId.toLong(),
            allowdefalut = true,
            small = false
        )
        val path = Constants.ALBUM_COVER_DIR + music.mediaAlbumId + ".jpg"
        if (bitmap != null) {
            emit(bitmap)
        } else if (File(path).exists()) {
            emit(BitmapFactory.decodeFile(path))
        } else {
            emit(null)
        }
    }

    fun getMusicList() = flow {
        val local = LocalMediaUtils.getMusic(Utils.getApp())
        val fromDatabase = musicDao.getAll()
        val savedInBase = mutableListOf<Music>()
        val storedFromLocal = mutableListOf<Music>()
        local.forEach { item -> fromDatabase.find { music -> music.id == item.id }?.let { savedInBase.add(it) } }
        fromDatabase.forEach { item -> local.find { music -> music.id == item.id }?.let { storedFromLocal.add(it) } }
        val deletedMusics = fromDatabase.subtract(savedInBase.toSet())
        val upcomingMusics = local.subtract(storedFromLocal.toSet())
        deletedMusics.forEach { item -> musicDao.deleteMusic(item) }
        upcomingMusics.forEach { item -> musicDao.getMusic(item.id)?: musicDao.saveMusic(item) }
        LogUtil.i(TAG, "local: ${local.size}, saved: ${savedInBase.size}, deleted: ${deletedMusics.size}, upcoming: ${upcomingMusics.size}")
        val stored = musicDao.getAll()
        emit(stored)
    }

    fun syncWithRemote(music: Music, song: Song) = flow {
        music.mediaId = song.id.toString()
        music.mediaTitle = song.name
        music.mediaAlbumId = song.album.id.toString()
        music.mediaArtistId = song.artists[0].id.toString()
        music.mediaAlbumName = song.album.name
        music.mediaArtistName = song.artists.joinToString { artist -> artist.name }
        val result = musicDao.updateMusic(music)
        if (result > 0) emit(true)
        else error("Update music failed!")
    }

    @OptIn(FlowPreview::class)
    fun removeMusic(music: Music) = flow {
        deleteMusic(music)
            .flatMapConcat { deleteMusicFromDatabase(music) }
            .flatMapConcat { deleteMusicLyric(music) }
            .flatMapConcat { deleteMusicCover(music) }
            .catch { emit(Result.failure(it)) }
            .collect { emit(it) }
    }

    private fun deleteMusic(music: Music) = flow {
        if (!File(music.path).exists()) {
            emit(Result.failure(Exception("Music file: [${music.path}] not found!")))
        } else if (!File(music.path).delete()) {
            emit(Result.failure(Exception("Music file: [${music.path}] did not deleted!")))
        } else {
            emit(Result.success(true))
        }
    }

    private fun deleteMusicFromDatabase(music: Music) = flow {
        val result = musicDao.deleteMusic(music)
        if (result > 0) emit(Result.success(result))
        else emit(Result.failure(Exception("Remove music failed!")))
    }

    private fun deleteMusicLyric(music: Music) = flow {
        val path = Constants.LYRIC_DIR + music.mediaId + ".txt"
        if (!File(path).exists()) {
            emit(Result.failure(Exception("Music [${music.name}] lyric not found!")))
        } else if (!File(path).delete()) {
            emit(Result.failure(Exception("Music [${music.name}] lyric did not deleted!")))
        } else {
            emit(Result.success(true))
        }
    }

    private fun deleteMusicCover(music: Music) = flow {
        val path = Constants.ALBUM_COVER_DIR + music.mediaAlbumId + ".jpg"
        if (!File(path).exists()) {
            emit(Result.failure(Exception("Music [${music.name}] cover not found!")))
        } else if (!File(path).delete()) {
            emit(Result.failure(Exception("Music [${music.name}] cover did not deleted!")))
        } else {
            emit(Result.success(true))
        }
    }

    fun getAllPlayLists() = flow {
        val playLists: List<PlayList> = playListDao.getAll()
        emit(playLists)
    }

    fun getFavoriteMusicList() = flow {
        val favorites: PlayList = playListDao.getFavorites()
        emit(favorites)
    }

    fun alterPlayList(playList: PlayList) = flow {
        val result = playListDao.update(playList)
        if (result > 0) emit(Result.success(result))
        else emit(Result.failure(Exception("Alter play list failed!")))
    }

    fun addPlayList(playList: PlayList) = flow {
        val result = playListDao.save(playList)
        if (result > 0) emit(Result.success(result))
        else emit(Result.failure(Exception("Add play list failed!")))
    }

    fun deletePlayList(playList: PlayList) = flow {
        val result = playListDao.delete(playList)
        if (result > 0) emit(Result.success(result))
        else emit(Result.failure(Exception("Delete play list failed!")))
    }

    fun getRecentPlayed() = flow {
        val recent = playHistoryDao.getAll()
        emit(recent)
    }

    fun addRecent(playHistory: PlayHistory) = flow {
        val result = playHistoryDao.save(playHistory)
        if (result > 0) emit(Result.success(result))
        else emit(Result.failure(Exception("Add recent play history failed!")))
    }
}