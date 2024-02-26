package com.example.gallery.main.music.views

import LogUtil
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import androidx.core.animation.doOnEnd
import com.example.gallery.Constants.bezierInterpolator
import com.example.gallery.base.utils.AnimationUtils.onAnimationEnd
import com.example.gallery.base.utils.ViewUtils.dp
import com.example.gallery.base.utils.ViewUtils.setHeight
import com.example.gallery.base.utils.ViewUtils.setMargins
import com.example.gallery.databinding.ActivityMusicBinding
import com.example.gallery.main.music.enums.ControllerState

@SuppressLint("ClickableViewAccessibility")
class MusicControllerGestureDetector(
    private val binding: ActivityMusicBinding
) : SimpleOnGestureListener() {
    private val TAG = "MusicControllerGestureDetector"
    private var controllerAnimator = ValueAnimator()
    private var pivot = 0f
    private var offset: Float = 0f
    private var state: ControllerState = ControllerState.HIDDEN
    private val collapsedHeight = 88.dp
    private val deltaHeight = binding.root.height - collapsedHeight
    var offsetChangedListener: (offset: Float) -> Unit = {}
    var stateChangedListener: (state: ControllerState) -> Unit = {}

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return if (state == ControllerState.COLLAPSED) {
            updateState(ControllerState.EXPENDING)
            true
        } else false
    }

    override fun onDown(e: MotionEvent): Boolean {
        LogUtil.d(TAG, "onDown, rawY: ${e.rawY}")
        pivot = e.rawY
        return true
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        val deltaOffset = (e1.y - e2.y) / deltaHeight
        LogUtil.i(TAG, "e1.y: ${e1.y}, e2.y: ${e2.y}, deltaOffset: $deltaOffset")
        updateOffset(offset + deltaOffset)
        return true
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        controllerAnimator.cancel()
        LogUtil.i(TAG, "onFling, velocityY[$velocityY]")
        val startOffset = offset
        val endOffset = if (velocityY > 0) 0f else 1f
        controllerAnimator = ObjectAnimator.ofFloat(startOffset, endOffset).apply {
            duration = 500
            interpolator = bezierInterpolator
            addUpdateListener { updateOffset(this.animatedValue as Float) }
            doOnEnd { updateState(state = if (velocityY > 0) ControllerState.COLLAPSED else ControllerState.EXPENDED) }
            start()
        }
        return true
    }

    private fun updateOffset(offset: Float) {
        this.offset = offset
        offsetChangedListener(offset)
        changeControllerOffset(offset)
    }

    private fun changeControllerOffset(offset: Float) {
        val margin = (16f - 16f * offset).dp
        val expendedHeight = binding.root.height
        val collapsedHeight = 88.dp
        val height = collapsedHeight + (expendedHeight - collapsedHeight) * offset
        binding.cardView.setMargins(margin, margin, margin, margin)
        binding.cardView.setHeight(height.toInt())
    }

    fun updateState(state: ControllerState) {
        when (state) {
            ControllerState.EXPENDING -> {
                ObjectAnimator.ofFloat(0f, 1f).apply {
                    duration = 500
                    interpolator = bezierInterpolator
                    addUpdateListener { updateOffset(this.animatedValue as Float) }
                    doOnEnd { updateState(ControllerState.EXPENDED) }
                    start()
                }
            }

            ControllerState.COLLAPSING -> {
                ObjectAnimator.ofFloat(1f, 0f).apply {
                    duration = 320
                    interpolator = bezierInterpolator
                    addUpdateListener { updateOffset(this.animatedValue as Float) }
                    doOnEnd { updateState(ControllerState.COLLAPSED) }
                    start()
                }
            }

            ControllerState.SHOWING -> {
                if (this.state == ControllerState.HIDDEN)
                    binding.root.animate().alphaBy(binding.root.alpha).alphaBy(1f).translationYBy(0.5f)
                        .translationY(1f).setDuration(300)
                        .onAnimationEnd { updateState(ControllerState.COLLAPSED) }
                        .start()
            }

            ControllerState.HIDDEN -> binding.root.alpha = 0f

            else -> {}
        }
        this.state = state
        stateChangedListener(state)
    }
}