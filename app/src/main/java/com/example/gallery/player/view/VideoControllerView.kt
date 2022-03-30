package com.example.gallery.player.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import com.example.gallery.R
import com.example.gallery.media.remote.mv.MusicVideoResult
import com.example.gallery.player.MyPlayer
import com.example.gallery.player.listener.OnVideoControlListener
import kotlinx.android.synthetic.main.video_media_controller.view.*


class VideoControllerView(context: Context, attributeSet: AttributeSet? = null, defStyleAttrs: Int = 0) :
    FrameLayout(context, attributeSet, defStyleAttrs) {
    private val title: TextView? = null

    // 默认显示时间3秒
    val DEFAULT_SHOW_TIME = 3000

    private val mShowing = false
    private val mAllowUnWifiPlay = false
    private val mDragging = false
    private val mDraggingProgress: Long = 0
    private val mPlayer: MyPlayer? = null
    private val videoInfo: MusicVideoResult? = null
    var onVideoControlListener: OnVideoControlListener? = null
    var onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener? = null
    var isPlaying: Boolean = false
        set(value) {
            field = value
            if (value) play.setBackgroundResource(R.drawable.ic_play)
            else play.setBackgroundResource(R.drawable.ic_pause)
        }
    var progress: Int = 0
        set(value) {
            field = value
            seekbar.progress = value
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.video_media_controller, null, false)
    }

    private fun initConteroller() {
        back.setOnClickListener { onVideoControlListener?.onBack() }
        seekbar.setOnSeekBarChangeListener(onSeekBarChangeListener)
        play.setOnClickListener {
            onVideoControlListener?.onStartPlay()
        }
    }

    fun show() {
        visibility = VISIBLE
        postDelayed({
            hide()
        }, DEFAULT_SHOW_TIME.toLong())
    }

    fun hide() { visibility = GONE }
}