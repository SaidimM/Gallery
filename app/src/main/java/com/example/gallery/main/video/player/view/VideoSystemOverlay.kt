package com.example.gallery.main.video.player.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.example.gallery.R

class VideoSystemOverlay : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttrs: Int) : super(
        context,
        attributeSet,
        defStyleAttrs
    )

    private lateinit var volume_bar: VerticalProgressView
    private lateinit var light_bar: VerticalProgressView
    private lateinit var text: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.video_overlay_system, this)
    }

    fun updateVolume(value: Int) {
        volume_bar.progress = value
        volume_bar.visibility = VISIBLE
        val valueText = "volume: $value%"
        text.text = valueText
    }

    fun updateBrightness(value: Int) {
        light_bar.progress = value
        light_bar.visibility = VISIBLE
        val valueText = "brightness: $value%"
        text.text = valueText
    }

    fun hide() {
        volume_bar.visibility = GONE
        light_bar.visibility = GONE
        text.text = ""
    }
}