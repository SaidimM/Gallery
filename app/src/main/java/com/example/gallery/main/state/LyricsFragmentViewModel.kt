package com.example.gallery.main.state

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gallery.Strings.LYRIC_DIR
import com.example.gallery.base.utils.LocalMusicUtils.readFile
import com.example.gallery.media.local.Music
import com.example.gallery.media.remote.lyrics.Lyric
import java.io.File

class LyricsFragmentViewModel : ViewModel() {

    private var _music = MutableLiveData<Music>()
    val music: LiveData<Music> = _music

    private var _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap> = _bitmap

    private var _lyrics = MutableLiveData<ArrayList<Lyric>>()
    val lyrics: LiveData<ArrayList<Lyric>> = _lyrics

    fun getLyric() {
        if (music.value == null) return
        val path = LYRIC_DIR + music.value!!.mediaId + ".txt"
        if (!File(path).exists()) return
        val data = readFile(path)
        val strings: ArrayList<String> = data.split(Regex("\n"), 0) as ArrayList<String>
        val lyrics: ArrayList<Lyric> = arrayListOf()
        strings.forEach { string ->
            try {
                if (string == "") return@forEach
                val text = string.substring(string.indexOf(']') + 1)
                val time = string.substring(string.indexOf('[') + 1, string.indexOf(']'))
                val min = time.substring(0, time.indexOf(':')).toInt() * 60 * 1000
                val sec = (time.substring(time.indexOf(':') + 1).toFloat() * 1000).toInt()
                val lyric = Lyric(min + sec, text)
                lyrics.add(lyric)
                if (lyrics.isNotEmpty()) lyrics.last().endPosition = min + sec
            } catch (e: Exception) {
                e.printStackTrace()
                return@forEach
            }
        }
        val newList = lyrics.filter { it.text.isNotEmpty() } as ArrayList
        _lyrics.postValue(newList)
    }

    fun setMusic(music: Music) {
        _music.value = music
    }
}