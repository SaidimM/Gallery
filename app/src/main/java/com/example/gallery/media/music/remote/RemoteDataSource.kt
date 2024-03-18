package com.example.gallery.media.music.remote

import LogUtil
import com.example.gallery.Constants
import com.example.gallery.media.music.local.bean.Music
import com.example.gallery.media.music.remote.music.MusicDetailResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class RemoteDataSource {

    private val TAG = "RemoteDataSource"
    private val loggingInterceptor =
        HttpLoggingInterceptor { LogUtil.d(TAG, it) }.apply { level = HttpLoggingInterceptor.Level.BODY }
    private val client = OkHttpClient.Builder()
        .callTimeout(3000, TimeUnit.MILLISECONDS)
        .connectTimeout(3000, TimeUnit.MILLISECONDS)
//        .addInterceptor(loggingInterceptor)
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://music.163.com").client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private var endpoint: NeteaseApi = retrofit.create(NeteaseApi::class.java)

    fun searchMusic(music: Music) = flow {
        LogUtil.i(TAG, "start")
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

    fun getMv(music: Music) = flow {
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

    fun getAlbum(music: Music) = flow {
        if (music.mediaAlbumId.isEmpty()) {
            emit(Result.failure<String>(Exception("Music album id is empty!")))
            return@flow
        }
        val response = endpoint.getAlbumInfo(music.mediaId)
        val result = response.body()
        if (!response.isSuccessful || result == null) {
            emit(Result.failure<String>(Exception("request failed!")))
        } else if (result.code != 200) {
            emit(Result.failure<String>(Exception("bad response: ${result.code}")))
        } else emit(Result.success(result))
    }

    fun downloadAlbumCover(music: Music, result: MusicDetailResult) = flow {
        val file = File(Constants.ALBUM_COVER_DIR + "${music.id}.jpg")
        LogUtil.d(TAG, "music: ${music.name} isExists: ${file.exists()}")
        if (file.exists()) emit(true)
        else file.createNewFile()
        val request = Request.Builder().url(result.songs[0].album.blurPicUrl).build()
        val response = client.newCall(request).execute()
        val inputStream = response.body?.byteStream()
        val outputStream = FileOutputStream(file)
        LogUtil.d(TAG, "Start save music: ${music.name}")
        inputStream?.use { input ->
            val buf = ByteArray(2048)
            var len: Int
            while (input.read(buf).also { len = it } != -1) {
                outputStream.write(buf, 0, len)
            }
            LogUtil.d(TAG, "saved the music: ${music.name}")
            emit(true)
        }
    }.flowOn(Dispatchers.IO)

    fun getMusicDetail(music: Music) = flow {
        if (music.mediaId.isEmpty()) {
            Result.failure<String>(Exception("Music id is empty!"))
            return@flow
        }
        val response = endpoint.getMusicDetail(music.mediaId, "[${music.mediaId}]")
        val result = response.body()
        if (!response.isSuccessful || result == null) {
            emit(Result.failure<String>(Exception("request failed!")))
        } else if (result.code != 200) {
            emit(Result.failure<String>(Exception("bad response: ${result.code}")))
        } else emit(Result.success(result))
    }

    fun getLyrics(music: Music) = flow {
        if (music.mediaId.isEmpty()) error("Music id is empty!")
        val response = endpoint.getLyric(id = music.mediaId)
        val result = response.body()
        if (!response.isSuccessful || result == null) error("request failed!")
        else if (result.code != 200) error("bad response: ${result.code}")
        else emit(result)
    }

    fun getArtist(music: Music) = flow {
        if (music.mediaArtistId.isEmpty()) {
            emit(Result.failure(Exception("Music artist id is empty!")))
            return@flow
        }
        val response = endpoint.getArtist(music.mediaArtistId)
        val result = response.body()
        if (!response.isSuccessful || result == null) {
            emit(Result.failure<String>(Exception("request failed!")))
        } else if (result.code != 200) {
            emit(Result.failure<String>(Exception("bad response: ${result.code}")))
        } else emit(Result.success(result))
    }
}
