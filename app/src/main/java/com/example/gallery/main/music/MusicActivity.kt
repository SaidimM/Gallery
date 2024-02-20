package com.example.gallery.main.music

import LogUtil
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.animation.doOnEnd
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.base.utils.AnimationUtils.onAnimationEnd
import com.example.gallery.base.utils.ViewUtils.setHeight
import com.example.gallery.base.utils.ViewUtils.setMargins
import com.example.gallery.databinding.ActivityMusicBinding
import com.example.gallery.main.music.enums.ControllerState
import com.example.gallery.main.music.viewModels.MusicViewModel
import com.example.gallery.main.music.views.EaseCubicInterpolator
import kotlin.math.abs


class MusicActivity : BaseActivity() {
    private val viewModel: MusicViewModel by lazy { getActivityScopeViewModel(MusicViewModel::class.java) }
    override val binding: ActivityMusicBinding by lazy { ActivityMusicBinding.inflate(layoutInflater) }
    private val bezierInterpolator = EaseCubicInterpolator(0.25f, 0.25f, 0.15f, 1f)
    private val controllerAnimator = ObjectAnimator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
    }

    private fun initData() {
        viewModel.loadMusic()
        viewModel.getLastPlayedMusic()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        title = getString(R.string.music)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.cardView.setOnTouchListener { v, event -> gestureDetector.onTouchEvent(event) }
    }

    override fun onStop() {
        viewModel.recyclePlayer()
        super.onStop()
    }

    override fun onBackPressed() {
        if (viewModel.controllerState.value == ControllerState.EXPENDED || viewModel.controllerState.value == ControllerState.EXPENDING)
            viewModel.updateController(ControllerState.COLLAPSING)
        else super.onBackPressed()
    }

    override fun observe() {
        viewModel.music.observe(this) {
            binding.cardView.visibility = View.VISIBLE
            binding.fragmentList.setPadding(0, 0, 0, binding.cardView.height)
        }
        viewModel.controllerState.observe(this) { observeControllerState(it) }
        viewModel.controllerOffset.observe(this) { onControllerSlides(it) }
    }

    private fun observeControllerState(state: ControllerState) {
        when (state) {
            ControllerState.SHOWING -> {
                binding.cardView.animate().alphaBy(binding.cardView.alpha).alphaBy(1f).translationYBy(0.5f)
                    .translationY(1f).setDuration(300)
                    .onAnimationEnd { viewModel.updateController(state = ControllerState.COLLAPSED) }.start()
            }

            ControllerState.EXPENDING -> {
                ObjectAnimator.ofFloat(0f, 1f).apply {
                    duration = 500
                    interpolator = bezierInterpolator
                    addUpdateListener { viewModel.updateController(offset = this.animatedValue as Float) }
                    doOnEnd { viewModel.updateController(state = ControllerState.EXPENDED) }
                    start()
                }
            }

            ControllerState.COLLAPSING -> {
                ObjectAnimator.ofFloat(1f, 0f).apply {
                    duration = 320
                    interpolator = bezierInterpolator
                    addUpdateListener { viewModel.updateController(offset = this.animatedValue as Float) }
                    doOnEnd { viewModel.updateController(state = ControllerState.COLLAPSED) }
                    start()
                }
            }

            ControllerState.HIDDEN -> binding.cardView.alpha = 0f

            else -> {}
        }
    }

    private fun onControllerSlides(offset: Float) {
        val margin = (16f - 16f * offset).dp
        val expendedHeight = binding.root.height
        val collapsedHeight = 88.dp
        val height = collapsedHeight + (expendedHeight - collapsedHeight) * offset
        binding.cardView.setMargins(margin, margin, margin, margin)
        binding.cardView.setHeight(height.toInt())
    }

    private val gestureDetector by lazy {
        GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
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
                if (e2 ==  null || e1 == null) return false
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
        })
    }

}