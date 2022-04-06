package com.example.gallery.player

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.gallery.R
import kotlinx.android.synthetic.main.video_overlay_system.view.*

class VideoSystemOverlay: FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttrs: Int) : super(
        context,
        attributeSet,
        defStyleAttrs
    )

    init {
        LayoutInflater.from(context).inflate(R.layout.video_overlay_system, this)
    }

    fun updateVolume(value: Int) {
        volume_bar.progress = value
        volume_bar.visibility = VISIBLE
    }

    fun updateBrightness(value: Int) {
        light_bar.progress = value
        light_bar.visibility = VISIBLE
    }

    fun hide() {
        volume_bar.visibility = GONE
        light_bar.visibility = GONE
    }
}