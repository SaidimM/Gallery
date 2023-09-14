package com.example.gallery.main.music.lyric

import LogUtil
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.doOnLayout
import androidx.core.view.iterator
import androidx.core.widget.NestedScrollView
import com.example.gallery.R
import com.example.gallery.base.utils.ViewUtils.dp
import com.example.gallery.base.utils.ViewUtils.px
import com.example.gallery.media.remote.lyrics.Lyric
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

class LrcScrollView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttrs: Int) : super(
        context, attributeSet, defStyleAttrs
    )

    private val TAG = "LrcScrollView"

    private var scrollView: NestedScrollView = NestedScrollView(context)

    private val linearLayout = LinearLayout(context)

    private val offsets: ArrayList<Int> = arrayListOf()

    private val texts: ArrayList<TextView> = arrayListOf()

    private var index: Int = 0

    private var isFullDisplay = false

    private val bottomViewHeight = 96.dp

    private var defaultColor = Color.parseColor("#C4C4C4")

    init {
        setPadding(16.dp, 0, 16.dp, 0)
        linearLayout.orientation = LinearLayout.VERTICAL
        scrollView.addView(linearLayout)
        scrollView.descendantFocusability = ScrollView.FOCUS_BLOCK_DESCENDANTS
        scrollView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        this.addView(scrollView)
    }

    var data: ArrayList<Lyric> = arrayListOf()
        set(value) {
            field.clear()
            field.addAll(value)
            linearLayout.removeAllViews()
            field.forEach {
                val textView = TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    text = it.text
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
                    setTextColor(Color.WHITE)
                    setPadding(16.dp, 16.dp, 16.dp, 16.dp)
                    val font = ResourcesCompat.getFont(context, R.font.roboto_black)
                    alpha = 0.4f
                    setTypeface(font, Typeface.BOLD)
                }
                linearLayout.addView(textView)
                texts.add(textView)
            }
            val bottomView = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    scrollView.width, scrollView.height - texts.last().height
                )
                setPadding(0, 16.dp, 0, 16.dp)
                isClickable = false
            }
            linearLayout.addView(bottomView)
            linearLayout.doOnLayout {
                linearLayout.children.forEachIndexed { index, view ->
                    LogUtil.d(TAG, "index: $index, view height: ${view.height.px}, top: ${view.top.px}")
                    offsets.add(view.top)
                    texts.add(view as TextView)
                }
            }
        }

    suspend fun start(position: Int = 0) {
        data.find { position >= it.position && position < it.endPosition }?.let { this.index = data.indexOf(it) }
        data.subList(index, data.size - 1).asFlow().transform {
            LogUtil.d(TAG, it.toString())
            val delay = it.endPosition - it.position
            delay(delay.toLong())
            LogUtil.d(TAG, "delay: $delay")
            this@LrcScrollView.index = data.indexOf(it)
            emit(this@LrcScrollView.index)
            LogUtil.d(TAG, "index: ${this@LrcScrollView.index}")
        }.collect {
            coroutineScope {
                launch(Dispatchers.Main) {
                    scrollToIndex(it)
                    delay(250)
                    animateIndexText(it)
                    alphaAnimateTexts(it)
                }
            }
        }
    }

    private fun scrollToIndex(index: Int) {
        val offset = offsets[index]
        if (offset <= 32.dp) return
        ObjectAnimator.ofInt(scrollView.scrollY, offsets[index] - 32.dp).apply {
            setDuration(400)
            addUpdateListener { scrollView.scrollY = it.animatedValue as Int }
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun animateIndexText(index: Int) {
        if (index > 0) {
            val preText = texts[index - 1]
            ObjectAnimator.ofFloat(1.05f, 1f).apply {
                duration = 150
                addUpdateListener {
                    preText.pivotX = 0.2f
                    preText.pivotY = 0f
                    preText.scaleX = it.animatedValue as Float
                    preText.scaleY = it.animatedValue as Float
                }
                start()
            }
        }

        val textView = texts[index]
        ObjectAnimator.ofFloat(1f, 1.05f).apply {
            duration = 250
            addUpdateListener {
                textView.pivotX = 0.2f
                textView.pivotY = 0f
                textView.scaleX = it.animatedValue as Float
                textView.scaleY = it.animatedValue as Float
            }
            start()
        }
    }

    private fun alphaAnimateTexts(index: Int, isFullDisplay: Boolean = false) {
        if (index != 0) {
            val preText = texts[index - 1]
            preText.animate().alphaBy(1f).alpha(0.4f).setDuration(200).start()
        }
        texts[index].animate().alphaBy(0.4f).alpha(1f).start()

        this.isFullDisplay = isFullDisplay
        val displayHeight = scrollView.height - if (isFullDisplay) 0 else bottomViewHeight
        var deltaHeight = displayHeight
        var deltaIndex = index
        while (deltaHeight > 0) {
            val textView = texts[index]
            textView.alpha = 0.4f * (deltaHeight * displayHeight)
            deltaHeight -= textView.height
            deltaIndex ++
        }
    }
}