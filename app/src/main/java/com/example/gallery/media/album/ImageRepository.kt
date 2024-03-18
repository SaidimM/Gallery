package com.example.gallery.media.album

import com.example.gallery.base.utils.LocalMediaUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageRepository {
    companion object {
        private var repository: ImageRepository? = null
            get() {
                if (field == null) field = ImageRepository()
                return field
            }

        fun getInstance() = repository!!
    }

    suspend fun getImages() = withContext(Dispatchers.IO) { LocalMediaUtils.getAllImageFiles() }
}