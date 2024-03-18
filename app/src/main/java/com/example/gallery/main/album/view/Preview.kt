package com.example.gallery.main.album.view

import LogUtil
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.VelocityTracker
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.gallery.base.utils.GeneralUtils
import com.example.gallery.main.music.views.EaseCubicInterpolator
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequestBuilder

@SuppressLint("ClickableViewAccessibility")
class Preview : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(
        context: Context,
        attributeSet: AttributeSet,
        defStyleAttrs: Int,
        defStyleRes: Int
    ) : super(context, attributeSet, defStyleAttrs, defStyleRes)

    private val TAG = "Preview"
    private var scale = 1f
    private val DOUBLE_TAP_SCALE = 3f
    private var isLongImage = false
    private val imageView: SimpleDraweeView by lazy { SimpleDraweeView(context) }
    private val bezierInterpolator = EaseCubicInterpolator(0.25f, 0.25f, 0.15f, 1f)
    private val scaleGestureDetector: ScaleGestureDetector by lazy {
        ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                Log.d("onScale", "scaleFactor = " + detector.scaleFactor)
                if (detector.scaleFactor * scale < 1) return false
                scale *= detector.scaleFactor
                imageView.pivotX = detector.focusX
                imageView.pivotY = detector.focusY
                imageView.scaleX = scale
                imageView.scaleY = scale
                return true
            }
        })
    }

    private val gestureDetector: GestureDetector by lazy {
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                LogUtil.d(TAG, "on Double Tap occurred")
                val startFloat: Float
                val endFloat: Float
                val duration: Long
                if (imageView.scaleX >= DOUBLE_TAP_SCALE) {
                    duration = 500
                    startFloat = scale
                    endFloat = 1f
                } else if (scale < 1f) {
                    startFloat = scale
                    endFloat = 1f
                    duration = 500
                } else {
                    imageView.pivotX = e.x
                    imageView.pivotY = e.y
                    startFloat = scale
                    endFloat = DOUBLE_TAP_SCALE
                    duration = 800
                }
                ObjectAnimator.ofFloat(startFloat, endFloat).apply {
                    this.duration = duration
                    interpolator = bezierInterpolator
                    addUpdateListener {
                        scale = animatedValue as Float
                        imageView.scaleX = scale
                        imageView.scaleY = scale
                    }
                    start()
                }
                return true
            }

            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (!isLongImage && (imageView.left >= left || imageView.right <= right)) return false
                if (isLongImage && (imageView.top >= top || imageView.bottom <= bottom)) return false
                imageView.x -= distanceX
                imageView.y -= distanceY
                velocityTracker.addMovement(e2)
                velocityTracker.computeCurrentVelocity(100)
                LogUtil.d(TAG, "action: " + e2.action)
                LogUtil.d(
                    TAG,
                    "xVelocity: ${velocityTracker.xVelocity}, yVelocity: ${velocityTracker.yVelocity}"
                )
                return true
            }
        })
    }

    private val velocityTracker: VelocityTracker = VelocityTracker.obtain()

    init {
        setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            scaleGestureDetector.onTouchEvent(event)
            true
        }
    }

    fun setImage(uri: Uri) {
        val imageRequest = ImageRequestBuilder
            .newBuilderWithSource(uri)
            .setProgressiveRenderingEnabled(true)
            .build()
        val genericDraweeHierarchy = GenericDraweeHierarchyBuilder(context.resources).setActualImageScaleType(
            ScalingUtils.ScaleType.FIT_CENTER).build()
        imageView.controller = Fresco.newDraweeControllerBuilder()
            .setImageRequest(imageRequest)
            .build()
        imageView.hierarchy = genericDraweeHierarchy
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        addView(imageView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }
}