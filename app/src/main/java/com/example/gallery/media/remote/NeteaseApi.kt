package com.example.gallery.media.remote

import com.example.gallery.media.remote.album.AlbumResult
import com.example.gallery.media.remote.artist.ArtistResult
import com.example.gallery.media.remote.lyrics.LyricResult
import com.example.gallery.media.remote.music.MusicDetailResult
import com.example.gallery.media.remote.mv.MusicVideoResult
import com.example.gallery.media.remote.search.SearchResult
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NeteaseApi {
    @GET(value = "/api/search/get/web")
    suspend fun searchMusic(
        @Query("csrf_token=hlpretag") token: String = "",
        @Query("hlposttag") tag: String = "",
        @Query("s") criteria: String,
        @Query("type") type: Int = 1,
        @Query("offset") offset: Int = 0,
        @Query("total") total: Boolean = true,
        @Query("limit") limit: Int = 5
    ): Response<SearchResult>

    @GET(value = "/api/song/lyric")
    fun getLyric(
        @Query("os") os: String = "pc",
        @Query("id") id: String,
        @Query("lv") lv: Int = -1,
        @Query("kv") kv: Int = -1,
        @Query("tv") tv: Int = -1,
    ): Response<LyricResult>

    @GET(value = "/api/mv/detail")
    suspend fun getMv(
        @Query("id") id: String,
        @Query("type") type: String = "mp4"
    ): Response<MusicVideoResult>

    @GET(value = "/api/album/{album_id}")
    suspend fun getAlbumInfo(@Path("album_id") albumId: String): Response<AlbumResult>

    @GET(value = "/api/artist/{artist_id}")
    suspend fun getArtist(@Path("artist_id") artistId: String): Response<ArtistResult>

    //http://music.163.com/api/song/detail/?id=31090820&ids=%5B31090820%5D
    @GET(value = "/api/song/detail/")
    fun getMusicDetail(@Query("id") id: String, @Query("ids") ids: String): Response<MusicDetailResult>
}