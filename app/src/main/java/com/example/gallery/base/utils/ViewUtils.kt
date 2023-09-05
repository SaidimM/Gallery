package com.example.gallery.base.utils

import LogUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.example.gallery.Strings
import com.example.gallery.media.local.bean.Music
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object ViewUtils {

    const val TAG = ""
    suspend fun loadAlbumCover(item: Music, imageView: ImageView) {
        getAlbumBitmap(item)
            .catch { LogUtil.e(TAG, it.message.toString()) }
            .collect { Glide.with(imageView).load(it).into(imageView) }
    }

    suspend fun getAlbumBitmap(music: Music) = flow<Bitmap> {
        val albumCoverPath = Strings.ALBUM_COVER_DIR + "${music.mediaAlbumId}.jpg"
        val bitmap = withContext(Dispatchers.IO) {
            if (File(albumCoverPath).exists()) {
                BitmapFactory.decodeFile(albumCoverPath)
            } else LocalMediaUtils.getArtwork(
                Utils.getApp(),
                music.id,
                music.albumId,
                allowdefalut = true,
                small = false
            )
        } ?: error("couldn't get album cover!")
        emit(bitmap)
    }
}
