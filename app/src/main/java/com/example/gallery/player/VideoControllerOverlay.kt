package com.example.gallery.player

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.SeekBar
import com.example.gallery.R
import kotlinx.android.synthetic.main.video_controller_overlay.view.*

class VideoControllerOverlay : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttrs: Int) : super(
        context,
        attributeSet,
        defStyleAttrs
    )

    private val tag = "VideoControllerOverlay"

    private val interval = 5000

    private var dragging = false

    private var showing = false

    init {
        LayoutInflater.from(context).inflate(R.layout.video_controller_overlay, this)
        play.setOnClickListener {
            if (player == null) {
                play.setBackgroundResource(R.drawable.ic_pause)
            } else if (player!!.isPlaying()) {
                player?.pause()
                play.setBackgroundResource(R.drawable.ic_play)
            } else {
                player?.start()
                play.setBackgroundResource(R.drawable.ic_pause)
            }
            show()
        }
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (player == null || !dragging) return
                player!!.seekTo(player!!.getDuration() * progress / 100)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                dragging = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                show()
                dragging = false
            }
        })
    }

    fun played() {
        play.setBackgroundResource(R.drawable.ic_pause)
    }

    fun paused() {
        play.setBackgroundResource(R.drawable.ic_play)
    }

    fun show() {
        visibility = VISIBLE
        postDelayed(action, 5000)
        showing = true
        post(mShowProgress)
    }

    fun showProgress() {
        if (player == null || player?.getDuration() == 0) return
        val durationText = "${GeneralTools.millisecondToString(player!!.getCurrentPosition(), false)} / ${
            GeneralTools.millisecondToString(player!!.getDuration(), false)
        }"
        duration.text = durationText
        val duration = player!!.getDuration()
        val position = player!!.getCurrentPosition()
        seekbar.progress = position * 100 / duration
    }

    fun hide() {
        visibility = GONE
        removeCallbacks(action)
        showing = false
    }

    val action = Runnable{
        visibility = GONE
    }

    private var player: Player? = null

    private var info: IVideoInfo? = null

    fun setPlayer(player: Player) {
        this.player = player
        if (info != null) show()
    }

    fun setInfo(info: IVideoInfo) {
        this.info = info
        title.text = info.getTitle()
        if (player != null) show()
    }

    private val mShowProgress: Runnable = object : Runnable {
        override fun run() {
            showProgress()
            if (!dragging && showing && player?.isPlaying() == true && player != null) {
                // 解决1秒之内的误差，使得发送消息正好卡在整秒
//                Log.e("TAG", "run: " + (1000 - pos % 1000))
                postDelayed(this, 1000)
            }
        }
    }

    fun release() {
        removeCallbacks(mShowProgress)
    }
}