package com.example.gallery.main.model

import com.example.gallery.media.local.enums.SortType

data class AlbumSortModel(
    var sortType: SortType = SortType.CREATED,
    var isDescending: Boolean = true,
    var splitByTime: Boolean = true
)