package com.example.gallery.media.remote

import com.example.unpixs.media.ui.page.FastJsonConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

class MusicRepository {
    private val client = OkHttpClient.Builder()
            .callTimeout(3000, TimeUnit.MILLISECONDS)
            .connectTimeout(3000, TimeUnit.MILLISECONDS)
            .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://music.163.com").client(client)
        .addConverterFactory(FastJsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    companion object {
        private var instance: MusicRepository? = null
            get() {
                if (field == null) field = MusicRepository()
                return field
            }
        fun getInstane() = instance!!

    }

    private var endpoint: NeteaseApi = retrofit.create(NeteaseApi::class.java)

    fun searchMusic(name: String) = endpoint.searchMusic(criteria = name)

    fun getMv(mvId: String) = endpoint.getMv(mvId)

    fun getLyrics(id: String) = endpoint.getLyric(id = id)

    fun getAlbum(albumId: String) = endpoint.getAlbumInfo(albumId)

    fun getArtist(artistId: String) = endpoint.getArtist(artistId)
}