package com.example.gallery.base.utils

import kotlin.math.absoluteValue

object GeneralUtils {
    fun Int.millisecondToString(hasSpace: Boolean = false): String {
        val minute = (this.absoluteValue.toLong() / 60000).toString().let { if (it.count() == 1) "0$it" else it }
        val second = (this.absoluteValue.toLong() % 60000 / 1000).toString().let { if (it.count() == 1) "0$it" else it }
        return if (hasSpace) "$minute : $second" else "$minute:$second"
    }
}