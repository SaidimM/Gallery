package com.example.gallery.main

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.example.gallery.media.remote.lyrics.Lyric
import com.example.gallery.player.GeneralTools.dp
import org.jetbrains.anko.collections.forEachWithIndex

class LyricsView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttrs: Int) : super(
        context,
        attributeSet,
        defStyleAttrs
    )

    var data: ArrayList<Lyric> = arrayListOf()
        set(value) {
            field = value
            val array = arrayListOf<Lyric>()
            value.forEach { if (it.text != "") array.add(it) }
            field = array
            invalidate()
        }

    val paint = Paint().apply {
        color = Color.WHITE
        textSize = 24.dp.toFloat()
        textAlign = Paint.Align.LEFT
    }

    var scroll: Float = 0f
        set(value) {
            field = value
            if (field < 0) field = 0f
            invalidate()
        }

    var currentTime = 0
    private var currentPosition = 0
        set(value) {
            if (field == value) return
            val interval = data[value].position - data[field].position
            field = value
            if (field >= data.size - 3) {
                anim.pause()
                clearAnimation()
                return
            }
            anim.startDelay = interval.toLong()
            anim.start()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val lineInScreen = height / 50.dp
        val startIndex = (scroll / 50.dp).toInt()
        data.forEachWithIndex { i, lyric ->
            if (i > startIndex + lineInScreen|| i < startIndex) return@forEachWithIndex
            if (i - startIndex == 1) currentPosition = i
            val textWidth = paint.measureText(lyric.text)
            val y = (i + 0.5) * 50.dp - scroll
            canvas?.drawText(lyric.text, 16.dp.toFloat(), y.toFloat(), paint)
        }
    }

    var touchPoint = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchPoint = event.y
                performClick()
            }
            MotionEvent.ACTION_MOVE -> {
                scroll -= (event.y - touchPoint)
                touchPoint = event.y
            }
            MotionEvent.ACTION_UP -> {
                touchPoint = 0f
            }
        }
        return true
    }

    private val anim = ValueAnimator.ofFloat(0f, 50f).apply {
        addUpdateListener {
            val value = it.animatedValue as Float
            scroll += value
        }
        interpolator = AccelerateDecelerateInterpolator()
        duration = 1000
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    fun start() {
        if (currentPosition == 0) anim.startDelay = data[currentPosition].position.toLong()
        anim.start()
    }

    fun pause() {
        anim.pause()
    }
}