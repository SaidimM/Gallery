package com.example.gallery.base.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.gallery.Constants
import com.example.gallery.media.music.local.bean.Music
import java.io.File

object BindingAdapters {
    @BindingAdapter(value = ["setMusicCover"], requireAll = false)
    fun setMusicCover(imageView: ImageView, music: Music?) {
        if (music == null) return
        val path = Constants.getAlbumCoverPath(music)
        Glide.with(imageView.context).load(File(path)).into(imageView)
    }
}