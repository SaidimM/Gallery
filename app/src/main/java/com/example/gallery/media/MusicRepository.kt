package com.example.gallery.media

import LogUtil
import android.util.Log
import com.blankj.utilcode.util.SPUtils
import com.example.gallery.Strings
import com.example.gallery.Strings.MUSIC_ID
import com.example.gallery.media.local.bean.Music
import com.example.gallery.media.remote.NeteaseApi
import com.example.gallery.media.remote.search.Song
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MusicRepository {
    private val TAG = "MusicRepository"

    private val loggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    private val client = OkHttpClient.Builder()
        .callTimeout(3000, TimeUnit.MILLISECONDS)
        .connectTimeout(3000, TimeUnit.MILLISECONDS)
        .addInterceptor(loggingInterceptor)
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://music.163.com").client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    companion object {
        private var repository: MusicRepository? = null
            get() {
                if (field == null) field = MusicRepository()
                return field
            }

        fun getInstance() = repository!!

    }

    private var endpoint: NeteaseApi = retrofit.create(NeteaseApi::class.java)

    fun getMusicInfo(music: Music) = flow {
        if (music.name.isEmpty()) error("music name incorrect!")
        else emit(endpoint.searchMusic(criteria = "${music.name}%20${music.singer}"))
    }

    @OptIn(FlowPreview::class)
    suspend fun getAllSongsInfo(musicList: ArrayList<Music>) {
        musicList.asFlow().flatMapConcat { searchMusic(it) }.catch {
            LogUtil.e(TAG, it.message.toString())
        }.collect { Log.d(TAG, it.name) }
    }

    private fun searchMusic(music: Music) = flow {
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
        kotlinx.coroutines.delay(1000)
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

    fun getLyrics(id: String) = flow { emit(endpoint.getLyric(id = id)) }

    fun getArtist(artistId: String) = flow { emit(endpoint.getArtist(artistId)) }

    fun getLastPlayedMusic(): Long = SPUtils.getInstance().getString(MUSIC_ID, "0").toLong()

    fun saveLastPlayedMusic(music: Music) = SPUtils.getInstance().put(MUSIC_ID, music.id.toString())
}