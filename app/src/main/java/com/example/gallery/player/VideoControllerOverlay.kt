package com.example.gallery.player

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.widget.FrameLayout
import android.widget.SeekBar
import com.example.gallery.R
import kotlinx.android.synthetic.main.video_controller_overlay.view.*
import kotlinx.android.synthetic.main.view_player.view.*

class VideoControllerOverlay : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttrs: Int) : super(
        context,
        attributeSet,
        defStyleAttrs
    )

    private val tag = "VideoControllerOverlay"

    private val interval = 5000L

    private var dragging = false

    private var showing = false

    val player = Player()

    var videoInfo: IVideoInfo? = null
        set(value) {
            if (value == null) return
            field = value
            title.text = value.getTitle()
            player.path = value.getPath()
            player.initialize()
        }

    private val playerListener = object : PlayerListener {
        override fun onPrepared(mediaPlayer: MediaPlayer) { play() }

        override fun onLoadingChanged(isLoaded: Boolean) {
        }

        override fun onBufferinghChengedListener(mediaPlayer: MediaPlayer, percent: Int) {
        }

        override fun onCompletionListener(mediaPlayer: MediaPlayer) {
        }

        override fun onError(mp: MediaPlayer, what: Int, extra: Int) {
        }

        override fun onVideoSizeChanged(mediaPlayer: MediaPlayer, width: Int, height: Int) {
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.video_controller_overlay, this)
        play.setOnClickListener {
            if (player.isPlaying()) {
                player.pause()
                play.setBackgroundResource(R.drawable.ic_play)
            } else {
                player.start()
                play.setBackgroundResource(R.drawable.ic_pause)
            }
            show()
        }
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!dragging) return
                player.seekTo(player.getDuration() * progress / 100)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                dragging = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                show()
                dragging = false
            }
        })
        player.playerListener = playerListener
        back.setOnClickListener { (context as Activity).onBackPressed() }
    }

    fun setPlayerHolder(holder: SurfaceHolder) {
        player.holder = holder
        player.initialize()
    }

    fun play() {
        player.start()
        play.setBackgroundResource(R.drawable.ic_pause)
        show()
    }

    fun pause() {
        player.pause()
        play.setBackgroundResource(R.drawable.ic_play)
    }

    fun show() {
        visibility = VISIBLE
        postDelayed(action, interval)
        showing = true
        post(mShowProgress)
    }

    fun showProgress() {
        if (player.getDuration() == 0) return
        val durationText = "${GeneralTools.millisecondToString(player.getCurrentPosition(), false)} / ${
            GeneralTools.millisecondToString(player.getDuration(), false)
        }"
        duration.text = durationText
        val duration = player.getDuration()
        val position = player.getCurrentPosition()
        seekbar.progress = position * 100 / duration
    }

    fun hide() {
        visibility = GONE
        removeCallbacks(action)
        showing = false
    }

    private val action = Runnable {
        visibility = GONE
    }

    private val mShowProgress: Runnable = object : Runnable {
        override fun run() {
            showProgress()
            if (!dragging && showing && player.isPlaying()) {
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