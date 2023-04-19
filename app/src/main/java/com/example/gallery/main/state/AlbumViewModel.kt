package com.example.gallery.main.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gallery.base.utils.LocalMediaUtils
import com.example.gallery.media.local.bean.ImgFolderBean
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumViewModel : ViewModel() {

    private val _album = MutableLiveData<ArrayList<ImgFolderBean>>()
    val album: LiveData<ArrayList<ImgFolderBean>> = _album

    fun getImages() {
        viewModelScope.launch {
            val folders = withContext(this.coroutineContext) { LocalMediaUtils.getImageFolders()}
            _album.postValue(folders)
        }
    }
}