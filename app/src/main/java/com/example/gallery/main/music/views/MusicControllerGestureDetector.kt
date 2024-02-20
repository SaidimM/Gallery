package com.example.gallery.main.music.views

import LogUtil
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import androidx.core.animation.doOnEnd
import com.example.gallery.main.music.enums.ControllerState
import com.example.gallery.main.music.viewModels.MusicViewModel

class MusicControllerGestureDetector(private val viewModel: MusicViewModel) : SimpleOnGestureListener() {
    private val TAG = "MusicControllerGestureDetector"
    private var controllerAnimator = ValueAnimator()
    private var pivot = 0f

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        return if (viewModel.controllerState.value == ControllerState.COLLAPSED) {
            viewModel.updateController(state = ControllerState.EXPENDING)
            true
        } else false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        LogUtil.d(TAG, "onDown, rawY: ${e?.rawY}")
        pivot = e?.rawY ?: 0f
        return true
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        if (e2 == null || e1 == null) return false
        LogUtil.d(TAG, "onScroll, rawY: ${e2.rawY}, action1: ${e1.action}, action2: ${e2.action}")
//                val offset = abs(e2.rawY - pivot) / (binding.root.height - 88.dp)
        val offset = viewModel.controllerOffset.value!! + (distanceY / (binding.root.height - 88.dp).toFloat())
        LogUtil.d(TAG, "scale: $offset")
        onControllerSlides(offset)
        return true
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        controllerAnimator.cancel()
        LogUtil.d(TAG, "onFling, velocityY[$velocityY]")
        if (e1 == null) return false
        val startOffset = viewModel.controllerOffset.value!!
        val endOffset = if (velocityY > 0) 0f else 1f
        controllerAnimator = ObjectAnimator.ofFloat(startOffset, endOffset).apply {
//                    duration = (binding.root.height * 100 / abs(velocityY)).toLong()
            duration = 500
            interpolator = bezierInterpolator
            addUpdateListener { onControllerSlides(this.animatedValue as Float) }
            doOnEnd { viewModel.updateController(state = if (velocityY > 0) ControllerState.COLLAPSED else ControllerState.EXPENDED) }
            start()
        }
        return true
    }
}