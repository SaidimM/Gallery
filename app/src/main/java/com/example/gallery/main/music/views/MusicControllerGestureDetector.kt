package com.example.gallery.main.music.views

import LogUtil
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.WindowManager
import androidx.core.animation.doOnEnd
import com.example.gallery.base.utils.AnimationUtils.onAnimationEnd
import com.example.gallery.base.utils.ViewUtils.dp
import com.example.gallery.base.utils.ViewUtils.setHeight
import com.example.gallery.base.utils.ViewUtils.setMargins
import com.example.gallery.base.utils.ViewUtils.setWidth
import com.example.gallery.databinding.FragmentPlayerBinding
import com.example.gallery.main.music.enums.ControllerState
import com.example.gallery.main.music.enums.PlayerViewState

@SuppressLint("ClickableViewAccessibility")
class MusicControllerGestureDetector(
    private val binding: FragmentPlayerBinding
) : SimpleOnGestureListener() {
    private val TAG = "MusicControllerGestureDetector"
    private var controllerAnimator = ValueAnimator()
    private val bezierInterpolator = EaseCubicInterpolator(0.25f, 0.25f, 0.15f, 1f)
    private var pivot = 0f
    private var offset: Float = 0f
    var state: ControllerState = ControllerState.HIDDEN
    var playerViewState: PlayerViewState = PlayerViewState.ALBUM
    var offsetChangedListener: (offset: Float) -> Unit = {}
    var stateChangedListener: (state: ControllerState) -> Unit = {}

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        return if (state == ControllerState.COLLAPSED) {
            ObjectAnimator.ofFloat(0f, 1f).apply {
                duration = 500
                interpolator = bezierInterpolator
                addUpdateListener { updateOffset(this.animatedValue as Float) }
                doOnEnd { updateState(ControllerState.EXPENDED) }
                start()
            }
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
        val offset = offset + (distanceY / (binding.root.height - 88.dp).toFloat())
        updateOffset(offset)
        return true
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        controllerAnimator.cancel()
        LogUtil.d(TAG, "onFling, velocityY[$velocityY]")
        if (e1 == null) return false
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
        onSheetSlides(offset)
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

    private fun updatePlayerViewState(state: PlayerViewState) {
        this.playerViewState = state
        when (state) {
            PlayerViewState.ALBUM -> {
                ObjectAnimator.ofFloat(0f, 1f).apply {
                    interpolator = bezierInterpolator
                    duration = 500
                    addUpdateListener { animator -> changeAlbumCoverOffset(animator.animatedValue as Float) }
                    start()
                }
                binding.lyricsView.clearAnimation()
                binding.lyricsView.animate().alphaBy(1f).alpha(0f).setDuration(500).start()
                binding.songName.animate().alphaBy(0f).alpha(1f)
                    .translationYBy(4.dp.toFloat()).translationY(0f)
                    .setInterpolator(bezierInterpolator).setDuration(500).start()
                binding.singerName.animate().alphaBy(0f).alpha(1f)
                    .translationYBy(4.dp.toFloat()).translationY(0f)
                    .setInterpolator(bezierInterpolator).setDuration(500).start()
            }

            PlayerViewState.LYRICS -> {
                ObjectAnimator.ofFloat(1f, 0f).apply {
                    interpolator = bezierInterpolator
                    duration = 380
                    addUpdateListener { animator -> changeAlbumCoverOffset(animator.animatedValue as Float) }
                    start()
                }
                binding.lyricsView.clearAnimation()
                binding.lyricsView.animate().alphaBy(0f).alpha(1f).setDuration(200).start()
                binding.songName.animate().alphaBy(1f).alpha(0f)
                    .translationYBy(0f).translationY(8.dp.toFloat())
                    .setInterpolator(bezierInterpolator).setDuration(320).start()
                binding.singerName.animate().alphaBy(1f).alpha(0f)
                    .translationYBy(0f).translationY(8.dp.toFloat())
                    .setInterpolator(bezierInterpolator).setDuration(320).start()
            }

            else -> {}
        }
    }

    private fun onSheetSlides(slideOffset: Float) {
        changeControllerOffset(slideOffset)
        changeSliderOffset(slideOffset)
        changeAlbumCoverOffset(slideOffset)
    }

    private fun changeControllerOffset(offset: Float) {
        val margin = (16f - 16f * offset).dp
        val windowManager = binding.root.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        windowManager.defaultDisplay.getSize(point)
        val expendedHeight = point.y
        val collapsedHeight = 88.dp
        val height = collapsedHeight + (expendedHeight - collapsedHeight) * offset
        binding.cardView.setMargins(margin, margin, margin, margin)
        binding.cardView.setHeight(height.toInt())
    }

    private fun changeSliderOffset(offset: Float) {
        val marginEnd = (96.dp * offset).toInt()
        binding.button.alpha = offset
        binding.musicName.setMargins(start = 88.dp, end = marginEnd)
        binding.root.setPadding(0, (32.dp * offset).toInt(), 0, 0)
    }

    private fun changeAlbumCoverOffset(offset: Float) {
        LogUtil.i(TAG, "offset: $offset")
        val albumWidth = 56 + (320 - 56) * offset
        val marginTop = 56 * offset
        val marginStart = 16.dp + ((binding.root.width - 320.dp) / 2 - 16.dp) * offset
        binding.album.setWidth(albumWidth.dp)
        binding.album.setHeight(albumWidth.dp)
        binding.album.setMargins(marginStart.toInt(), marginTop.dp)
        binding.musicName.alpha = ((1 - offset) * (1 - offset))
        binding.album.radius = 8 + (16 - 6).dp.toFloat() * offset
    }
}