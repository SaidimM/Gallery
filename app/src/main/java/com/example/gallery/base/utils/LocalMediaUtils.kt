package com.example.gallery.base.utils

import LogUtil
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.blankj.utilcode.util.ArrayUtils
import com.blankj.utilcode.util.Utils
import com.example.gallery.Constants.LYRIC_DIR
import com.example.gallery.main.album.models.AlbumItemModel
import com.example.gallery.media.album.local.ImgFolderBean
import com.example.gallery.media.music.local.bean.Music
import com.example.gallery.media.album.local.Video
import com.example.gallery.media.music.local.enums.MediaType
import java.io.*
import java.util.*


object LocalMediaUtils {
    //定义一个集合，存放从本地读取到的内容
    var list: MutableList<Music> = arrayListOf()

    private val TAG = this.javaClass.simpleName
    fun getMusic(context: Context): List<Music> {
        val list = mutableListOf<Music>()
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val music = Music()
                var name =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                var singer =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val duration =
                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))
                val album =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                val albumId =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                //list.add(song);
                //把歌曲名字和歌手切割开
                //song.setName(name);
                music.singer = singer
                music.path = path
                music.duration = duration
                music.size = size
                music.id = id
                music.album = album
                music.albumId = albumId.toLong()
                if (size > 1000 * 800) {
                    if (name.contains("-")) {
                        val str = name.split("-".toRegex()).toTypedArray()
                        singer = str[0]
                        music.singer = singer
                        name = str[1]
                        if (name.indexOf('.') != -1) name = name.substring(0, name.indexOf('.'))
                        music.name = name
                    } else {
                        music.name = name
                    }
                    list.add(music)
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    suspend fun getVideos(): List<Video> {
        val videos: MutableList<Video> = ArrayList()
        var c: Cursor? = null
        try {
            // String[] mediaColumns = { "_id", "_data", "_display_name",
            // "_size", "date_modified", "duration", "resolution" };
            c = Utils.getApp().contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Video.Media.DEFAULT_SORT_ORDER
            )
            if (c == null) return emptyList()
            while (c.moveToNext()) {
                val path = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)) // 路径
                if (!File(path).exists()) {
                    continue
                }
                val id = c.getInt(c.getColumnIndexOrThrow(MediaStore.Video.Media._ID)) // 视频的id
                val name =
                    c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)) // 视频名称
                val resolution =
                    c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION)) //分辨率
                val size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)) // 大小
                val duration =
                    c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)) // 时长
                val date =
                    c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)) //修改时间
                val video = Video(id, path, name, resolution, size, date, duration)
                videos.add(video)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            c?.close()
        }
        return videos
    }

    // 获取视频缩略图
    fun getVideoThumbnail(id: Int): Bitmap? {
        var bitmap: Bitmap? = null
        val options = BitmapFactory.Options()
        options.inDither = false
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        bitmap = MediaStore.Video.Thumbnails.getThumbnail(
            Utils.getApp().contentResolver,
            id.toLong(),
            MediaStore.Images.Thumbnails.MICRO_KIND,
            options
        )
        return bitmap
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * 得到图片文件夹集合
     */
    private fun getImageFolders(): ArrayList<ImgFolderBean> {
        val folders: ArrayList<ImgFolderBean> = arrayListOf()
        // 扫描图片
        var c: Cursor? = null
        try {
            c = Utils.getApp().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Images.Media.MIME_TYPE + "= ? or " + MediaStore.Images.Media.MIME_TYPE + "= ? or " + MediaStore.Images.Media.MIME_TYPE + "= ?",
                arrayOf("image/jpeg", "image/png", "image/webp"),
                MediaStore.Images.Media.DATE_MODIFIED
            )
            if (c == null) return folders
            val mDirs: MutableList<String> = ArrayList() //用于保存已经添加过的文件夹目录
            while (c.moveToNext()) {
                val index = c.getColumnIndex(MediaStore.Images.Media.DATA)
                val path = c.getString(index) // 路径
                val parentFile = File(path).parentFile ?: continue
                val dir = parentFile.absolutePath
                if (mDirs.contains(dir)) //如果已经添加过
                    continue
                mDirs.add(dir) //添加到保存目录的集合中
                val folderBean = ImgFolderBean()
                folderBean.dir = dir
                folderBean.fistImgPath = path
                if (parentFile.list() == null) continue
                val count: Int = parentFile.list { _, filename ->
                    filename.endsWith(".jpeg") || filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(
                        ".webp"
                    )
                }!!.size
                folderBean.count = count
                folders.add(folderBean)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            c?.close()
        }
        return folders
    }

    /**
     * 通过图片文件夹的路径获取该目录下的图片
     */
    private fun getImgListByDir(dir: String): ArrayList<File> {
        val imgPaths = ArrayList<File>()
        val directory = File(dir)
        if (!directory.exists()) {
            return imgPaths
        }
        val files = directory.listFiles() ?: return imgPaths
        for (file in files) {
            if (isPicFile(file.path)) {
                imgPaths.add(file)
            }
        }
        return imgPaths
    }

    private fun isPicFile(file: String): Boolean {
        //文件名后缀，即文件类型
        val index = file.lastIndexOf(".")
        if (index == -1) return false
        val suffixName = file.substring(index)
        val s = arrayOf(".jpg", ".png", ".jpeg", ".webp")
        return ArrayUtils.contains(s, suffixName.lowercase(Locale.getDefault()))
    }

    private fun getFileListByFolder(folder: ImgFolderBean): ArrayList<AlbumItemModel> {
        val models = arrayListOf<AlbumItemModel>()
        val files: ArrayList<File> = getImgListByDir(folder.dir)
        try {
            files.forEach { file ->
                val model = AlbumItemModel(
                    mediaType = MediaType.IMAGE,
                    uri = Uri.fromFile(file),
                    isSelected = false,
                    foldrName = if (file.parent == null) "" else file.parent.toString(),
                    createdTime = file.lastModified(),
                    lastEditedTime = file.lastModified(),
                    lastAccessTime = file.lastModified()
                )
                models.add(model)
                LogUtil.d(TAG, model.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return models
    }

    fun getAllImageFiles(): ArrayList<AlbumItemModel> {
        val files = arrayListOf<AlbumItemModel>()
        val folders = getImageFolders()
        folders.forEach { files.addAll(getFileListByFolder(it)) }
        files.sortBy { -it.createdTime }
        return files
    }
}