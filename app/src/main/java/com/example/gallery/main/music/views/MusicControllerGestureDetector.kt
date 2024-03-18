package com.example.gallery.main.music.views

import LogUtil
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
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
    context: Context,
    private val viewModel: MusicViewModel
) : SimpleOnGestureListener() {
    private val TAG = "onTouchEvent"
    private var controllerAnimator = ValueAnimator()
    private val collapsedHeight = 88.dp
    private val deltaHeight = ScreenUtils.getScreenHeight() - collapsedHeight
    private val gestureDetector = GestureDetector(context, this)
    var onSingleTapListener: (() -> Boolean) = { false }

    fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        val isEventConsumed =  gestureDetector.onTouchEvent(event)
        val isActionUp = event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_OUTSIDE
        val isScrolling = viewModel.controllerState.value == ControllerState.EXPENDING || viewModel.controllerState.value == ControllerState.COLLAPSING
        return if (!isEventConsumed && isScrolling && isActionUp) onActionUp()
        else isEventConsumed
    }

    private fun updateOffset(offset: Float) {
        if (offset < 0 || offset > 1) return
        viewModel.updateControllerOffset(offset)
    }

    private fun updateState(state: ControllerState) {
        if (state == viewModel.controllerState.value) return
        viewModel.updateControllerState(state)
    }

    override fun onSingleTapUp(e: MotionEvent) = onSingleTapListener.invoke()

    override fun onDown(e: MotionEvent): Boolean {
        controllerAnimator.cancel()
        LogUtil.d(TAG, "onDown, rawY: ${e.rawY}")
        return true
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        if (viewModel.controllerState.value == ControllerState.EXPENDED) updateState(ControllerState.COLLAPSING)
        else if (viewModel.controllerState.value == ControllerState.COLLAPSED) updateState(ControllerState.EXPENDING)
        val deltaOffset = (e1.y - e2.y) / deltaHeight
        LogUtil.i(TAG, "deltaHeight: $deltaHeight")
        LogUtil.i(TAG, "e1.y: ${e1.y}, e2.y: ${e2.y}, deltaOffset: $deltaOffset")
        updateOffset(viewModel.controllerOffset.value!! + deltaOffset)
        return true
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        LogUtil.i(TAG, "onFling, e1: ${e1.y}, e2: ${e2.y}, velocityY: $velocityY")
        val isScrollUp = if (e2.y - e1.y < 0) true else false
        return if (isScrollUp) expandController()
        else collapseController()
    }

    private fun onActionUp(): Boolean {
        if (viewModel.controllerState.value == ControllerState.EXPENDING) expandController()
        else if (viewModel.controllerState.value == ControllerState.COLLAPSING) collapseController()
        return false
    }

    fun collapseController(): Boolean {
        if (viewModel.controllerState.value == ControllerState.COLLAPSED) return false
        controllerAnimator.cancel()
        controllerAnimator = ObjectAnimator.ofFloat(viewModel.controllerOffset.value!!, 0f).apply {
            duration = 500
            interpolator = bezierInterpolator
            addUpdateListener { updateOffset(this.animatedValue as Float) }
            doOnEnd { updateState(ControllerState.COLLAPSED) }
            start()
        }
        return true
    }

    fun expandController(): Boolean {
        if (viewModel.controllerState.value == ControllerState.EXPENDED) return false
        controllerAnimator.cancel()
        updateState(ControllerState.EXPENDING)
        controllerAnimator = ObjectAnimator.ofFloat(viewModel.controllerOffset.value!!, 1f).apply {
            duration = 500
            interpolator = bezierInterpolator
            addUpdateListener { updateOffset(this.animatedValue as Float) }
            doOnEnd { updateState(ControllerState.EXPENDED) }
            start()
        }
        return true
    }
}