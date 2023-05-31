package com.example.gallery.main.model

import com.example.gallery.media.local.enums.SortType

data class AlbumSortModel(
    val sortType: SortType = SortType.CREATED,
    val isDescending: Boolean = true,
    val isShowTime: Boolean = true
)