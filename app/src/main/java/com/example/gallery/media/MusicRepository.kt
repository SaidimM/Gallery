package com.example.gallery.media

import LogUtil
import android.util.Log
import androidx.room.PrimaryKey
import androidx.room.Room
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.Utils
import com.example.gallery.Strings.MUSIC_ID
import com.example.gallery.base.utils.LocalMediaUtils
import com.example.gallery.media.local.bean.Music
import com.example.gallery.media.local.database.GalleryDatabase
import com.example.gallery.media.remote.NeteaseApi
import com.example.gallery.media.remote.search.SearchResult
import com.example.gallery.media.remote.search.Song
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Objects
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy
import java.util.concurrent.TimeUnit

class MusicRepository {
    private val TAG = "MusicRepository"

    private val loggingInterceptor =
        HttpLoggingInterceptor{ LogUtil.d(TAG, it) }.apply { level = HttpLoggingInterceptor.Level.BODY }
    private val client = OkHttpClient.Builder()
        .callTimeout(3000, TimeUnit.MILLISECONDS)
        .connectTimeout(3000, TimeUnit.MILLISECONDS)
        .addInterceptor(loggingInterceptor)
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://music.163.com").client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private var endpoint: NeteaseApi = retrofit.create(NeteaseApi::class.java)
    private val dao = GalleryDatabase.getInstance().getMusicDao()

    companion object {
        private var repository: MusicRepository? = null
            get() {
                if (field == null) field = MusicRepository()
                return field
            }

        fun getInstance() = repository!!

    }

    fun searchMusic(music: Music) = flow {
        val response = endpoint.searchMusic(criteria = "${music.name}%20${music.singer}")
        if (!response.isSuccessful || response.body() == null) {
            LogUtil.d(TAG, response.message())
            error("request failed!")
        }
        val result = response.body()!!
        if (result.code != 200) {
            error("bad response: ${result.code}")
        }
        if (result.result.songs.isEmpty() || result.result.songCount == 0) {
            error("response music size is zero!")
        }
        val song = result.result.songs[0]
        saveMusic(music, song)
        saveMusicToDatabase(music)
        emit(music)
    }

    private fun saveMusic(music: Music, song: Song) {
        music.mediaId = song.id.toString()
        music.artistId = song.artists[0].id.toString()
        music.mvId = song.mvid
        music.name = song.name
        music.singer = song.let {
            val iterator = it.artists.iterator()
            var singer = ""
            while (iterator.hasNext()) {
                singer += iterator.next().name + ","
            }
            singer = singer.substring(0, singer.length - 1)
            singer
        }
        music.mediaAlbumId = song.album.id.toString()
    }

    private fun saveMusic(target: Music, source: Music) {
        target.apply {
            name = source.name
            singer = source.singer
            size = source.size
            duration = source.duration
            path = source.path
            albumId = source.albumId
            id = source.id
            mediaId = source.mediaId
            mediaAlbumId = source.mediaAlbumId
            mvId = source.mvId
            artistId = source.artistId
            albumCoverBlurHash = source.albumCoverBlurHash
        }
    }

    private fun saveMusicToDatabase(music: Music) {
        val result = dao.getMusic(music.id)
        if (result == null) dao.saveMusic(music)
        else dao.updateMusic(music)
    }

    fun getMv(music: Music) = flow {
        if (music.mvId == 0) error("Music didn't have any MV!")
        else emit(endpoint.getMv(music.mvId.toString()))
    }

    fun getAlbum(albumId: String) = flow {
        if (albumId.isEmpty()) error("album is empty!")
        else emit(endpoint.getAlbumInfo(albumId))
    }

    fun getMusicDetail(musicId: String) = flow {
        if (musicId.isEmpty()) error("music id is empty!")
        else emit(endpoint.getMusicDetail(musicId, "[$musicId]"))
    }

    fun getLyrics(id: String) = flow {
        val result = endpoint.getLyric(id = id)
        if (!result.isSuccessful || result.body() == null) {
            error("request failed!")
        }
        val body = result.body()!!
        if (body.code != 200) error("response error, response code: ${body.code}")
        Log.d(this.javaClass.simpleName, body.lrc.lyric)
        emit(body)
    }

    fun getArtist(artistId: String) = flow { emit(endpoint.getArtist(artistId)) }

    fun getLastPlayedMusic(): Long = SPUtils.getInstance().getString(MUSIC_ID, "0").toLong()

    fun saveLastPlayedMusic(music: Music) = SPUtils.getInstance().put(MUSIC_ID, music.id.toString())

    suspend fun getMusics() = flow {
        coroutineScope {
            val list = withContext(Dispatchers.IO) { LocalMediaUtils.getMusic(Utils.getApp()) }
            val stored = withContext(Dispatchers.IO) { dao.getAll() }
            launch(Dispatchers.IO) {
                stored.forEach { item -> list.find { music -> music.id == item.id }?.let { saveMusic(it, item) } }
            }
            emit(list)
        }
    }
}