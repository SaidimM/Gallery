package com.example.gallery.base.utils

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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object ViewUtils {
    suspend fun loadAlbumCover(item: Music, imageView: ImageView): Bitmap {
        var bitmap: Bitmap?
        coroutineScope {
            val albumCoverPath = Strings.ALBUM_COVER_DIR + "${item.mediaAlbumId}.jpg"
            bitmap = withContext(Dispatchers.IO) {
                if (File(albumCoverPath).exists()) {
                    BitmapFactory.decodeFile(albumCoverPath)
                } else LocalMediaUtils.getArtwork(
                    Utils.getApp(),
                    item.id,
                    item.albumId,
                    allowdefalut = true,
                    small = false
                )
            }
            launch(Dispatchers.Main) {
                Glide.with(imageView).load(bitmap).into(imageView)
            }
        }
        return bitmap!!
    }
}
