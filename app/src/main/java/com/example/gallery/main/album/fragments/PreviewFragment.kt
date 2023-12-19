package com.example.gallery.main.album.fragments

import LogUtil
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.VelocityTracker
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.fragment.app.viewModels
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.base.utils.AnimationUtils.onAnimationEnd
import com.example.gallery.databinding.FragmentPreviewBinding
import com.example.gallery.main.album.models.AlbumItemModel
import com.example.gallery.main.album.viewModels.PreviewFragmentViewModel
import com.example.gallery.main.music.views.EaseCubicInterpolator
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequestBuilder


class PreviewFragment(
    private val imageItemModel: AlbumItemModel
) : BaseFragment() {
    private val viewModel: PreviewFragmentViewModel by viewModels()
    private var scale = 1f
    private val DOUBLE_TAP_SCALE = 3f
    private var isLongImage = false
    private val bezierInterpolator = EaseCubicInterpolator(0.25f, 0.25f, 0.15f, 1f)
    private val scaleGestureDetector: ScaleGestureDetector by lazy {
        ScaleGestureDetector(requireContext(), object : SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                if (detector == null) return false
                Log.d("onScale", "scaleFactor = " + detector.scaleFactor)
                if (detector.scaleFactor * scale < 1) return false
                scale *= detector.scaleFactor
                binding.imageHide.pivotX = detector.focusX
                binding.imageHide.pivotY = detector.focusY
                binding.imageHide.scaleX = scale
                binding.imageHide.scaleY = scale
                return true
            }
        })
    }

    private val gestureDetector: GestureDetector by lazy {
        GestureDetector(requireContext(), object : SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                if (e == null) return false
                LogUtil.d(TAG, "on Double Tap occurred")
                val startFloat: Float
                val endFloat: Float
                val duration: Long
                if (binding.imageHide.scaleX >= DOUBLE_TAP_SCALE) {
                    duration = 500
                    startFloat = scale
                    endFloat = 1f
                } else if (scale < 1f) {
                    startFloat = scale
                    endFloat = 1f
                    duration = 500
                } else {
                    binding.imageHide.pivotX = e.x
                    binding.imageHide.pivotY = e.y
                    startFloat = scale
                    endFloat = DOUBLE_TAP_SCALE
                    duration = 800
                }
                ObjectAnimator.ofFloat(startFloat, endFloat).apply {
                    this.duration = duration
                    interpolator = bezierInterpolator
                    addUpdateListener {
                        scale = animatedValue as Float
                        binding.imageHide.scaleX = scale
                        binding.imageHide.scaleY = scale
                    }
                    start()
                }
                return true
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent?,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (e1 == null || e2 == null) return false
                if (!isLongImage && (binding.imageHide.left >= requireView().left || binding.imageHide.right <= requireView().right)) return false
                if (isLongImage && (binding.imageHide.top >= requireView().top || binding.imageHide.bottom <= requireView().bottom)) return false
                binding.imageHide.x -= distanceX
                binding.imageHide.y -= distanceY
                velocityTracker.addMovement(e2)
                velocityTracker.computeCurrentVelocity(100)
                LogUtil.d(TAG, "action: " + e2.action)
                LogUtil.d(TAG, "xVelocity: ${velocityTracker.xVelocity}, yVelocity: ${velocityTracker.yVelocity}")
                return true
            }
        })
    }

    private val velocityTracker: VelocityTracker = VelocityTracker.obtain()

    override val binding: FragmentPreviewBinding by lazy {
        FragmentPreviewBinding.inflate(
            layoutInflater
        )
    }

    var onClick: () -> Unit = {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        binding.container.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            scaleGestureDetector.onTouchEvent(event)
            true
        }
        val uri = Uri.parse("file://${imageItemModel.path}")
        val imageRequest = ImageRequestBuilder
            .newBuilderWithSource(uri)
            .setProgressiveRenderingEnabled(true)
            .build()
        binding.imageHide.controller = Fresco.newDraweeControllerBuilder()
            .setImageRequest(imageRequest)
            .build()
        val animation = AlphaAnimation(0f, 1f)
        animation.duration = 300
        binding.container.startAnimation(animation)
    }

    fun onBackPressed(afterDispatch: () -> Unit) {
        val animation = AlphaAnimation(1f, 0f)
        animation.duration = 200
        animation.onAnimationEnd { afterDispatch() }
        binding.container.startAnimation(animation)
    }
}