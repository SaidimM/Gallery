package com.example.gallery.main.music.lyric

import LogUtil
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.doOnLayout
import androidx.core.widget.NestedScrollView
import com.example.gallery.R
import com.example.gallery.base.utils.ViewUtils.dp
import com.example.gallery.base.utils.ViewUtils.px
import com.example.gallery.media.music.remote.lyrics.Lyric
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

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
            clearAnimation()
            linearLayout.removeAllViewsInLayout()
            index = 0
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
            measure(MeasureSpec.EXACTLY, MeasureSpec.EXACTLY)
            layout(left, top, right, bottom)
            scrollView.setOnScrollChangeListener { _, _, _, _, _ -> setTextsAlpha() }
        }

    fun setDragListener(afterFrag: () -> Boolean) = scrollView.setOnDragListener { _, _ -> afterFrag() }

    @OptIn(FlowPreview::class)
    suspend fun start(position: Int = 0) {
        data.find { position >= it.position && position < it.endPosition }?.let { this.index = data.indexOf(it) }
        data.subList(index, data.size - 1).asFlow()
            .onEach { delay(it.endPosition.toLong() - it.position) }
            .flatMapConcat {
                flow {
                    LogUtil.d(TAG, it.toString())
                    index = data.indexOf(it)
                    LogUtil.d(TAG, "index: $index")
                    if (index == -1) return@flow
                    emit(index)
                }
            }.catch {
                LogUtil.e(TAG, it.toString())
            }.collect {
                coroutineScope {
                    launch(Dispatchers.Main) {
                        scrollToIndex(it)
                        animateIndexText(it)
                        alphaAnimateTexts(it)
                    }
                }
            }
    }

    private fun scrollToIndex(index: Int) {
        if (offsets.size <= index) return
        this.index = index
        val offset = offsets[index]
        if (offset <= 32.dp) return
        ObjectAnimator.ofInt(scrollView.scrollY, offsets[index] - 16.dp).apply {
            setDuration(400)
            addUpdateListener { scrollView.scrollY = it.animatedValue as Int }
            interpolator = DecelerateInterpolator()
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

    private fun setTextsAlpha() {
        val displayHeight = scrollView.height
        var deltaHeight = displayHeight
        var deltaIndex = offsets.findLast { scrollView.scrollY - it > 0 }?.let { offsets.indexOf(it) } ?: 0
        while (deltaHeight > 0) {
            val textView = texts[deltaIndex]
            textView.alpha = 0.4f * (deltaHeight.toFloat() / displayHeight)
            deltaHeight -= textView.height
            deltaIndex++
        }
    }

    private fun alphaAnimateTexts(index: Int) {
        if (index != 0) {
            val preText = texts[index - 1]
            preText.animate().alphaBy(1f).alpha(0.2f).setDuration(200).start()
        }
        texts[index].animate().alphaBy(0.4f).alpha(1f).start()
    }
}