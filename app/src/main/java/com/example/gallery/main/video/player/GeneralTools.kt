package com.example.gallery.main.video.player

import com.blankj.utilcode.util.Utils
import kotlin.math.absoluteValue

object GeneralTools {
    fun millisecondToString(millisecond: Int, hasSpace: Boolean = false): String {
        val minute = (millisecond.absoluteValue.toLong() / 60000).toString().let { if (it.count() == 1) "0$it" else it }
        val second = (millisecond.absoluteValue.toLong() % 60000 / 1000).toString().let { if (it.count() == 1) "0$it" else it }
        return if (hasSpace) "$minute : $second" else "$minute:$second"
    }

    inline val Double.dp: Int get() = run {
        return toFloat().dp
    }
    inline val Int.dp: Int get() = run {
        return toFloat().dp
    }
    inline val Float.dp: Int get() = run {
        val scale: Float = Utils.getApp().resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }
}