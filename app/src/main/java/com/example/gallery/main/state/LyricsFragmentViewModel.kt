package com.example.gallery.main.state

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gallery.media.local.Music
import com.example.gallery.media.remote.MusicRepository
import com.example.gallery.media.remote.lyrics.Lyric
import io.reactivex.schedulers.Schedulers

class LyricsFragmentViewModel : ViewModel() {

    private val repository = MusicRepository.getInstane()

    private var _lyrics = MutableLiveData<ArrayList<Lyric>>()
    val lyrics: LiveData<ArrayList<Lyric>> = _lyrics

    fun getLyric(music: Music) {
        if (music.mediaId == null) return
        val disposable = repository.getLyrics(music.mediaId!!)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (!it.isSuccessful || it.body() == null) return@subscribe
                val data = it.body()!!.lrc.lyric
                Log.d(this.javaClass.simpleName, data)
                val strings: ArrayList<String> = data.split(Regex("\n"), 0) as ArrayList<String>
                val lyrics: ArrayList<Lyric> = arrayListOf()
                strings.forEach { string ->
                    try {
                        if (string == "") return@forEach
                        val text = string.substring(string.indexOf(']') + 1)
                        if (text == "") return@forEach
                        val time = string.substring(string.indexOf('[') + 1, string.indexOf(']'))
                        val min = time.substring(0, time.indexOf(':')).toInt() * 60 * 1000
                        val sec = (time.substring(time.indexOf(':') + 1).toFloat() * 1000).toInt()
                        val lyric = Lyric(min + sec, text)
                        lyrics.add(lyric)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return@forEach
                    }
                }
                _lyrics.postValue(lyrics)
            }, {
                Log.d(this.javaClass.simpleName, it.message.toString())
            })
    }
}