package com.example.gallery.player

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.widget.FrameLayout
import com.example.gallery.R
import kotlinx.android.synthetic.main.view_player.view.*

class VideoPlayerView: FrameLayout {
    constructor(context: Context): super(context)
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttrs: Int): super(context, attributeSet, defStyleAttrs)

    private val player = Player()
    private val playerListener = object: PlayerListener{
        override fun onPrepared(mediaPlayer: MediaPlayer) {
            player.start()
        }

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
    var path: String = ""
        set(value) {
            field = value
            player.path = value
            player.initialize()
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_player, this)
        surface.holder.addCallback(object: SurfaceHolder.Callback{
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {}
            override fun surfaceCreated(holder: SurfaceHolder) {
                player.holder = holder
                player.initialize()
            }
        })
        player.playerListener = playerListener
    }
}