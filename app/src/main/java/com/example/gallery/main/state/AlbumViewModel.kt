package com.example.gallery.main.state

import androidx.lifecycle.*
import com.example.gallery.base.utils.LocalMediaUtils
import com.example.gallery.main.model.AlbumSortModel
import com.example.gallery.main.model.AlbumItemModel
import com.example.gallery.media.ImageRepository
import com.example.gallery.media.MusicRepository
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

    private val _sortOption = MutableLiveData(AlbumSortModel())
    val sortOption: LiveData<AlbumSortModel> = _sortOption

    private val _album: MutableLiveData<ArrayList<AlbumItemModel>> = MutableLiveData()
    val album: LiveData<ArrayList<AlbumItemModel>> = _album

    fun getImages() {
        viewModelScope.launch {
            val items = withContext(Dispatchers.IO) { imageRepository.getImages() }
            sortImages(items, _sortOption.value!!)
            _album.postValue(items)
        }
    }

    private fun sortImages(images: ArrayList<AlbumItemModel>, albumSortModel: AlbumSortModel): MutableLiveData<ArrayList<AlbumItemModel>> {
        val result: MutableLiveData<ArrayList<AlbumItemModel>> = MutableLiveData()
        viewModelScope.launch {
            launch {
                images.sortBy {
                    when(albumSortModel.sortType) {
                        SortType.CREATED -> -it.createdTime
                        SortType.EDITED -> -it.lastEditedTime
                        SortType.ACCESSED -> -it.lastAccessTime
                    }
                }
            }
            launch { if (!albumSortModel.isDescending) images.reverse() }
            launch {
                if (albumSortModel.isShowTime) {
                    var time = 0L
                    val oneDayMills = 24 * 60 * 60 * 1000
                    if (albumSortModel.sortType == SortType.CREATED) {
                        images.forEachWithIndex { i, image ->
                            if ((image.createdTime / oneDayMills) * oneDayMills != time) {
                                time = (image.createdTime / oneDayMills) * oneDayMills
                                images.add(i, AlbumItemModel(MediaType.TITLE, createdTime = time))
                            }
                        }
                    } else if (albumSortModel.sortType == SortType.EDITED) {
                        images.forEachWithIndex { i, image ->
                            if ((image.lastEditedTime / oneDayMills) * oneDayMills != time) {
                                time = (image.lastEditedTime / oneDayMills) * oneDayMills
                                images.add(i, AlbumItemModel(MediaType.TITLE, lastEditedTime = time))
                            }
                        }
                    } else if (albumSortModel.sortType == SortType.ACCESSED) {
                        images.forEachWithIndex { i, image ->
                            if ((image.lastAccessTime / oneDayMills) * oneDayMills != time) {
                                time = (image.lastAccessTime / oneDayMills) * oneDayMills
                                images.add(i, AlbumItemModel(MediaType.TITLE, lastAccessTime = time))
                            }
                        }
                    }
                }
            }
            result.postValue(images)
        }
        return result
    }
}