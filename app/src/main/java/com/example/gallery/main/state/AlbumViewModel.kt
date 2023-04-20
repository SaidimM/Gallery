package com.example.gallery.main.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gallery.base.utils.LocalMediaUtils
import com.example.gallery.media.local.enums.SortType
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AlbumViewModel : ViewModel() {

    private val _album = MutableLiveData<ArrayList<File>>()
    val album: LiveData<ArrayList<File>> = _album

    fun getImages() {
        viewModelScope.launch {
            val folders = withContext(this.coroutineContext) { LocalMediaUtils.getAllImageFiles(SortType.CREATED)}
            _album.postValue(folders)
        }
    }
}