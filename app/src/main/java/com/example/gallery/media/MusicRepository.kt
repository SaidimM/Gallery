package com.example.gallery.media

import android.util.Log
import com.example.gallery.base.response.fastJson.FastJsonConverterFactory
import com.example.gallery.media.local.bean.Music
import com.example.gallery.media.remote.NeteaseApi
import com.example.gallery.media.remote.album.AlbumResult
import com.example.gallery.media.remote.lyrics.LyricResult
import com.example.gallery.media.remote.music.MusicDetailResult
import com.example.gallery.media.remote.mv.MusicVideoResult
import com.example.gallery.media.remote.search.Result
import com.example.gallery.media.remote.search.SearchResult
import com.example.gallery.media.remote.search.Song
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import retrofit2.Response
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

    fun getMv(music: Music) = flow{
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
}