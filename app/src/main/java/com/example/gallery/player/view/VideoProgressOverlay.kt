package com.example.gallery.player.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.gallery.R
import com.example.gallery.player.GeneralTools
import kotlinx.android.synthetic.main.video_overlay_progress.view.*

class VideoProgressOverlay : FrameLayout {

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

    fun updateProgress(currentPosition: Int, difference: Int) {
        val text = GeneralTools.millisecondToString(currentPosition)
        position.text = text
        var diff = GeneralTools.millisecondToString(difference)
        if (text == "00 : 00") diff = "00 : 00"
        val diffText = if (difference >= 0) "( + $diff )" else "( - $diff )"
        differ.text = diffText
        visibility = VISIBLE
    }
}