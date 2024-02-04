package com.example.gallery.base.utils

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.example.gallery.R
import java.io.FileDescriptor
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

object AlbumCoverUtils {

    //获取专辑封面的Uri
    private val albumArtUri = Uri.parse("content://media/external/audio/albumart")


    /**
     * 获取专辑封面位图对象
     * @param context
     * @param song_id
     * @param album_id
     * @param allowdefalut
     * @return
     */
    fun getArtwork(
        context: Context,
        song_id: Long,
        album_id: Long,
        allowdefalut: Boolean,
        small: Boolean
    ): Bitmap? {
        if (album_id < 0) {
            if (song_id < 0) {
                val bm = getArtworkFromFile(context, song_id, -1)
                if (bm != null) {
                    return bm
                }
            }
            return if (allowdefalut) {
                getDefaultArtwork(context, small)
            } else null
        }
        val res = context.contentResolver
        val uri = ContentUris.withAppendedId(albumArtUri, album_id)
        if (uri != null) {
            var `in`: InputStream? = null
            return try {
                `in` = res.openInputStream(uri)
                val options = BitmapFactory.Options()
                //先制定原始大小
                options.inSampleSize = 1
                //只进行大小判断
                options.inJustDecodeBounds = true
                //调用此方法得到options得到图片的大小
                BitmapFactory.decodeStream(`in`, null, options)
                /** 我们的目标是在你N pixel的画面上显示。 所以需要调用computeSampleSize得到图片缩放的比例  */
                /** 这里的target为800是根据默认专辑图片大小决定的，800只是测试数字但是试验后发现完美的结合  */
                if (small) {
                    options.inSampleSize = computeSampleSize(options, 40)
                } else {
                    options.inSampleSize = computeSampleSize(options, 600)
                }
                // 我们得到了缩放比例，现在开始正式读入Bitmap数据
                options.inJustDecodeBounds = false
                options.inDither = false
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                `in` = res.openInputStream(uri)
                BitmapFactory.decodeStream(`in`, null, options)
            } catch (e: FileNotFoundException) {
                var bm = getArtworkFromFile(context, song_id, album_id)
                if (bm != null) {
                    if (bm.config == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false)
                        if (bm == null && allowdefalut) {
                            return getDefaultArtwork(context, small)
                        }
                    }
                } else if (allowdefalut) {
                    bm = getDefaultArtwork(context, small)
                }
                bm
            } finally {
                try {
                    `in`?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    /**
     * 从文件当中获取专辑封面位图
     * @param context
     * @param songid
     * @param albumid
     * @return
     */
    private fun getArtworkFromFile(context: Context, songid: Long, albumid: Long): Bitmap? {
        var bm: Bitmap? = null
        require(!(albumid < 0 && songid < 0)) { "Must specify an album or a song id" }
        val uri: Uri
        var pfd: ParcelFileDescriptor? = null
        try {
            val options = BitmapFactory.Options()
            var fd: FileDescriptor? = null
            if (albumid < 0) {
                uri = Uri.parse(
                    "content://media/external/audio/media/"
                            + songid + "/albumart"
                )
                pfd = context.contentResolver.openFileDescriptor(uri, "r")
                if (pfd != null) {
                    fd = pfd.fileDescriptor
                }
            } else {
                uri = ContentUris.withAppendedId(albumArtUri, albumid)
                pfd = context.contentResolver.openFileDescriptor(uri, "r")
                if (pfd != null) {
                    fd = pfd.fileDescriptor
                }
            }
            options.inSampleSize = 1
            // 只进行大小判断
            options.inJustDecodeBounds = true
            // 调用此方法得到options得到图片大小
            BitmapFactory.decodeFileDescriptor(fd, null, options)
            // 我们的目标是在800pixel的画面上显示
            // 所以需要调用computeSampleSize得到图片缩放的比例
            options.inSampleSize = 100
            // 我们得到了缩放的比例，现在开始正式读入Bitmap数据
            options.inJustDecodeBounds = false
            options.inDither = false
            options.inPreferredConfig = Bitmap.Config.ARGB_8888

            //根据options参数，减少所需要的内存
            bm = BitmapFactory.decodeFileDescriptor(fd, null, options)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            pfd?.close()
        }
        return bm
    }

    /**
     * 获取默认专辑图片
     * @param context
     * @return
     */
    fun getDefaultArtwork(context: Context, small: Boolean): Bitmap? {
        val opts = BitmapFactory.Options()
        opts.inPreferredConfig = Bitmap.Config.RGB_565
        if (small) {    //返回小图片
            //return
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_music, opts)
        }
        //return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.defaultalbum), null, opts);
        return null
    }

    /**
     * 对图片进行合适的缩放
     * @param options
     * @param target
     * @return
     */
    fun computeSampleSize(options: BitmapFactory.Options, target: Int): Int {
        val w = options.outWidth
        val h = options.outHeight
        val candidateW = w / target
        val candidateH = h / target
        var candidate = candidateW.coerceAtLeast(candidateH)
        if (candidate == 0) {
            return 1
        }
        if (candidate > 1) {
            if (w > target && w / candidate < target) {
                candidate -= 1
            }
        }
        if (candidate > 1) {
            if (h > target && h / candidate < target) {
                candidate -= 1
            }
        }
        return candidate
    }
}