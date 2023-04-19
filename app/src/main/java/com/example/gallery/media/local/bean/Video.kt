package com.example.gallery.media.local.bean

class Video(id: Int, path: String, name: String, resolution: String, size: Long, date: Long, duration: Long) {
    private var id = 0
    private var path: String = ""
    private var name: String = ""
    private var resolution: String = "" // 分辨率
    private var size: Long = 0
    private var date: Long = 0
    private var duration: Long = 0

    init {
        this.id = id
        this.path = path
        this.name = name
        this.resolution = resolution
        this.size = size
        this.date = date
        this.duration = duration
    }
}