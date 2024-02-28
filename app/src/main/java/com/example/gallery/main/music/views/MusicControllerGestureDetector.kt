package com.example.gallery.main.music.views

import LogUtil
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.core.animation.doOnEnd
import com.example.gallery.Constants.bezierInterpolator
import com.example.gallery.base.utils.AnimationUtils.setListeners
import com.example.gallery.base.utils.ViewUtils.dp
import com.example.gallery.base.utils.ViewUtils.setHeight
import com.example.gallery.base.utils.ViewUtils.setMargins
import com.example.gallery.databinding.ActivityMusicBinding
import com.example.gallery.main.music.enums.ControllerState
import com.example.gallery.main.music.viewModels.MusicViewModel

@SuppressLint("ClickableViewAccessibility")
class MusicControllerGestureDetector(
    private val binding: ActivityMusicBinding,
    private val viewModel: MusicViewModel
) : SimpleOnGestureListener() {
    private val TAG = "MusicControllerGestureDetector"
    private var controllerAnimator = ValueAnimator()
    private var pivot = 0f
    private var offset: Float = 0f
    private var state: ControllerState = ControllerState.HIDDEN
    private val startMargin = 8.dp
    private val collapsedHeight = 88.dp
    private val deltaHeight = binding.root.height - collapsedHeight

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
        LogUtil.i(TAG, "deltaHeight: $deltaHeight")
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
        if (offset < 0) return
        changeControllerOffset(offset)
        this.offset = offset
        viewModel.updateControllerOffset(offset)
    }

    fun updateState(state: ControllerState) {
        changeControllerState(state)
        this.state = state
        viewModel.updateControllerState(state)
    }

    private fun changeControllerOffset(offset: Float) {
        if (offset < 0) return
        val margin = (startMargin - startMargin * offset).toInt()
        val expendedHeight = binding.root.height
        val collapsedHeight = 88.dp
        val height = collapsedHeight + (expendedHeight - collapsedHeight) * offset
        binding.cardView.setMargins(margin, margin, margin, margin)
        binding.cardView.setHeight(height.toInt())
    }

    private fun changeControllerState(state: ControllerState) {
        LogUtil.d(TAG, "updateState, state: $state")
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
                LogUtil.d(TAG, "this state, state: $state")
                binding.cardView.animate().alphaBy(0f).alpha(1f).setDuration(1000)
                    .setListeners(onStart = { binding.cardView.visibility = View.VISIBLE },
                        onEnd = { this.state = ControllerState.COLLAPSED }).start()
            }

            ControllerState.HIDDEN -> binding.cardView.alpha = 0f

            else -> {}
        }
    }

    fun backPressed(): Boolean {
        if (state == ControllerState.EXPENDED) {
            updateState(ControllerState.COLLAPSING)
            return true
        } else {
            return false
        }
    }
}