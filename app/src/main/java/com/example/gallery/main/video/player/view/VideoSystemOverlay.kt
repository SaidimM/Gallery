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

    private var volumeBar: VerticalProgressView
    private var lightBar: VerticalProgressView
    private var text: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.video_overlay_system, this)
        volumeBar = findViewById(R.id.volume_bar)
        lightBar = findViewById(R.id.light_bar)
        text = findViewById(R.id.text)
    }

    fun updateVolume(value: Int) {
        volumeBar.progress = value
        volumeBar.visibility = VISIBLE
        val valueText = "volume: $value%"
        text.text = valueText
    }

    fun updateBrightness(value: Int) {
        lightBar.progress = value
        lightBar.visibility = VISIBLE
        val valueText = "brightness: $value%"
        text.text = valueText
    }

    fun hide() {
        volumeBar.visibility = GONE
        lightBar.visibility = GONE
        text.text = ""
    }
}