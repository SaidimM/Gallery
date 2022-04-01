package com.example.gallery.player

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.gallery.R

class VideoProgressOverlay: FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttrs: Int) : super(
        context,
        attributeSet,
        defStyleAttrs
    )

    init {
        LayoutInflater.from(context).inflate(R.layout.video_overlay_progress, this)
    }

    fun updateProgress() {

    }
}