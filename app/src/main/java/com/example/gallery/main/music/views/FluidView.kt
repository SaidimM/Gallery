package com.example.gallery.main.music.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.graphics.withRotation
import androidx.palette.graphics.Palette
import com.blankj.utilcode.util.ScreenUtils
import com.example.gallery.R
import com.example.gallery.base.utils.ViewUtils.setWidth
import kotlin.math.sqrt

class FluidView(context: Context, attributeSet: AttributeSet? = null) : FrameLayout(context, attributeSet) {
    private val TAG = "FluidView"

    private val ANIMATION_INTERVAL = 3000

    private var palette: Palette? = null

    private var rotatedAngle = 0f
    private val animation = ValueAnimator.ofFloat(0f, 360f).apply {
        duration = ANIMATION_INTERVAL.toLong()
        repeatCount = Animation.INFINITE
        repeatMode = ValueAnimator.RESTART
        interpolator = LinearInterpolator()
        addUpdateListener {
            rotatedAngle = it.animatedValue as Float
            invalidate()
        }
    }

    private val radius by lazy { sqrt((ScreenUtils.getScreenWidth() * ScreenUtils.getScreenWidth() + ScreenUtils.getScreenHeight() * ScreenUtils.getScreenHeight()).toDouble()).toInt() }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(radius, radius)
    }

    fun initBackground(bitmap: Bitmap) {
        palette = Palette.from(bitmap).generate()
        animation.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawRectBottom(canvas)
//        drawRectMiddle()
//        drawRectTop()
    }

    private fun drawRectBottom(canvas: Canvas) {
        val left = -(radius - width) / 2
        val top = -(radius - height) / 2
        val rect = Rect(left, top, radius, radius)
        val paint = Paint().apply {
            shader = LinearGradient(0f, 0f, radius.toFloat(), radius.toFloat(), intArrayOf(
                    palette?.getDominantColor(Color.WHITE) ?: Color.WHITE, Color.WHITE
                ), null, Shader.TileMode.CLAMP
            )
        }
        canvas.withRotation(rotatedAngle, (width / 2).toFloat(), (height / 2).toFloat()) { drawRect(rect, paint) }
    }

    private fun drawRectTop() {
        val canvas = Canvas()
        val rect = Rect(radius, radius, radius, radius).apply {
            left = -(radius - width) / 2
            top = -(radius - height) / 2
        }
        val paint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, radius.toFloat(), intArrayOf(
                    palette?.getLightVibrantColor(Color.WHITE)?: Color.WHITE, palette?.getDarkVibrantColor(Color.BLACK)?: Color.BLACK
                ), null, Shader.TileMode.CLAMP
            )
            blendMode = BlendMode.DST_ATOP
        }
        canvas.withRotation(rotatedAngle, 0.5f, 0.5f) { drawRect(rect, paint) }
    }

    private fun drawRectMiddle() {
        val canvas = Canvas()
        val rect = Rect(radius, radius, radius, radius).apply {
            left = -(radius - width) / 2
            top = -(radius - height) / 2
        }
        val paint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, radius.toFloat(), intArrayOf(
                    Color.WHITE, Color.TRANSPARENT
                ), null, Shader.TileMode.CLAMP
            )
            blendMode = BlendMode.DST_OUT
        }
        canvas.withRotation(-rotatedAngle, 0.5f, 0.5f) { drawRect(rect, paint) }
    }
}