package com.example.gallery.main.album.view

import LogUtil
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.VelocityTracker
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.gallery.R
import com.example.gallery.main.music.views.EaseCubicInterpolator
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequestBuilder
import java.util.*
import kotlin.properties.Delegates

@SuppressLint("ClickableViewAccessibility")
class Preview : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(
        context: Context,
        attributeSet: AttributeSet,
        defStyleAttrs: Int,
        defStyleRes: Int
    ) : super(context, attributeSet, defStyleAttrs, defStyleRes)

    companion object {
        private const val TAG = "Preview"
        private const val DOUBLE_TAP_SCALE = 3f
    }

    private var scale = 1f
    private var isLongImage = false
    private var pivotX: Float = 0f
    private var pivotY: Float = 0f
    private val imageView: SimpleDraweeView by lazy { SimpleDraweeView(context) }
    private val bezierInterpolator = EaseCubicInterpolator(0.25f, 0.25f, 0.15f, 1f)
    private val scaleGestureDetector: ScaleGestureDetector by lazy {
        ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                Log.d(TAG, "scaleFactor = " + detector.scaleFactor)
                if (detector.scaleFactor * scale < 1) return false
                scale *= detector.scaleFactor
                invalidate()
                Log.d(TAG, "scale = $scale")
                return false
            }
        })
    }

    private val gestureDetector: GestureDetector by lazy {
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                LogUtil.d(TAG, "on Double Tap occurred")
                if (imageView.animation?.hasEnded() == false) return false
                val startFloat: Float
                val duration: Long
                if (scale >= DOUBLE_TAP_SCALE) {
                    duration = 500
                    startFloat = scale
                    scale = 1f
                } else if (scale < 1f) {
                    startFloat = scale
                    scale = 1f
                    duration = 500
                } else {
                    startFloat = scale
                    scale = DOUBLE_TAP_SCALE
                    duration = 800
                    pivotX = e.x
                    pivotY = e.y
                }
                val animation = ScaleAnimation(startFloat, scale, startFloat, scale, pivotX, pivotY)
                animation.duration = duration
                animation.fillAfter = true
                animation.interpolator = bezierInterpolator
                imageView.startAnimation(animation)
                return true
            }

            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float,
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
        this.id = R.id.preview
        setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            scaleGestureDetector.onTouchEvent(event)
            if (scale != 1f) v.parent.requestDisallowInterceptTouchEvent(true)
            true
        }
    }

    fun setImage(uri: Uri) {
        val imageRequest = ImageRequestBuilder
            .newBuilderWithSource(uri)
            .setProgressiveRenderingEnabled(true)
            .build()
        imageView.controller = Fresco.newDraweeControllerBuilder()
            .setImageRequest(imageRequest)
            .build()
        imageView.hierarchy.actualImageScaleType = ScalingUtils.ScaleType.FIT_CENTER
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        layoutParams.topToTop = this.id
        layoutParams.bottomToBottom = this.id
        addView(imageView, layoutParams)
    }
}