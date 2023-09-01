package com.example.gallery.main.music.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
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

    private val binding: ViewFluidBinding by lazy {
        DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.view_fluid,
            this,
            true
        )
    }

    fun initBackground(bitmap: Bitmap) {
        val palette = Palette.from(bitmap).generate()
        binding.fluidBottom.background = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            intArrayOf(
                palette.getDarkVibrantColor(Color.WHITE),
                palette.getDarkMutedColor(Color.WHITE)
            )
        )
        binding.fluidBottom.layoutParams.apply {
            val radius =
                sqrt((this@FluidView.width * this@FluidView.width + this@FluidView.height * this@FluidView.height).toDouble()).toInt()
            width = radius
            height = radius
        }
        val animationBottom = AnimationUtils.loadAnimation(context, R.anim.anim_fluid_bottom).apply {
            repeatMode = Animation.RESTART
            repeatCount = Animation.INFINITE
        }
        binding.fluidBottom.startAnimation(animationBottom)

        binding.fluidMiddle.background = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                palette.getLightVibrantColor(Color.WHITE),
                palette.getLightMutedColor(Color.WHITE)
            )
        )
        binding.fluidMiddle.layoutParams.apply {
            val radius =
                sqrt((this@FluidView.width * this@FluidView.width * 4 / 9 + this@FluidView.height * this@FluidView.height * 4 / 9).toDouble()).toInt() * 2
            this.width = radius
            this.height = radius
        }
        val animationMiddle = AnimationUtils.loadAnimation(context, R.anim.anim_fluid_middle).apply {
            repeatMode = Animation.RESTART
            repeatCount = Animation.INFINITE
        }
        binding.fluidMiddle.startAnimation(animationMiddle)

        binding.fluidTop.background = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                palette.getVibrantColor(Color.WHITE),
                palette.getMutedColor(Color.WHITE)
            )
        )
        val animationTop = AnimationUtils.loadAnimation(context, R.anim.anim_fluid_top).apply {
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        binding.fluidTop.startAnimation(animationTop)
    }
}