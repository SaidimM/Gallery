package com.example.gallery.main.music.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.example.gallery.Strings
import com.example.gallery.base.utils.LocalMediaUtils
import com.example.gallery.media.MusicRepository
import com.example.gallery.media.local.bean.Music
import com.example.gallery.media.remote.lyrics.Lyric
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

class MusicPlayerViewModel : ViewModel() {

    private val repository = MusicRepository.getInstance()

    private var _lyrics = MutableLiveData<ArrayList<Lyric>>()
    val lyrics: LiveData<ArrayList<Lyric>> = _lyrics

    fun getLyric(music: Music): Boolean {
        val path = Strings.LYRIC_DIR + music.mediaId + ".txt"
        if (!File(path).exists()) return false
        val data = LocalMediaUtils.readFile(path)
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
        return true
    }

    fun saveLyric(music: Music) {
        if (music.mediaId.isEmpty()) return
        viewModelScope.launch {
            repository.getLyrics(music.mediaId)
                .catch { LogUtils.e(it) }
                .collect {
                    if (it.code == 200) {
                        Log.d(this.javaClass.simpleName, it.lrc.lyric)
                        LocalMediaUtils.writeStringToFile(Strings.LYRIC_DIR + music.mediaId + ".txt", it.lrc.lyric)
                    }
                }
        }
    }
}