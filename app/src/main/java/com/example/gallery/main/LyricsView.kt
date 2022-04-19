package com.example.gallery.main

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.StaticLayout
import android.text.TextPaint
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

    var lineMargin = 32.dp

    var indexLineTop = 144.dp

    var spaceBetweenLine = 48.dp

    val paint = TextPaint().apply {
        color = Color.WHITE
        textSize = 24.dp.toFloat()
        textAlign = Paint.Align.LEFT
        typeface = Typeface.DEFAULT_BOLD
    }

    private var texts: ArrayList<StaticLayout> = arrayListOf()

    private var lineStartIndexes: ArrayList<Int> = arrayListOf()

    var data: ArrayList<Lyric> = arrayListOf()
        set(value) {
            field = value
            texts.clear()
            lineStartIndexes.clear()
            lineStartIndexes.add(indexLineTop)
            value.forEach {
                val layout = StaticLayout.Builder.obtain(
                    it.text,
                    0,
                    it.text.length,
                    paint,
                    width - 2 * lineMargin
                ).setLineSpacing(10f, 1.2f).build()
                texts.add(layout)
                lineStartIndexes.add(layout.height + spaceBetweenLine)
            }
            invalidate()
        }

    var currentTime = 0

    var scroll: Float = 0f
        set(value) {
            field = value
            if (field < 0) field = 0f
            invalidate()
        }

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
        if (lineStartIndexes.isEmpty() || texts.isEmpty()) return
        val lineInScreen = 6
        val startIndex = if (currentPosition - 1 < 0) 0 else currentPosition - 1
        canvas?.translate(lineMargin.toFloat(), -(scroll - lineStartIndexes[startIndex]))
        for (i in startIndex until (startIndex + lineInScreen)) {
            val layout = texts[i]
            canvas?.translate(0f, lineMargin.toFloat())
            texts[i].draw(canvas)
            canvas?.translate(0f,  layout.height.toFloat() + lineMargin)
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