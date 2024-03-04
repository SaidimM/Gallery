package com.example.gallery.base.utils

import LogUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.example.gallery.Constants
import com.example.gallery.R
import com.example.gallery.base.utils.AlbumCoverUtils.getArtwork
import com.example.gallery.media.music.local.bean.Music
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ViewUtils {

    const val TAG = "ViewUtils"
    suspend fun loadAlbumCover(
        item: Music,
        imageView: ImageView,
        with: Int = imageView.width,
        height: Int = imageView.height
    ) {
        getAlbumBitmap(item)
            .catch {
                coroutineScope {
                    launch(Dispatchers.Main) {
                        Glide.with(imageView).load(R.drawable.ic_music).override(with, height).into(imageView)
                    }
                }
            }
            .collect {
                coroutineScope {
                    launch(Dispatchers.Main) {
                        Glide.with(imageView).load(it).override(with, height).into(imageView)
                    }
                }
            }
    }

    private suspend fun getAlbumBitmap(music: Music) = flow<Bitmap> {
        val albumCoverPath = Constants.ALBUM_COVER_DIR + "${music.id}.jpg"
        val isFileExists = File(albumCoverPath).exists()
        val bitmap = withContext(Dispatchers.Main) {
            if (isFileExists) BitmapFactory.decodeFile(albumCoverPath)
            else getArtwork(Utils.getApp(), music.id, music.albumId, allowdefalut = true, small = false)
        } ?: error("couldn't get album cover!")
        if (!isFileExists) saveAlbumCover(music, bitmap)
        emit(bitmap)
    }

    private fun saveAlbumCover(music: Music, bitmap: Bitmap) {
        try {
            val albumCoverPath = Constants.ALBUM_COVER_DIR + "${music.id}.jpg"
            FileOutputStream(albumCoverPath).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun View.setMargins(start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0) {
        val parent = this.parent
        val layoutParams = this.layoutParams
        when (parent) {
            is ConstraintLayout -> {
                (layoutParams as ConstraintLayout.LayoutParams).setMargins(start, top, end, bottom)
            }

            is LinearLayout -> {
                (layoutParams as LinearLayout.LayoutParams).setMargins(start, top, end, bottom)
            }

            is FrameLayout -> {
                (layoutParams as FrameLayout.LayoutParams).setMargins(start, top, end, bottom)
            }
        }
        this.layoutParams = layoutParams
    }

    fun View.setHeight(height: Int) {
        val layoutParams = this.layoutParams
        layoutParams.height = height
        this.layoutParams = layoutParams
    }

    fun View.setHeight(height: Float) {
        val layoutParams = this.layoutParams
        layoutParams.height = height.toInt()
        this.layoutParams = layoutParams
    }

    fun View.setWidth(width: Int) {
        val layoutParams = this.layoutParams
        layoutParams.width = width
        this.layoutParams = layoutParams
    }

    fun View.setSizes(width: Int, height: Int) {
        val layoutParams = this.layoutParams
        layoutParams.width = width
        layoutParams.height = height
        this.layoutParams = layoutParams
    }

    fun View.toSizeString() = "height: ${this.measuredHeight}, top: ${this.top}"

    val Int.dp: Int
        get() = run {
            return toFloat().dp
        }

    val Float.dp: Int
        get() = run {
            val scale: Float = Utils.getApp().resources.displayMetrics.density
            return (this * scale + 0.5f).toInt()
        }

    val Int.px: Int
        get() = run {
            return toFloat().px
        }

    val Float.px: Int
        get() = run {
            val scale: Float = Utils.getApp().resources.displayMetrics.density
            return ((this - 0.5f) / scale).toInt()
        }
}
