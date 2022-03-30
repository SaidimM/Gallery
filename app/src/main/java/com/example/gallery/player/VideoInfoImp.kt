package com.example.gallery.player

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class VideoInfoImp(
    private var path: String,
    private var title: String
) : VideoInfo, Serializable {
    override fun getPath() = path
    override fun getTitle() = title

    fun setTitle(title: String) {
        this.title = title
    }

    fun setPath(path: String) {
        this.path = path
    }
}