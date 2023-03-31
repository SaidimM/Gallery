package com.example.gallery.base.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.gallery.Strings.ALBUM_COVER_DIR
import com.example.gallery.media.local.Music
import java.io.File

object BindingAdapters {
    @BindingAdapter(value = ["setMusicCover"], requireAll = false)
    fun setMusicCover(imageView: ImageView, music: Music?) {
        if (music == null) return
        val path = ALBUM_COVER_DIR + music.mediaAlbumId + ".jpg"
        Glide.with(imageView.context).load(File(path)).into(imageView)
    }
}