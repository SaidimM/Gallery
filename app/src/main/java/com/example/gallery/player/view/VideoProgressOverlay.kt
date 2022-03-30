package com.example.gallery.player.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.gallery.R
import kotlinx.android.synthetic.main.video_overlay_progress.view.*

class VideoProgressOverlay(context: Context, attributeSet: AttributeSet? = null, defStyleAttrs: Int = 0) :
    FrameLayout(context, attributeSet, defStyleAttrs) {
    private var duration = -1
    private var difference = -1
    private var startProgress = -1

    private val tag = this.javaClass.simpleName

    init {
        LayoutInflater.from(context).inflate(R.layout.video_overlay_progress, this)
    }

    fun updateProgress(diff: Int, currPosition: Int, duration: Int) {
        if (duration == 0) return
        if (startProgress == -1) {
            Log.i(tag, "show: start seek = $startProgress")
            startProgress = currPosition
        }
        position.visibility = VISIBLE
        differ.visibility = VISIBLE

        this.duration = duration
        this.difference += diff
        val targetProgress = getTargetProgress()
        position.text = targetProgress.toString()
        differ.text = difference.toString()
    }

    fun getTargetProgress(): Int {
        if (duration == -1) return -1
        var newSeekProgress = startProgress + difference
        if (newSeekProgress <= 0) newSeekProgress = 0
        if (newSeekProgress >= duration) newSeekProgress = duration
        return newSeekProgress
    }

    fun hide() {
        duration = -1
        startProgress = -1
        difference = -1
        visibility = GONE
        position.visibility = GONE
        differ.visibility = GONE
    }
}