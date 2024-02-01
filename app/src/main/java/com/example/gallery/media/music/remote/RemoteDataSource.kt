package com.example.gallery.media.music.remote

import LogUtil
import com.example.gallery.media.music.MusicDataSource
import com.example.gallery.media.music.local.bean.Music
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RemoteDataSource : MusicDataSource {

    private val TAG = "RemoteDataSource"
    private val loggingInterceptor =
        HttpLoggingInterceptor { LogUtil.d(TAG, it) }.apply { level = HttpLoggingInterceptor.Level.BODY }
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

    override fun searchMusic(music: Music) = flow {
        val response = endpoint.searchMusic(criteria = "${music.name}%20${music.singer}")
        val result = response.body()
        if (!response.isSuccessful || result == null) {
            emit(Result.failure<String>(Exception("request failed!")))
        } else if (result.code != 200) {
            emit(Result.failure<String>(Exception("bad response: ${result.code}")))
        } else if (result.result.songs.isEmpty() || result.result.songCount == 0) {
            emit(Result.failure<String>(Exception("response music size is zero!")))
        } else {
            val song = result.result.songs[0]
            emit(Result.success(song))
        }
    }

    override fun getMv(music: Music) = flow {
        if (music.mvId == 0) {
            emit(Result.failure<String>(Exception("Music has no mv!")))
            return@flow
        }
        val response = endpoint.getMv(music.mvId.toString())
        val result = response.body()
        if (!response.isSuccessful || result == null) {
            emit(Result.failure<String>(Exception("request failed!")))
        } else if (result.code != 200) {
            emit(Result.failure<String>(Exception("bad response: ${result.code}")))
        } else emit(Result.success(result))
    }

    override fun getAlbum(music: Music) = flow {
        if (music.mediaAlbumId.isEmpty()) {
            emit(Result.failure<String>(Exception("Music album id is empty!")))
            return@flow
        }
        val response = endpoint.getMv(music.mvId.toString())
        val result = response.body()
        if (!response.isSuccessful || result == null) {
            emit(Result.failure<String>(Exception("request failed!")))
        } else if (result.code != 200) {
            emit(Result.failure<String>(Exception("bad response: ${result.code}")))
        } else emit(Result.success(result))
    }

    override fun getMusicDetail(music: Music) = flow {
        if (music.mediaId.isEmpty()) {
            Result.failure<String>(Exception("Music id is empty!"))
            return@flow
        }
        val response = endpoint.getMusicDetail(music.mediaId, "[$music.mediaId]")
        val result = response.body()
        if (!response.isSuccessful || result == null) {
            emit(Result.failure<String>(Exception("request failed!")))
        } else if (result.code != 200) {
            emit(Result.failure<String>(Exception("bad response: ${result.code}")))
        } else emit(Result.success(result))
    }

    override fun getLyrics(music: Music) = flow {
        if (music.mediaId.isEmpty()) {
            Result.failure<String>(Exception("Music id is empty!"))
            return@flow
        }
        val response = endpoint.getLyric(id = music.mediaId)
        val result = response.body()
        if (!response.isSuccessful || result == null) {
            emit(Result.failure<String>(Exception("request failed!")))
        } else if (result.code != 200) {
            emit(Result.failure<String>(Exception("bad response: ${result.code}")))
        } else emit(Result.success(result))
    }

    override fun getArtist(music: Music) = flow {
        if (music.artistId.isEmpty()) {
            emit(Result.failure(Exception("Music artist id is empty!")))
            return@flow
        }
        val response = endpoint.getArtist(music.artistId)
        val result = response.body()
        if (!response.isSuccessful || result == null) {
            emit(Result.failure<String>(Exception("request failed!")))
        } else if (result.code != 200) {
            emit(Result.failure<String>(Exception("bad response: ${result.code}")))
        } else emit(Result.success(result))
    }

    override fun getMusicList() = flow<Result<Any>> { emit(Result.failure(Exception("Not implemented!"))) }
}
