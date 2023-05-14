package com.example.gallery.main.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gallery.base.utils.LocalMediaUtils
import com.example.gallery.main.model.AlbumItemModel
import com.example.gallery.media.local.enums.MediaType
import com.example.gallery.media.local.enums.SortType
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumViewModel : ViewModel() {

    private val _album = MutableLiveData<ArrayList<AlbumItemModel>>()
    val album: LiveData<ArrayList<AlbumItemModel>> = _album

    fun getImages() {
        viewModelScope.launch {
            val files = withContext(this.coroutineContext) { LocalMediaUtils.getAllImageFiles(SortType.CREATED) }
            val images = arrayListOf<AlbumItemModel>()
            files.forEach { images.add(AlbumItemModel(MediaType.IMAGE, it.path)) }
            _album.postValue(images)
        }
    }
}