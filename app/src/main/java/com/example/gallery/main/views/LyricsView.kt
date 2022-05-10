package com.example.gallery.main.views

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
import android.view.GestureDetector
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

    private var indexLineTop = 118.dp

    private var spaceBetweenLine = 48.dp

    private val paint = TextPaint().apply {
        textSize = 32.dp.toFloat()
        textAlign = Paint.Align.LEFT
        color = Color.parseColor("#c4c4c4")
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
        alpha = 72
    }

    private val focusedPaint = TextPaint().apply {
        color = Color.WHITE
        textSize = 32.dp.toFloat()
        textAlign = Paint.Align.LEFT
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
        alpha = 72
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
            if (field >= data.size) {
                anim.pause()
                clearAnimation()
                return
            }
            startScroll()
        }

    private val nextPositionAction = Runnable {
        currentPosition++
        animateAlpha()
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
        val start = (startIndex - indexLineTop / 50.dp).let { if (it < 0 ) 0 else it }
        val end = if (startIndex + lineInScreen >= lineStartIndexes.size - 1)
            lineStartIndexes.size - 1 else startIndex + lineInScreen
        val indexLinePosition = lineStartIndexes[startIndex]
        val transHeight = indexLinePosition - scroll + indexLineTop
        canvas?.translate(lineMargin.toFloat(), transHeight + lineStartIndexes[start] - indexLinePosition)
        for (i in start until end) {
            if (data[i].text.isEmpty()) continue
            val layout = StaticLayout.Builder.obtain(
                data[i].text,
                0,
                data[i].text.length,
                if (i == currentPosition) focusedPaint else paint,
                width - 2 * lineMargin
            ).setLineSpacing(10f, 1.2f).build()
            canvas?.translate(0f, spaceBetweenLine.toFloat() / 2)
            layout.draw(canvas)
            canvas?.translate(0f, spaceBetweenLine.div(2f) + layout.height)
        }
    }

    private fun start() {
        val startPosition =
            if (currentPosition == 0) data[currentPosition].position.toLong()
            else data[currentPosition].position.toLong() - data[currentPosition - 1].position.toLong()
        postDelayed({ startScroll() }, startPosition)
        animateAlpha()
    }

    private var anim: ValueAnimator = ValueAnimator()

    private fun startScroll() {
        if (lineStartIndexes.isEmpty() || currentPosition + 1 == data.size) return
        val delay = data[currentPosition + 1].position.toLong() - data[currentPosition].position.toLong()
        anim.apply {
            setFloatValues(scroll, lineStartIndexes[currentPosition])
            duration = 720
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                val value = it.animatedValue as Float
                scroll = value
            }
        }
        if (!scrollAnimating) anim.start()
        else invalidate()
        postDelayed(nextPositionAction, delay)
    }

    private fun animateAlpha() {
        val alphaAnimation = ValueAnimator()
        alphaAnimation.apply {
            setIntValues(72, 255)
            duration = 480
            addUpdateListener {
                val value = it.animatedValue as Int
                focusedPaint.alpha = value
                invalidate()
            }
            start()
        }
    }

    private var scrollAnimating = false

    override fun performClick(): Boolean {
        Log.i(this.javaClass.simpleName, "perform click")
        return super.performClick()
    }

    private var dragging: Boolean = false

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        if (data.isEmpty()) return false
        gestureDetector.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                performClick()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                scrollAnimating = dragging
                dragging = false
                if (scrollAnimating) {
                    val delayMills = if (scroll - lineStartIndexes[currentPosition] < height) 1000 else 3000
                    postDelayed({ scrollAnimating = false }, delayMills.toLong())
                }
            }
        }
        return true
    }

    private fun doubleTap(event: MotionEvent?): Boolean {
        if (event == null) return false
        removeCallbacks(nextPositionAction)
        val tapPosition = (scroll + event.y - indexLineTop).let { if (it < 0) 0f else it }
        for (i in 0 until lineStartIndexes.size - 1) {
            if (tapPosition < lineStartIndexes[i] || tapPosition > lineStartIndexes[i + 1]) continue
            else {
                anim.pause()
                currentPosition = i
                break
            }
        }
        invalidate()
        return true
    }

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            scroll += distanceY
            dragging = true
            return true
        }

        override fun onDoubleTap(e: MotionEvent?) = doubleTap(e)
    })
}