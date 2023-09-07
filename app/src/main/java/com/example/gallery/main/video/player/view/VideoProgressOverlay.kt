package com.example.gallery.main.video.player.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.example.gallery.R
import com.example.gallery.base.utils.GeneralUtils
import com.example.gallery.base.utils.GeneralUtils.millisecondToString

class VideoProgressOverlay : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttrs: Int) : super(
        context,
        attributeSet,
        defStyleAttrs
    )

    private var position: TextView
    private var differ: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.video_overlay_progress, this)
        position = findViewById(R.id.position)
        differ = findViewById(R.id.differ)
    }

    fun updateProgress(currentPosition: Int, difference: Int) {
        val text = currentPosition.millisecondToString()
        position.text = text
        var diff = difference.millisecondToString()
        if (text == "00 : 00") diff = "00 : 00"
        val diffText = if (difference >= 0) "( + $diff )" else "( - $diff )"
        differ.text = diffText
        visibility = VISIBLE
    }
}