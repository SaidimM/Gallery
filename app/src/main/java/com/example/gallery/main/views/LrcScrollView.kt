package com.example.gallery.main.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

class LrcScrollView: ScrollView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttrs: Int) : super(
        context,
        attributeSet,
        defStyleAttrs
    )
}