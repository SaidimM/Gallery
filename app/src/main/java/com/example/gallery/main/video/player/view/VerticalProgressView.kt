package com.example.gallery.main.video.player.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.blankj.utilcode.util.Utils

class VerticalProgressView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttrs: Int) : super(
        context,
        attributeSet,
        defStyleAttrs
    )

    private var backColor: Int = Color.GRAY

    private var progressColor: Int = Color.WHITE

    var defaultWidth = 8.dp

    var defaultHeight = 192.dp

    private val paint = Paint().apply {
        strokeWidth = width.toFloat()
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
    }

    var progress = 0
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val paintPadding = width.toFloat() / 2
        paint.color = backColor
        paint.strokeWidth = width.toFloat()
        canvas?.drawLine(paintPadding, paintPadding, paintPadding, height.toFloat() - paintPadding, paint)
        if (progress == 0) return
        paint.color = progressColor
        val paintHeight = (height - width) * (100 - progress) / 100
        canvas?.drawLine(paintPadding, height.toFloat() - paintPadding, paintPadding, paintHeight + paintPadding, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val with = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.UNSPECIFIED -> defaultWidth
            MeasureSpec.AT_MOST -> defaultWidth
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(widthMeasureSpec)
            else -> defaultWidth
        }
        val height = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.UNSPECIFIED -> defaultHeight
            MeasureSpec.AT_MOST -> defaultHeight
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(heightMeasureSpec)
            else -> defaultHeight
        }
        setMeasuredDimension(with, height)
    }

    private val Int.dp: Int get() = run {
        return toFloat().dp
    }
    private val Float.dp: Int get() = run {
        val scale: Float = Utils.getApp().resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }
}