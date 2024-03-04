package com.example.gallery.base.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.gallery.Constants.ALBUM_COVER_DIR
import com.example.gallery.media.music.local.bean.Music
import java.io.File

object BindingAdapters {
    @BindingAdapter(value = ["setMusicCover"], requireAll = false)
    fun setMusicCover(imageView: ImageView, music: Music?) {
        if (music == null) return
        val path = ALBUM_COVER_DIR + music.id + ".jpg"
        Glide.with(imageView.context).load(File(path)).into(imageView)
    }
}