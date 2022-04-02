package com.example.gallery.player

import android.os.Parcelable
import java.io.Serializable

interface IVideoInfo : Serializable {
    fun getTitle(): String
    fun getPath(): String
}