package com.example.gallery.main.music.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.palette.graphics.Palette
import com.example.gallery.R
import kotlin.math.sqrt

class FluidView(context: Context, attributeSet: AttributeSet? = null) : FrameLayout(context, attributeSet) {
    private val TAG = "FluidView"

    private val gradientBottomLayer =
        GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, intArrayOf(Color.WHITE, Color.GRAY))

    private val gradientMiddleLayer =
        GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(Color.WHITE, Color.GRAY))

    private val gradientTopLayer =
        GradientDrawable(GradientDrawable.Orientation.TR_BL, intArrayOf(Color.WHITE, Color.GRAY))

    private val viewBottom = View(context)
    private val viewMiddle = View(context)
    private val viewTop = View(context)

    init {
        addView(viewBottom)
        addView(viewMiddle)
        addView(viewTop)
        initLayers()
    }

    fun initBackground(bitmap: Bitmap) {
        val palette = Palette.from(bitmap).generate()
        gradientBottomLayer.colors = intArrayOf(palette.getDominantColor(Color.WHITE), Color.WHITE)
        gradientMiddleLayer.colors = intArrayOf(palette.getVibrantColor(Color.WHITE), palette.getLightVibrantColor(Color.WHITE))
        gradientTopLayer.colors = intArrayOf(palette.getDarkVibrantColor(Color.GRAY), palette.getDarkMutedColor(Color.GRAY))

        viewBottom.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_fluid_bottom).apply {
            repeatMode = Animation.RESTART
            repeatCount = Animation.INFINITE
        })
        viewMiddle.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_fluid_middle).apply {
            repeatMode = Animation.RESTART
            repeatCount = Animation.INFINITE
        })
        viewTop.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_fluid_top).apply {
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        })
    }

    private fun initLayers() {
        val viewBottomRadius = sqrt((width * width + height * height).toDouble()).toInt()
        (viewBottom.layoutParams as LayoutParams).apply {
            width = viewBottomRadius
            height = viewBottomRadius
        }.apply {
            background = gradientBottomLayer
            top = -(viewBottomRadius - height) / 2
            left = -(viewBottomRadius - width) / 2
        }
        val viewMiddleRadius = sqrt((width * width + height * height).toDouble()).toInt() * 3 / 2
        (viewMiddle.layoutParams as LayoutParams).apply {
            width = viewMiddleRadius
            height = viewMiddleRadius
            gravity = Gravity.CENTER
        }.apply {
            background = gradientMiddleLayer
            top = -(viewMiddleRadius - height) / 2
            left = -(viewMiddleRadius - width) / 2
            alpha = 0.4f
        }
        viewMiddle.visibility = GONE
        val viewTopRadius = sqrt((width * width + height * height).toDouble()).toInt()
        (viewTop.layoutParams as LayoutParams).apply {
            width = viewTopRadius
            height = viewTopRadius
            gravity = Gravity.CENTER
        }.apply {
            background = gradientTopLayer
            top = -(viewBottomRadius - height) / 2
            left = -(viewBottomRadius - width) / 2
            alpha = 0.4f
        }
        viewTop.visibility = GONE
    }
}