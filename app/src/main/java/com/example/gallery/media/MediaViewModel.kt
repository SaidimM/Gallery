package com.example.gallery.media

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.gallery.media.local.Music
import com.example.gallery.media.local.MusicDatabase
import com.example.gallery.media.remote.MusicRepository
import com.example.gallery.media.remote.mv.MusicVideoResult
import com.example.gallery.media.remote.search.Song
import io.reactivex.schedulers.Schedulers


class MediaViewModel : ViewModel() {
    private var _song = MutableLiveData<Song>()
    val song: LiveData<Song> = _song

    private var _lyrics = MutableLiveData<String>()
    val lyric: LiveData<String> = _lyrics

    private val repository = MusicRepository.getInstane()

    private val db: MusicDatabase = MusicDatabase.getInstance()

    fun getMusicInfo(music: Music) {
        if (music.mediaId != null) return
        val disposable = repository.searchMusic("${music.name.toString()}%20${music.singer.toString()}")
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe({ 
                if (!it.isSuccessful || it.body() == null) return@subscribe
                val item = it.body()!!.result.songs.find { song ->
                    song.mvid != 0 && song.duration <= music.duration + 100 && song.duration >= music.duration - 100
                }
                if (item != null) {
                    music.mediaId = item.id.toString()
                    music.artistId = item.artists[0].id.toString()
                    music.mvId = item.mvid
                    val temp = db.getDao().getMusicByMediaId(music.id.toString())
                    if (temp == null) db.getDao().insert(music)
                    else temp.apply {
                        mediaId = item.id.toString()
                        artistId = item.artists[0].id.toString()
                        mvId = item.mvid
                        db.getDao().update(this)
                    }
                } else it.body()!!.result.songs.find { song->
                        if (song.duration == music.duration) {
                            music.mediaId = song.id.toString()
                            music.artistId = song.artists[0].id.toString()
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
                        true
                    }
                Log.d(this.javaClass.simpleName, it.toString())
            },{
                Log.d(this.javaClass.simpleName, it.message.toString())
            })
    }

    fun getMv(music: Music, successfull: (MusicVideoResult) -> Unit){
        if (music.mvId == 0) return
        val disposable = repository.getMv(music.mvId.toString())
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe {
                if (!it.isSuccessful || it.body() == null) return@subscribe
                successfull(it.body()!!)
            }
    }
}