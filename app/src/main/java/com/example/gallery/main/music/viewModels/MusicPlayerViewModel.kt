package com.example.gallery.main.music.viewModels

import LogUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gallery.Strings
import com.example.gallery.base.utils.LocalMediaUtils
import com.example.gallery.media.MusicRepository
import com.example.gallery.media.local.bean.Music
import com.example.gallery.media.remote.lyrics.Lyric
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class MusicPlayerViewModel : ViewModel() {
    private val TAG = "MusicPlayerViewModel"

    private val repository = MusicRepository.getInstance()

    private var _lyrics = MutableLiveData<ArrayList<Lyric>>()
    val lyrics: LiveData<ArrayList<Lyric>> = _lyrics

    fun initMusic(music: Music) {
        viewModelScope.launch(Dispatchers.IO) {
            searchMusic(music)
                .map { findLyrics(it).single() }
                .map { getLyrics(it).single() }
                .catch { LogUtil.e(TAG, it.message.toString()) }
                .collect { _lyrics.postValue(it) }
        }
    }

    private fun searchMusic(music: Music) = flow {
        if (music.mediaId.isNotEmpty()) emit(music)
        else repository.searchMusic(music).collect { emit(it) }
    }.catch { LogUtil.e(TAG, it.message.toString()) }

    private fun findLyrics(music: Music) = flow {
        val path = Strings.LYRIC_DIR + music.mediaId + ".txt"
        if (File(path).exists()) emit(music)
        else repository.getLyrics(music.mediaId).collect {
            LocalMediaUtils.writeStringToFile(Strings.LYRIC_DIR + music.mediaId + ".txt", it.lrc.lyric)
            emit(music)
        }
    }.catch { LogUtil.e(TAG, it.message.toString()) }

    private fun getLyrics(music: Music) = flow {
        val path = Strings.LYRIC_DIR + music.mediaId + ".txt"
        if (!File(path).exists()) error("$path does not exist!")
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
                error(e)
            }
        }
        val newList = lyrics.filter { it.text.isNotEmpty() } as ArrayList
        emit(newList)
    }.catch { LogUtil.e(TAG, it.message.toString()) }
}