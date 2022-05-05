package com.example.gallery.media

import android.util.Log
import com.example.gallery.media.local.Music
import com.example.gallery.media.local.MusicDatabase
import com.example.gallery.media.remote.NeteaseApi
import com.example.gallery.media.remote.album.AlbumResult
import com.example.gallery.media.remote.mv.MusicVideoResult
import com.example.gallery.media.remote.search.Song
import com.example.unpixs.media.ui.page.FastJsonConverterFactory
import io.reactivex.schedulers.Schedulers
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

    private val db: MusicDatabase = MusicDatabase.getInstance()

    companion object {
        private var repository: MusicRepository? = null
            get() {
                if (field == null) field = MusicRepository()
                return field
            }
        fun getInstance() = repository!!

    }

    fun getMusicInfo(music: Music, success: (() -> Unit)? = null, failed: ((String) -> Unit)? = null) {
        if (music.mediaId != null) return
        val disposable = endpoint.searchMusic(criteria = "${music.name.toString()}%20${music.singer.toString()}")
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (!it.isSuccessful || it.body() == null) return@subscribe
                val item = it.body()!!.result.songs.find { song ->
                    song.mvid != 0 && song.duration <= music.duration + 100 && song.duration >= music.duration - 100
                }
                if (item != null) saveMusic(music, item)
                else {
                    val song = it.body()!!.result.songs.find { song ->
                        song.duration <= music.duration + 100 && song.duration >= music.duration - 100
                    }
                    if (song != null) saveMusic(music, song)
                    else failed?.let { it1 -> it1("") }
                }
                Log.d(this.javaClass.simpleName, it.toString())
                if (success != null) success()
            },{
                Log.d(this.javaClass.simpleName, it.message.toString())
                if (failed != null) failed(it.message.toString())
            })
    }

    private fun saveMusic(music: Music, song: Song) {
        music.mediaId = song.id.toString()
        music.artistId = song.artists[0].id.toString()
        music.albumId = song.album.id.toLong()
        music.mvId = song.mvid
        val temp = db.getDao().getMusicByMediaId(music.id.toString())
        if (temp == null) db.getDao().insert(music)
        else temp.apply {
            mediaId = song.id.toString()
            artistId = song.artists[0].id.toString()
            mvId = song.mvid
            db.getDao().update(this)
        }
    }

    fun getMv(music: Music, successful: (MusicVideoResult) -> Unit, failed: ((String) -> Unit)? = null) {
        if (music.mvId == 0) return
        val disposable = endpoint.getMv(music.mvId.toString())
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (!it.isSuccessful || it.body() == null) return@subscribe
                successful(it.body()!!)
            }, { throwable ->
                throwable.printStackTrace()
                failed?.let { failed(throwable.message.toString()) }
            })
    }

    fun getAlbum(albumId: String, success: ((AlbumResult) -> Unit)? = null, failed: ((String) -> Unit)? = null) {
        if (albumId.isEmpty()) return
        val disposable = endpoint.getAlbumInfo(albumId)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (!it.isSuccessful || it.body() == null) return@subscribe
                Log.d("AlbumImage", it.toString())
                if (success != null) {
                    success(it.body()!!)
                }
            }, { throwable ->
                throwable.printStackTrace()
                failed?.let { failed(throwable.message.toString()) }
            })
    }

    private var endpoint: NeteaseApi = retrofit.create(NeteaseApi::class.java)

    fun getLyrics(id: String) = endpoint.getLyric(id = id)

    fun getArtist(artistId: String) = endpoint.getArtist(artistId)
}