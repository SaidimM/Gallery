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
            val sorted: ArrayList<AlbumItemModel> = arrayListOf()
            var time = 0L
            val oneDayMills = 24 * 60 * 60 * 1000
            when (albumSortModel.sortType) {
                SortType.CREATED -> {
                    images.forEachIndexed { i, image ->
                        val currentDayMills = (image.createdTime / oneDayMills) * oneDayMills
                        if (currentDayMills != time) {
                            time = currentDayMills
                            sorted.add(AlbumItemModel(MediaType.TITLE, createdTime = time))
                        }
                        sorted.add(image)
                    }
                }
                SortType.EDITED -> {
                    images.forEachIndexed { i, image ->
                        val currentDayMills = (image.lastEditedTime / oneDayMills) * oneDayMills
                        if (currentDayMills != time) {
                            time = currentDayMills
                            sorted.add(AlbumItemModel(MediaType.TITLE, lastEditedTime = time))
                        }
                        sorted.add(image)
                    }
                }
                SortType.ACCESSED -> {
                    images.forEachIndexed { i, image ->
                        val currentDayMills = (image.lastAccessTime / oneDayMills) * oneDayMills
                        if (currentDayMills != time) {
                            time = currentDayMills
                            sorted.add(AlbumItemModel(MediaType.TITLE, lastAccessTime = time))
                        }
                        sorted.add(image)
                    }
                }
            }
            images.clear()
            images.addAll(sorted)
        }
    }
}