package com.example.gallery.base.utils

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.example.gallery.R
import com.example.gallery.Strings.LYRIC_DIR
import com.example.gallery.media.local.Music
import kotlinx.coroutines.CoroutineScope
import java.io.*
import kotlin.coroutines.CoroutineContext

object LocalMusicUtils {
    //定义一个集合，存放从本地读取到的内容
    var list: MutableList<Music> = arrayListOf()
    var music: Music? = null
    private var name: String = ""
    private var singer: String = ""
    private var path: String = ""
    private var duration = 0
    private var size: Long = 0
    private var albumId: Long = 0
    private var id: Long = 0

    private val TAG = this.javaClass.simpleName

    //获取专辑封面的Uri
    private val albumArtUri = Uri.parse("content://media/external/audio/albumart")
    fun getMusic(context: Context): ArrayList<Music> {
        val list = arrayListOf<Music>()
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                music = Music()
                name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))
                albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                //list.add(song);
                //把歌曲名字和歌手切割开
                //song.setName(name);
                music!!.singer = singer
                music!!.path = path
                music!!.duration = duration
                music!!.size = size
                music!!.id = id
                music!!.albumId = albumId
                if (size > 1000 * 800) {
                    if (name.contains("-")) {
                        val str = name.split("-".toRegex()).toTypedArray()
                        singer = str[0]
                        music!!.singer = singer
                        name = str[1]
                        if (name.indexOf('.') != -1) name = name.substring(0, name.indexOf('.'))
                        music!!.name = name
                    } else {
                        music!!.name = name
                    }
                    list.add(music!!)
                }
            }
        }
        cursor!!.close()
        return list
    }

    //    转换歌曲时间的格式
    fun formatTime(time: Int): String {
        return if (time / 1000 % 60 < 10) {
            (time / 1000 / 60).toString() + ":0" + time / 1000 % 60
        } else {
            (time / 1000 / 60).toString() + ":" + time / 1000 % 60
        }
    }

    /**
     * 获取专辑封面位图对象
     * @param context
     * @param song_id
     * @param album_id
     * @param allowdefalut
     * @return
     */
    fun getArtwork(context: Context, song_id: Long, album_id: Long, allowdefalut: Boolean, small: Boolean): Bitmap? {
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
        try {
            val options = BitmapFactory.Options()
            var fd: FileDescriptor? = null
            if (albumid < 0) {
                val uri = Uri.parse(
                    "content://media/external/audio/media/"
                            + songid + "/albumart"
                )
                val pfd = context.contentResolver.openFileDescriptor(uri, "r")
                if (pfd != null) {
                    fd = pfd.fileDescriptor
                }
            } else {
                val uri = ContentUris.withAppendedId(albumArtUri, albumid)
                val pfd = context.contentResolver.openFileDescriptor(uri, "r")
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

    /**
     * 根据专辑ID获取专辑封面图
     * @param album_id 专辑ID
     * @return
     */
    fun getAlbumArt(context: Context, album_id: Long): String? {
        val mUriAlbums = "content://media/external/audio/albums"
        val projection = arrayOf("album_art")
        val cur = context.contentResolver.query(
            Uri.parse("$mUriAlbums/$album_id"),
            projection,
            null,
            null,
            null
        )
        var album_art: String? = null
        if (cur!!.count > 0 && cur.columnCount > 0) {
            cur.moveToNext()
            album_art = cur.getString(0)
        }
        cur.close()
        var path: String? = null
        if (album_art != null) {
            path = album_art
        } else {
            //path = "drawable/music_no_icon.png";
            //bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_cover);
        }
        return path
    }

    /**
     * bitmap保存为file
     */
    @Throws(IOException::class)
    fun bitmapToFile(
        filePath: String,
        bitmap: Bitmap?, quality: Int
    ): File? {
        if (bitmap != null) {
            val file = File(
                filePath.substring(
                    0,
                    filePath.lastIndexOf(File.separator)
                )
            )
            if (!file.exists()) {
                file.mkdirs()
            }
            val bos = BufferedOutputStream(
                FileOutputStream(filePath)
            )
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos)
            bos.flush()
            bos.close()
            return file
        }
        return null
    }

    fun writeStringToFile(path: String, context: String) {
        val saveFile = File(path)
        if (!File(LYRIC_DIR).exists()) File(LYRIC_DIR).mkdir()
        if (!saveFile.exists()) saveFile.createNewFile()
        var fo: FileOutputStream? = null
        try {
            fo = FileOutputStream(saveFile)
            fo.write(context.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fo!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun readFile(path: String): String {
        val getFile = File(path)
        var fs: FileInputStream? = null
        var content = ""
        try {
            fs = FileInputStream(getFile)
            val length = fs.available()
            val bytes = ByteArray(length)
            fs.read(bytes)
            content = String(bytes, Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fs!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return content
    }
}