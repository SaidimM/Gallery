package com.example.gallery.player

import kotlin.math.absoluteValue

object GeneralTools {
    fun millisecondToString(millisecond: Int, hasSpace: Boolean = false): String {
        val minute = (millisecond.absoluteValue.toLong() / 60000).toString().let { if (it.count() == 1) "0$it" else it }
        val second = (millisecond.absoluteValue.toLong() % 60000 / 1000).toString().let { if (it.count() == 1) "0$it" else it }
        return if (hasSpace) "$minute : $second" else "$minute:$second"
    }
}