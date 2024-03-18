package com.example.gallery.media.album.local

class ImgFolderBean(
    /**当前文件夹的路径 */
    var dir: String = "",
    /**第一张图片的路径，用于做文件夹的封面图片 */
    var fistImgPath: String = "",
    /**文件夹名 */
    var name: String = "",
    /**文件夹中图片的数量 */
    var count: Int = 0
) 