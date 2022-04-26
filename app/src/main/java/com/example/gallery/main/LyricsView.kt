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

    private var lineMargin = 32.dp

    private var indexLineTop = 144.dp

    private var spaceBetweenLine = 48.dp

    private val paint = TextPaint().apply {
        color = Color.WHITE
        textSize = 24.dp.toFloat()
        textAlign = Paint.Align.LEFT
        color = Color.parseColor("#c4c4c4")
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }

    private var lineStartIndexes: ArrayList<Float> = arrayListOf()

    var data: ArrayList<Lyric> = arrayListOf()
        set(value) {
            field = value
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
                val last = lineStartIndexes.last()
                if (it.text.isEmpty()) lineStartIndexes.add(last)
                else lineStartIndexes.add(last + layout.height.toFloat() + spaceBetweenLine)
            }
            invalidate()
            start()
        }

    var currentTime = 0

    private var scroll: Float = 0f
        set(value) {
            if (data.isEmpty() || lineStartIndexes.isEmpty()) return
            if (value > lineStartIndexes.last() - indexLineTop) return
            field = value
            if (field < 0) field = 0f
            invalidate()
        }

    private var currentPosition = 0
        set(value) {
            if (field == value) return
            field = value
            if (field >= data.size - 1) {
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
        if (lineStartIndexes.isEmpty() || data.isEmpty()) return
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
            if (data[i].text.isEmpty()) continue
            val layout = StaticLayout.Builder.obtain(
                data[i].text,
                0,
                data[i].text.length,
                paint,
                width - 2 * lineMargin
            ).setLineSpacing(10f, 1.2f).build()
            canvas?.translate(0f, spaceBetweenLine.toFloat() / 2)
            if (i != currentPosition) layout.draw(canvas)
            else {
                paint.color = Color.WHITE
                layout.draw(canvas)
                paint.color = Color.parseColor("#c4c4c4")
            }
            canvas?.translate(0f, spaceBetweenLine.div(2f) + layout.height)
        }
    }

    private var touchPoint = 0f

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
        val scrollPosition = if (currentPosition < 2) 0 else currentPosition - 2
        val delay = data[currentPosition + 1].position.toLong() - data[currentPosition].position.toLong()
        val scrollingSpace = lineStartIndexes[scrollPosition + 1] - lineStartIndexes[scrollPosition]
        anim = ValueAnimator
            .ofFloat(0f, scrollingSpace).apply {
                duration = 720
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener {
                    val value = it.animatedValue as Float
                    val position = if (currentPosition < 2) 0 else currentPosition - 2
                    scroll = lineStartIndexes[position] + value
                }
            }
        if (currentPosition >= 2) anim?.start()
        else invalidate()
        postDelayed({
            currentPosition++
        }, delay)
    }

    override fun performClick(): Boolean {
        Log.i(this.javaClass.simpleName, "perform click")
        return super.performClick()
    }

    private fun start() {
        val startPosition =
            if (currentPosition == 0) data[currentPosition].position.toLong()
            else data[currentPosition].position.toLong() - data[currentPosition - 1].position.toLong()
        postDelayed({ startScroll() }, startPosition)
    }

    fun pause() {
        anim?.pause()
    }
}