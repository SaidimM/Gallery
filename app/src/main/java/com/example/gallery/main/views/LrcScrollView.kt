package com.example.gallery.main.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.example.gallery.media.remote.lyrics.Lyric

class LrcScrollView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttrs: Int) : super(
        context,
        attributeSet,
        defStyleAttrs
    )

    private var scrollView: ScrollView = ScrollView(context)

    private val linearLayout = LinearLayout(context)

    init {
        linearLayout.orientation = LinearLayout.VERTICAL
        scrollView.addView(linearLayout)
        scrollView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        this.addView(scrollView)
    }

    private var texts: ArrayList<TextView> = arrayListOf()

    var data: ArrayList<Lyric> = arrayListOf()
        set(value) {
            field.clear()
            field.addAll(value)
            field.forEach {
                val textView = TextView(context)
                textView.text = it.text
                textView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                linearLayout.addView(textView)
                texts.add(textView)
            }
            scrollView.invalidate()
        }
}