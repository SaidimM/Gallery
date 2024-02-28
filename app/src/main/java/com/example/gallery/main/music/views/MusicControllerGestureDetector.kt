package com.example.gallery.main.music.views

import LogUtil
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import androidx.core.animation.doOnEnd
import com.blankj.utilcode.util.ScreenUtils
import com.example.gallery.Constants.bezierInterpolator
import com.example.gallery.base.utils.ViewUtils.dp
import com.example.gallery.main.music.enums.ControllerState
import com.example.gallery.main.music.viewModels.MusicViewModel

@SuppressLint("ClickableViewAccessibility")
class MusicControllerGestureDetector(
    private val viewModel: MusicViewModel
) : SimpleOnGestureListener() {
    private val TAG = "MusicControllerGestureDetector"
    private var controllerAnimator = ValueAnimator()
    private var state: ControllerState = ControllerState.HIDDEN
    private val collapsedHeight = 88.dp
    private val deltaHeight = ScreenUtils.getScreenHeight() - collapsedHeight

    private fun updateOffset(offset: Float) {
        if (offset < 0) return
        viewModel.updateControllerOffset(offset)
    }

    private fun updateState(state: ControllerState) {
        this.state = state
        viewModel.updateControllerState(state)
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        LogUtil.d(TAG, "onSingleTapUp")
        return if (state == ControllerState.COLLAPSED) {
            updateState(ControllerState.EXPENDING)
            true
        } else false
    }

    override fun onDown(e: MotionEvent): Boolean {
        LogUtil.d(TAG, "onDown, rawY: ${e.rawY}")
        return true
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        val deltaOffset = (e1.y - e2.y) / deltaHeight
        LogUtil.i(TAG, "deltaHeight: $deltaHeight")
        LogUtil.i(TAG, "e1.y: ${e1.y}, e2.y: ${e2.y}, deltaOffset: $deltaOffset")
        updateOffset(viewModel.controllerOffset.value!! + deltaOffset)
        return true
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        controllerAnimator.cancel()
        LogUtil.i(TAG, "onFling, velocityY[$velocityY]")
        val startOffset = viewModel.controllerOffset.value!!
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
}