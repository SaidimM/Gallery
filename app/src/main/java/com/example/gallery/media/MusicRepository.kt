package com.example.gallery.media

import com.example.gallery.media.netease.Song
import com.example.unpixs.media.ui.page.FastJsonConverterFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

class MusicRepository {
    val client = OkHttpClient.Builder()
            .callTimeout(1000, TimeUnit.MILLISECONDS)
            .connectTimeout(1000, TimeUnit.MILLISECONDS)
            .build()
    val retrofit = Retrofit.Builder().baseUrl("https://music.163.com").client(client)
        .addConverterFactory(FastJsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    companion object {
        var instance: MusicRepository? = null
            get() {
                if (field == null) field = MusicRepository()
                return field
            }
    }

    private var endpoint: NetseaseApi = retrofit.create(NetseaseApi::class.java)

    fun getId(name: String, success: (songs: ArrayList<Song>) -> Unit) {
        val disposable = endpoint.searchMusic(name)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                it.body()?.let { it1 -> success(it1.result.songs as ArrayList<Song>) }
            }, {
                it.printStackTrace()
            })
    }
}