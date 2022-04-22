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
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.example.gallery.media.remote.lyrics.Lyric
import com.example.gallery.player.GeneralTools.dp

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

    private val paint = TextPaint().apply {
        color = Color.WHITE
        textSize = 24.dp.toFloat()
        textAlign = Paint.Align.LEFT
        color = Color.parseColor("#c4c4c4")
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }

    private var texts: ArrayList<StaticLayout> = arrayListOf()

    private var lineStartIndexes: ArrayList<Float> = arrayListOf()

    var data: ArrayList<Lyric> = arrayListOf()
        set(value) {
            field = value
            texts.clear()
            lineStartIndexes.clear()
            lineStartIndexes.add(0f)
            value.forEach {
                val layout = StaticLayout.Builder.obtain(
                    it.text,
                    0,
                    it.text.length,
                    paint,
                    width - 2 * lineMargin
                ).setLineSpacing(10f, 1.2f).build()
                texts.add(layout)
                val last = lineStartIndexes.last()
                lineStartIndexes.add(last + layout.height.toFloat() + spaceBetweenLine)
            }
            invalidate()
            start()
        }

    var currentTime = 0

    private var scroll: Float = 0f
        set(value) {
            if (texts.isEmpty() || lineStartIndexes.isEmpty()) return
            if (value > lineStartIndexes.last() - indexLineTop) return
            field = value
            if (field < 0) field = 0f
            invalidate()
        }

    private var currentPosition = 0
        set(value) {
            if (field == value) return
            field = value
            if (field >= data.size - 3) {
                anim?.pause()
                clearAnimation()
                return
            }
            startScroll()
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
        var startIndex = 0
        for (i in 0 until lineStartIndexes.size) {
            if (i == lineStartIndexes.size - 1) {
                startIndex = i
                break
            }
            if (scroll >= lineStartIndexes[i] && scroll < lineStartIndexes[i + 1]) {
                startIndex = i
                break
            }
        }
        val lineInScreen = height / 50.dp
        val transHeight = lineStartIndexes[startIndex] - scroll
        canvas?.translate(lineMargin.toFloat(), transHeight)
        val end = if (startIndex + lineInScreen >= lineStartIndexes.size - 1)
            lineStartIndexes.size - 1 else startIndex + lineInScreen
        for (i in startIndex until end) {
            val layout = texts[i]
            canvas?.translate(0f, spaceBetweenLine.toFloat() / 2)
            layout.draw(canvas)
            canvas?.translate(0f, spaceBetweenLine.div(2f) + layout.height)
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

    private var anim: ValueAnimator? = null

    private fun startScroll() {
        if (lineStartIndexes.isEmpty()) return
        val delay = data[currentPosition + 1].position.toLong() - data[currentPosition].position.toLong()
        val scrollingSpace = lineStartIndexes[currentPosition + 1] - lineStartIndexes[currentPosition]
        anim = ValueAnimator
            .ofFloat(0f, scrollingSpace).apply {
                duration = 720
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener {
                    val value = it.animatedValue as Float
                    Log.i("ValueAnimator", "value: $value, scroll:$scroll, scroll + value: ${scroll + value}")
                    scroll = lineStartIndexes[currentPosition] + value
                }
            }
        Log.i(
            "AnimationParams",
            "delay:$delay, scrollingSpace: $scrollingSpace, scroll: $scroll, position: $currentPosition"
        )
        anim?.start()
        postDelayed({
            currentPosition++
        }, delay)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    fun start() {
        if (currentPosition == 0) anim?.startDelay = data[currentPosition].position.toLong()
        startScroll()
    }

    fun pause() {
        anim?.pause()
    }
}