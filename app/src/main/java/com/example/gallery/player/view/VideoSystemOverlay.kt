package com.example.gallery.player.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.example.gallery.R
import kotlinx.android.synthetic.main.video_overlay_system.view.*

class VideoSystemOverlay(context: Context, attributeSet: AttributeSet? = null, defStyleAttrs: Int = 0) :
    FrameLayout(context, attributeSet, defStyleAttrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.video_overlay_system, this)
    }

    fun hide() { visibility = View.GONE }

    fun updateVolume(max: Int, progress: Int) {
        val progressText = "当前音量：$progress"
        text.text = progressText

        volume_bar.max = max
        volume_bar.progress = progress
        visibility = View.VISIBLE
    }

    fun updateBrightness(max: Int, progress: Int) {
        val progressText = "当前亮度：$progress"
        text.text = progressText

        volume_bar.max = max
        volume_bar.progress = progress
        visibility = View.VISIBLE
    }
}