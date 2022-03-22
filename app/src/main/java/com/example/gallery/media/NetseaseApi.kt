package com.example.gallery.media

import com.example.gallery.media.lyrics.LyricResult
import com.example.gallery.media.netease.SearchResult
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface NetseaseApi {
    @GET(value = "/api/search/get/web?csrf_token=hlpretag=&hlposttag=&s={query}&type=1&offset=0&total=true&limit=10")
    fun searchMusic(@Path("query") query: String): Observable<Response<SearchResult>>

    @GET(value = "/api/song/lyric?os=pc&id={id}&lv=-1&kv=-1&tv=-1")
    fun getLyric(@Path("id") id: String): Observable<Response<LyricResult>>
}