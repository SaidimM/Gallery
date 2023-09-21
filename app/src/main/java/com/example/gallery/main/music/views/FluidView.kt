package com.example.gallery.main.music.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.palette.graphics.Palette
import com.example.gallery.R
import com.example.gallery.databinding.ViewFluidBinding
import kotlin.math.sqrt

class FluidView(context: Context, attributeSet: AttributeSet? = null) : FrameLayout(context, attributeSet) {
    private val TAG = "FluidView"
    private lateinit var palette: Palette

    private val binding: ViewFluidBinding by lazy {
        DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.view_fluid, this, true
        )
    }

    private val radius by lazy { sqrt((this@FluidView.width * this@FluidView.width + this@FluidView.height * this@FluidView.height).toDouble()).toInt() }

    fun initBackground(bitmap: Bitmap) {
        palette = Palette.from(bitmap).generate()
        drawRectBottom()
        drawRectMiddle()
        drawRectTop()
    }

    private fun drawRectBottom() {
        binding.fluidBottom.background = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(
                palette.getDarkMutedColor(Color.WHITE), palette.getDarkVibrantColor(Color.WHITE)
            )
        ).apply { setBounds(left, top, (radius * 0.7f).toInt(), (radius * 0.7f).toInt()) }
        binding.fluidBottom.layoutParams.apply {
            width = radius
            height = radius
        }
        val animationBottom = AnimationUtils.loadAnimation(context, R.anim.anim_fluid_bottom).apply {
            repeatMode = Animation.RESTART
            repeatCount = Animation.INFINITE
        }
        binding.fluidBottom.startAnimation(animationBottom)
    }

    private fun drawRectMiddle() {
        binding.fluidMiddle.background = GradientDrawable(
            GradientDrawable.Orientation.TR_BL, intArrayOf(Color.WHITE, Color.TRANSPARENT)
        ).apply { setBounds(radius / 2, 0, radius / 2, radius) }
        binding.fluidMiddle.backgroundTintMode = PorterDuff.Mode.DST_OUT
        binding.fluidMiddle.layoutParams.apply {
            this.width = radius
            this.height = radius
        }
        val animationMiddle = AnimationUtils.loadAnimation(context, R.anim.anim_fluid_middle).apply {
            repeatMode = Animation.RESTART
            repeatCount = Animation.INFINITE
        }
        binding.fluidMiddle.startAnimation(animationMiddle)
    }

    private fun drawRectTop() {
        binding.fluidTop.background = GradientDrawable(
            GradientDrawable.Orientation.TR_BL, intArrayOf(
                palette.getVibrantColor(Color.WHITE), palette.getMutedColor(Color.WHITE)
            )
        ).apply { this.setBounds(radius, 0, radius, 0) }
        binding.fluidTop.layoutParams.apply {
            this.width = radius
            this.height = radius
        }
        binding.fluidTop.alpha = 0.5f
        binding.fluidTop.backgroundTintMode = PorterDuff.Mode.DST_ATOP
        val animationTop = AnimationUtils.loadAnimation(context, R.anim.anim_fluid_top).apply {
            repeatMode = Animation.RESTART
            repeatCount = Animation.INFINITE
        }
        binding.fluidTop.startAnimation(animationTop)
    }
}