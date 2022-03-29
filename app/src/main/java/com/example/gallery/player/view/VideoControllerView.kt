package com.example.gallery.player.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.example.gallery.R
import com.example.gallery.media.remote.mv.MusicVideoResult
import com.example.gallery.player.listener.OnVideoControlListener


class VideoControllerView(context: Context, attributeSet: AttributeSet? = null, defStyleAttrs: Int = 0) : FrameLayout(context, attributeSet, defStyleAttrs) {
    private val title: TextView? = null

    // 默认显示时间3秒
    val DEFAULT_SHOW_TIME = 3000

    private val mShowing = false
    private val mAllowUnWifiPlay = false
    private val mDragging = false
    private val mDraggingProgress: Long = 0
//    private val mPlayer: JsMediaPlayer? = null
    private val videoInfo: MusicVideoResult? = null
    private val onVideoControlListener: OnVideoControlListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.video_media_controller, null, false)
    }
}