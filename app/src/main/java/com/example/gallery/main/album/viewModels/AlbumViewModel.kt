package com.example.gallery.main.album.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gallery.main.album.models.AlbumItemModel
import com.example.gallery.main.album.models.AlbumSortModel
import com.example.gallery.media.ImageRepository
import com.example.gallery.media.local.enums.MediaType
import com.example.gallery.media.local.enums.SortType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.collections.forEachWithIndex

class AlbumViewModel : ViewModel() {

    private val imageRepository = ImageRepository.getInstance()

    private val _spamCount = MutableLiveData(4)
    val spamCount: LiveData<Int> = _spamCount

    val sortModel = AlbumSortModel()

    private val _album: MutableLiveData<ArrayList<AlbumItemModel>> = MutableLiveData(arrayListOf())
    val album: LiveData<ArrayList<AlbumItemModel>> = _album

    private val allImages: ArrayList<AlbumItemModel> = arrayListOf()

    fun getImages() {
        viewModelScope.launch {
            if (allImages.isEmpty()) withContext(Dispatchers.IO) { allImages.addAll(imageRepository.getImages()) }
            val sortedImages: ArrayList<AlbumItemModel> = arrayListOf()
            sortedImages.addAll(allImages)
            sortImages(sortedImages, sortModel)
            _album.postValue(sortedImages)
        }
    }

    private fun sortImages(
        images: ArrayList<AlbumItemModel> = album.value!!,
        albumSortModel: AlbumSortModel = AlbumSortModel()
    ) {
        viewModelScope.launch {
            launch { sortByTime(images, albumSortModel) }
            launch { sortByOrder(images, albumSortModel) }
            launch { splitByTime(images, albumSortModel) }
        }
    }

    private fun sortByTime(
        images: ArrayList<AlbumItemModel> = album.value!!,
        albumSortModel: AlbumSortModel = AlbumSortModel()
    ) {
        images.sortBy {
            when (albumSortModel.sortType) {
                SortType.CREATED -> -it.createdTime
                SortType.EDITED -> -it.lastEditedTime
                SortType.ACCESSED -> -it.lastAccessTime
            }
        }
    }

    private fun sortByOrder(
        images: ArrayList<AlbumItemModel> = album.value!!,
        albumSortModel: AlbumSortModel = AlbumSortModel()
    ) {
        if (!albumSortModel.isDescending) images.reverse()
    }

    private fun splitByTime(
        images: ArrayList<AlbumItemModel> = album.value!!,
        albumSortModel: AlbumSortModel = AlbumSortModel()
    ) {
        if (albumSortModel.splitByTime) {
            var time = 0L
            val oneDayMills = 24 * 60 * 60 * 1000
            when (albumSortModel.sortType) {
                SortType.CREATED -> {
                    images.forEachWithIndex { i, image ->
                        if ((image.createdTime / oneDayMills) * oneDayMills != time) {
                            time = (image.createdTime / oneDayMills) * oneDayMills
                            images.add(i, AlbumItemModel(MediaType.TITLE, createdTime = time))
                        }
                    }
                }
                SortType.EDITED -> {
                    images.forEachWithIndex { i, image ->
                        if ((image.lastEditedTime / oneDayMills) * oneDayMills != time) {
                            time = (image.lastEditedTime / oneDayMills) * oneDayMills
                            images.add(i, AlbumItemModel(MediaType.TITLE, lastEditedTime = time))
                        }
                    }
                }
                SortType.ACCESSED -> {
                    images.forEachWithIndex { i, image ->
                        if ((image.lastAccessTime / oneDayMills) * oneDayMills != time) {
                            time = (image.lastAccessTime / oneDayMills) * oneDayMills
                            images.add(i, AlbumItemModel(MediaType.TITLE, lastAccessTime = time))
                        }
                    }
                }
            }
        }
    }
}