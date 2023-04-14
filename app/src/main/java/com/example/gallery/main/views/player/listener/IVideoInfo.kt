package com.example.gallery.main.views.player.listener

import android.os.Parcelable
import java.io.Serializable

interface IVideoInfo : Serializable {
    fun getTitle(): String
    fun getPath(): String
}